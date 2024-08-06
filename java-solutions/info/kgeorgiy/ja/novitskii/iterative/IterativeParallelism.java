package info.kgeorgiy.ja.novitskii.iterative;

import info.kgeorgiy.java.advanced.iterative.NewScalarIP;
import info.kgeorgiy.java.advanced.iterative.ScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * IterativeParallelism class represents objects that can find any properties of List using several threads
 *
 * @author Ilya Novitskiy
 * @version 1.0
 */
public class IterativeParallelism implements ScalarIP, NewScalarIP {

    private ParallelMapper mapper = null;

    /**
     * Creates instance of {@link IterativeParallelism} class
     */
    public IterativeParallelism() {
    }

    public IterativeParallelism(ParallelMapper mapper) {
        this.mapper = mapper;
    }

    private <T, R> R action(int threads, List<? extends T> values, Function<Stream<? extends T>, R> functionPutter,
                            Function<Stream<R>, R> resultant, int step) throws InterruptedException {
        List<R> results = null;
        int threadsNumber = Math.min(Math.min(threads, values.size()), (values.size() + step - 1) / step);
        IntFunction<Integer> slice = j -> Math.min(j * values.size() / threadsNumber, values.size());
        if (mapper != null) {
            List<? extends Stream<? extends T>> args = IntStream.range(0, threadsNumber)
                    .mapToObj(i -> IntStream.iterate((slice.apply(i) + step - 1) / step * step,
                                    j -> j < slice.apply(i + 1), j -> j + step)
                            .mapToObj(values::get))
                    .toList();
            results = mapper.map(functionPutter, args);
        } else {
            final List<R> temp_results = new ArrayList<>(Collections.nCopies(threadsNumber, null));
            List<Thread> threadList = IntStream.range(0, threadsNumber).
                    mapToObj(i -> new Thread(() -> temp_results.set(i, functionPutter.apply(IntStream.iterate((slice.apply(i) + step - 1) / step * step,
                                    j -> j < slice.apply(i + 1), j -> j + step)
                            .mapToObj(values::get))))).peek(Thread::start).toList();
            for (Thread thread : threadList) {
                thread.join();
            }
            results = temp_results;
        }
        return resultant.apply(results.stream());
    }

    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator, int step)
            throws InterruptedException {
        return action(threads, values, stream -> stream.max(comparator).orElse(null), stream ->
                stream.max(comparator).orElse(null), step);
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator, int step)
            throws InterruptedException {
        return maximum(threads, values, comparator.reversed(), step);
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate, int step)
            throws InterruptedException {
        return action(threads, values, stream -> stream.allMatch(predicate), stream -> stream.allMatch(a -> a), step);
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate, int step)
            throws InterruptedException {
        return !all(threads, values, Predicate.not(predicate), step);
    }

    @Override
    public <T> int count(int threads, List<? extends T> values, Predicate<? super T> predicate, int step)
            throws InterruptedException {
        return action(threads, values, stream -> stream.filter(predicate).toList().size(), stream ->
                stream.reduce(0, Integer::sum), step);
    }
}
