package info.kgeorgiy.ja.novitskii.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

public class HelloUDPClient implements HelloClient {
    private void tryToSendRequestAndGet(String host, byte[] byteMessage, int port, String requestMessage) {
        try (DatagramSocket localSocket = new DatagramSocket()) {
            localSocket.setSoTimeout(300);
            InetAddress address = InetAddress.getByName(host);
            String correctResponse = "Hello, " + requestMessage;
            String responseMessage = "";
            while (!correctResponse.equals(responseMessage)) {
                DatagramPacket sendPacket = new DatagramPacket(byteMessage, byteMessage.length, address, port);
                byte[] byteGetMessage = new byte[localSocket.getReceiveBufferSize()];
                DatagramPacket getPacket = new DatagramPacket(byteGetMessage, byteGetMessage.length);
                try {
                    localSocket.send(sendPacket);
                    localSocket.receive(getPacket);
                } catch (IOException ex) {
                    System.err.println(ex.getLocalizedMessage());
                }
                responseMessage = new String(getPacket.getData(), 0, getPacket.getLength(), StandardCharsets.UTF_8);
            }
            System.out.println(requestMessage);
            System.out.println(responseMessage);
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        final ExecutorService requestsExecutor = Executors.newFixedThreadPool(threads);
        Phaser ph = new Phaser(1);
        for (int i = 1; i <= threads; i++) {
            final int threadIndex = i;
            ph.register();
            requestsExecutor.execute(() -> {
                for (int j = 1; j <= requests; j++) {
                    final String requestMessage = prefix + threadIndex + "_" + j;
                    byte[] byteMessage = requestMessage.getBytes(StandardCharsets.UTF_8);
                    tryToSendRequestAndGet(host, byteMessage, port, requestMessage);
                }
                ph.arrive();
            });
        }
        ph.arriveAndAwaitAdvance();
        requestsExecutor.shutdown();
        try {
            requestsExecutor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            System.err.println(e.getLocalizedMessage());
        }
    }

    public static void main(String[] args) {
        if (args == null) {
            System.err.println("args is null and we can't start Client");
            return;
        } else if (args.length < 5) {
            System.err.println("Not enough parameters given");
            return;
        }
        String host = null;
        int port = -1;
        String prefix = null;
        int threads = 0;
        int requests = 0;
        if (args[0] == null || args[2] == null) {
            System.err.println("Host or prefix are not provided");
            return;
        }
        host = args[0];
        prefix = args[2];
        try {
            port = Integer.parseInt(args[1]);
            threads = Integer.parseInt(args[3]);
            requests = Integer.parseInt(args[4]);
        } catch (NumberFormatException ex) {
            System.err.println("1-3-4 parameters should all be Integer");
        }

        HelloUDPClient client = new HelloUDPClient();
        client.run(host, port, prefix, threads, requests);
    }
}
