package info.kgeorgiy.java.advanced.hello;

import info.kgeorgiy.java.advanced.base.BaseTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * Full tests for {@link HelloServer}.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class HelloServerTest extends BaseTest {
    public static final InetSocketAddress SOCKET_ADDRESS = new InetSocketAddress("localhost", 28800);
    public static final String REQUEST = HelloServerTest.class.getName();
    public static final int FLOOD_REQUESTS = 10_000;

    public HelloServerTest() {
    }

    @Test
    public void test01_singleRequest() throws IOException {
        testSocket(1, socket -> checkResponse(socket, REQUEST));
    }

    @Test
    public void test02_multipleClients() throws IOException {
        testServer(1, server -> {
            for (int i = 0; i < 10; i++) {
                client(REQUEST + i);
            }
        });
    }

    @Test
    public void test03_multipleRequests() throws IOException {
        testSocket(1, socket -> {
            for (int i = 0; i < 10; i++) {
                checkResponse(socket, REQUEST + i);
            }
        });
    }

    @Test
    public void test04_parallelRequests() throws IOException {
        testSocket(1, socket -> {
            final Set<String> responses = new HashSet<>();
            for (int i = 0; i < 10; i++) {
                final String request = REQUEST + i;
                responses.add(Util.response(request));
                send(socket, request);
            }
            for (int i = 0; i < 10; i++) {
                final String response = Util.receive(socket);
                Assertions.assertTrue(responses.remove(response), "Unexpected response " + response);
            }
        });
    }

    @Test
    public void test05_parallelClients() throws IOException {
        testServer(1, server -> parallel(10, () -> client(REQUEST)));
    }

    @Test
    public void test06_dos() throws IOException {
        testSocket(3, socket -> parallel(10, () -> {
            for (int i = 0; i < FLOOD_REQUESTS; i++) {
                send(socket, REQUEST);
            }
        }));
    }

    @Test
    public void test07_noDoS() throws IOException {
        testServer(5, server -> parallel(5, () -> {
            try (final DatagramSocket socket = new DatagramSocket(null)) {
                for (int i = 0; i < FLOOD_REQUESTS; i++) {
                    checkResponse(socket, REQUEST + i);
                }
            }
        }));
    }

    private static void testServer(final int threads, final ConsumerCommand<HelloServer, IOException> action) throws IOException {
        final long start = System.currentTimeMillis();
        try (final HelloServer server = createCUT()) {
            server.start(SOCKET_ADDRESS.getPort(), threads);
            action.run(server);
        }
    }

    private static void send(final DatagramSocket socket, final String request) throws IOException {
        Util.send(socket, request, SOCKET_ADDRESS);
    }

    private static void client(final String request) throws IOException {
        try (final DatagramSocket socket = new DatagramSocket(null)) {
            checkResponse(socket, request);
        }
    }

    public static void testSocket(final int workers, final ConsumerCommand<DatagramSocket, IOException> command) throws IOException {
        testServer(workers, server -> {
            try (final DatagramSocket socket = new DatagramSocket()) {
                command.run(socket);
            }
        });
    }

    private static void checkResponse(final DatagramSocket socket, final String request) throws IOException {
        final String response = Util.request(request, socket, SOCKET_ADDRESS);
        Assertions.assertEquals(Util.response(request), response, "Invalid response");
    }
}
