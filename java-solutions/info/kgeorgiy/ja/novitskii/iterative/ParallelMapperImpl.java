package info.kgeorgiy.ja.novitskii.iterative;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public class ParallelMapperImpl implements ParallelMapper {

    private final List<Thread> threadPool;

    private final ThreadSafeQueue taskQueue;
    private Status threadsStatus = Status.ALL_OPENED;

    private enum Status {
        ALL_OPENED,
        ALL_CLOSED

    }

    private static class Counter {
        private int counter = 0;

        public synchronized void increment() {
            counter++;
            notifyAll();
        }

        public int getCounter() {
            return counter;
        }
    }

    private static class ThreadSafeQueue {

        private final ArrayDeque<Runnable> tasks;

        private Status status;

        public ThreadSafeQueue() {
            this.tasks = new ArrayDeque<>();
            this.status = Status.ALL_OPENED;
        }

        public synchronized void pushTask(Runnable task) {
            this.tasks.push(task);
            notify();
        }

        public synchronized Runnable popTask() throws InterruptedException {
            while (tasks.isEmpty() && status == Status.ALL_OPENED) {
                wait();
            }
            if (status == Status.ALL_CLOSED) {
                return null;
            }
            return tasks.pop();
        }

        public synchronized void close(Status status) {
            this.status = status;
            notifyAll();
        }
    }


    public ParallelMapperImpl(int threads) {
        this.taskQueue = new ThreadSafeQueue();
        this.threadPool = IntStream.range(0, threads).mapToObj(i -> new Thread(() -> {
            try {
                while (true) {
                    Runnable runner = this.taskQueue.popTask();
                    if (runner == null) {
                        break;
                    }
                    runner.run();
                }
            } catch (InterruptedException ignored) {
            }
        })).peek(Thread::start).toList();
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args)
            throws InterruptedException {
        final List<R> result = new ArrayList<>(Collections.nCopies(args.size(), null));
        final List<? extends T> copyArgs = args;
        final Counter countSetValues = new Counter();
        IntStream.range(0, args.size()).forEach(i -> taskQueue.pushTask(() -> {
            result.set(i, f.apply(copyArgs.get(i)));
            countSetValues.increment();
        }));
        synchronized (countSetValues) {
            while (countSetValues.getCounter() != args.size() && threadsStatus == Status.ALL_OPENED) {
                countSetValues.wait();
            }
        }
        return result;
    }

    @Override
    public void close() {
        if (threadsStatus == Status.ALL_OPENED) {
            threadsStatus = Status.ALL_CLOSED;
            taskQueue.close(Status.ALL_CLOSED);
            threadPool.forEach(Thread::interrupt);
        }
    }
}
