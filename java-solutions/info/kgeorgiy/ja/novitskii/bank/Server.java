package info.kgeorgiy.ja.novitskii.bank;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.net.*;

/**
 * A Server class that starts the client-server communication
 *
 * @author Ilya Novitskiy
 * @version 1
 */

public final class Server {
    private final static int DEFAULT_PORT = 1099;
    public static Bank bank;

    public static void main(final String... args) {
        final int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;

        bank = new RemoteBank(port);
        try {
            // Создаем registry
            LocateRegistry.createRegistry(DEFAULT_PORT);
            // сообщаем слою удаленных ссылок о существовании объекта
            UnicastRemoteObject.exportObject(bank, port);
            // Кладем в registry ссылку на банк
            Naming.rebind("//localhost/bank", bank);
            System.out.println("Server started");
        } catch (final RemoteException e) {
            System.out.println("Cannot export object: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (final MalformedURLException e) {
            System.out.println("Malformed URL");
        }
    }

}
