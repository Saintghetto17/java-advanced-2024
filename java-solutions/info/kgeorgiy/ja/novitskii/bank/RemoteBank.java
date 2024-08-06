package info.kgeorgiy.ja.novitskii.bank;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The realisation of bank that is created for RMI
 *
 * @author Ilya Novitskiy
 * @version 1
 */
public class RemoteBank implements Bank {


    private final int port;


    public final String messageCreation = "Created";
    public final ConcurrentLinkedQueue<String> LOGS = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Account>>
            userAccountsDataBase = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Person> userDataBase = new ConcurrentHashMap<>();

    public int getPort() {
        return port;
    }

    @Override
    public synchronized void setAmountToPersonAccount(String passportId, String accId, int amount) throws RemoteException {
        userAccountsDataBase.get(passportId).get(accId).setAmount(amount);
    }

    @Override
    public synchronized int getCurrentBalanceOfPersonOnAccount(String passportId, String accId) throws RemoteException {
        return userAccountsDataBase.get(passportId).get(accId).getAmount();
    }

    @Override
    public synchronized ConcurrentHashMap<String, Account> getUserBankAccounts(String passport) throws RemoteException {
        return Copy.deepCopyUserAccounts(userAccountsDataBase.get(passport));
    }

    public RemoteBank(final int port) {
        this.port = port;
    }

    private synchronized Account createAccount(String passportId, String accId) throws RemoteException {
        final Account account = new RemoteAccount(passportId + ":" + accId);
        if (userAccountsDataBase.get(passportId).putIfAbsent(accId, account) == null) {
            return account;
        } else {
            return userAccountsDataBase.get(passportId).get(accId);
        }
    }

    @Override
    public synchronized void createAccountForPerson(String passportId, String accId) throws RemoteException {
        if (userAccountsDataBase.get(passportId).containsKey(accId)) {
            System.out.println("The account " + accId + " for " + passportId + " exists");
            LOGS.add("NotCreated");
            return;
        }
        System.out.println("Creating account " + passportId + " with accountId " + accId);
        createAccount(passportId, accId);
        LOGS.add("Created");
    }


    @Override
    public synchronized Person createUserPerson(String name, String surname, String passportId, String accId)
            throws RemoteException {
        System.out.println("Registering person with passport " + passportId);
        boolean checkExistence = userDataBase.containsKey(passportId) &&
                userDataBase.get(passportId).getName().equals(name) && userDataBase.get(passportId).getSurname().equals(surname);
        RemotePerson remoteUser;
        if (!checkExistence) {
            remoteUser = new RemotePerson(name, surname, passportId, this);
            userDataBase.put(passportId, remoteUser);
            userAccountsDataBase.put(passportId, new ConcurrentHashMap<>(Map.of(accId, new RemoteAccount(passportId + ":" + accId))));
            return remoteUser;
        }
        remoteUser = (RemotePerson) userDataBase.get(passportId);
        if (!userAccountsDataBase.containsKey(accId)) {
            userAccountsDataBase.put(passportId, new ConcurrentHashMap<>(Map.of(accId, new RemoteAccount(passportId + ":" + accId))));
        }
        return remoteUser;
    }

    @Override
    public synchronized boolean isCorrectDataByPassportId(String passportId, String name, String surname) throws RemoteException {
        boolean validation = userDataBase.get(passportId).getName().equals(name)
                && userDataBase.get(passportId).getSurname().equals(surname);
        if (validation) {
            System.out.println("User-info for " + passportId + " is validated");
            return true;
        }
        System.out.println("Provided user-info for " + passportId + " is not valid, check your info");
        return false;
    }

    @Override
    public synchronized Person getPerson(final String passportId, String name, String surname,
                                         Class<? extends Person> type) throws RemoteException {
        Person user = userDataBase.get(passportId);
        if (user == null) {
            return null;
        }
        if (!isCorrectDataByPassportId(passportId, name, surname)) {
            System.out.println("Given passportId " + passportId + " doesn't belong to " + name + " " + surname);
            return null;
        }
        System.out.println("Account for passport " + passportId + " already exists");
        if (type == LocalPerson.class) {
            return new LocalPerson(user.getName(), user.getSurname(), passportId,
                    Copy.deepCopyUserAccounts(userAccountsDataBase.get(passportId)));
        } else {
            return user;
        }
    }

}
