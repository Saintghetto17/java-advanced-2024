package info.kgeorgiy.ja.novitskii.bank;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ilya Novitskiy
 * @version 1
 */
public class LocalPerson extends RemotePerson {

    ConcurrentHashMap<String, Account> bankAccounts;

    /**
     * Creates a local person
     * Local person is the person that will not be binded with bank DB
     *
     * @param name
     * @param surname
     * @param passportNumber
     * @param bankAccounts   that these local person will serve
     */

    LocalPerson(String name, String surname, String passportNumber, ConcurrentHashMap<String, Account> bankAccounts) {
        super(name, surname, passportNumber, null);
        this.bankAccounts = bankAccounts;
    }

    @Override
    public synchronized int accountBalance(String accId) throws RemoteException {
        return bankAccounts.get(accId).getAmount();
    }

    @Override
    public synchronized void setAccountBalance(String accId, int changedBalance) throws RemoteException {
        bankAccounts.get(accId).setAmount(changedBalance);
    }

    @Override
    public void createNewAccount(String accId) throws RemoteException {
        this.bankAccounts.putIfAbsent(accId, new RemoteAccount(accId));
    }

    @Override
    public ConcurrentHashMap<String, Account> getBankAccounts() throws RemoteException {
        return Copy.deepCopyUserAccounts(bankAccounts);
    }
}
