package info.kgeorgiy.ja.novitskii.hello;

import info.kgeorgiy.java.advanced.hello.NewHelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HelloUDPServer implements NewHelloServer {
    private ExecutorService responseExecutor;
    List<DatagramSocket> getSocket = new ArrayList<>();

    @Override
    public void start(int threads, Map<Integer, String> ports) {
        responseExecutor = Executors.newFixedThreadPool(threads);
        int bufSize = 0;
        for (Map.Entry<Integer, String> portString : ports.entrySet()) {
            DatagramSocket getLocalSocket = null;
            try {
                getLocalSocket = new DatagramSocket(portString.getKey());
                getLocalSocket.setSoTimeout(150);
                bufSize = getLocalSocket.getReceiveBufferSize();
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
            getSocket.add(getLocalSocket);
        }
        for (int i = 0; i < threads; i++) {
            final byte[] getBuf = new byte[bufSize];
            final DatagramPacket respPacket = new DatagramPacket(getBuf, getBuf.length);
            responseExecutor.execute(() -> {
                while (getSocket.stream().anyMatch(datagramSocket -> !datagramSocket.isClosed())) {
                    for (DatagramSocket socket : getSocket) {
                        try {
                            socket.receive(respPacket);
                            final byte[] responseBuf = ports.get(socket.getLocalPort())
                                    .replaceAll("\\$",
                                            new String(respPacket.getData(), 0, respPacket.getLength(), StandardCharsets.UTF_8))
                                    .getBytes(StandardCharsets.UTF_8);
                            DatagramPacket sendPacket = new DatagramPacket(responseBuf, 0, responseBuf.length,
                                    respPacket.getAddress(), respPacket.getPort());
                            socket.send(sendPacket);
                        } catch (IOException e) {
                            System.err.println(e.getLocalizedMessage());
                        }
                    }
                }
            });
        }
    }

    @Override
    public void close() {
        responseExecutor.shutdown();
        getSocket.forEach(DatagramSocket::close);
        try {
            responseExecutor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    public static void main(String[] args) {
        if (args == null) {
            System.err.println("args is null and we can't start Client");
            return;
        } else if (args.length < 2) {
            System.err.println("Not enough parameters given");
            return;
        }
        int port = -1;
        int threads = 0;
        try {
            port = Integer.parseInt(args[0]);
            threads = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            System.err.println("1-2 parameters should all be Integer");
        }
        try (HelloUDPServer server = new HelloUDPServer()) {
            server.start(port, threads);
        }
    }
}
