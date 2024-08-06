package info.kgeorgiy.ja.novitskii.bank;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Ilya Novitskiy
 * @version 1
 */

public final class Client {
    /**
     * Utility class.
     */
    private Client() {
    }

    /** Starts the work of client
     * @param args args[0] = name, args[1] = surname, args[2] = passport,
     *             args[3] = bankAccId, args[4] = moneyChange
     * @throws RemoteException
     */
    public static void main(final String... args) throws RemoteException {
        final Bank bank;
        try {
            // Stub, полученный от сервера
            bank = (Bank) Naming.lookup("//localhost/bank");
        } catch (final NotBoundException e) {
            System.out.println("Bank is not bound");
            return;
        } catch (final MalformedURLException e) {
            System.out.println("Bank URL is invalid");
            return;
        }

        if (args.length < 5) {
            System.out.println("Impossible to act with user: " +
                    "number of arguments should be 5");
            return;
        }
        if (Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.out.println("One of the arguments is not provided");
        }
        String name = args[0];
        String surname = args[1];
        String passportId = args[2];
        String accId = args[3];
        int change = 0;
        try {
            change = Integer.parseInt(args[4]);
        } catch (NumberFormatException ex) {
            System.out.println("You should provide the int number of money");
        }

        //
        Person user = bank.getPerson(passportId, name, surname, RemotePerson.class);
        if (user == null) {
            System.out.println("Creating account " + accId + " for person with passport " + passportId);
            user = bank.createUserPerson(name, surname, passportId, accId);
        }
        // I want let the client create an account by its own
        bank.createAccountForPerson(user.getPassportNumber(), accId);

        System.out.println("Passport id:" + passportId + " Account id: " + accId);
        System.out.println("Current balance: " + user.accountBalance(accId));
        System.out.println("Adding money");
        user.setAccountBalance(accId, user.accountBalance(accId) + change);
        System.out.println("Updated balance: " + user.accountBalance(accId));
    }
}
