package info.kgeorgiy.ja.novitskii.bank;

import javax.swing.text.html.ObjectView;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The class Represents the remoted version of Person - some conditions and methods are concurrent
 *
 * @author Ilya Novitskiy
 * @version 1
 */
public class RemotePerson implements Person {
    private final String name;

    private final String surname;
    private final String passportNumber;
    private Bank bank = null;

    /**
     * Creates a remote person that contains the reference to bank for RMI communication
     *
     * @param name
     * @param surname
     * @param passportNumber passport number of that user
     * @param bank           the reference to bank that contains that user
     */
    public RemotePerson(String name, String surname, String passportNumber, Bank bank) {
        this.name = name;
        this.surname = surname;
        this.passportNumber = passportNumber;
        this.bank = bank;
    }


    @Override
    public String getName() throws RemoteException {
        return name;
    }

    @Override
    public String getSurname() throws RemoteException {
        return surname;
    }

    @Override
    public String getPassportNumber() throws RemoteException {
        return passportNumber;
    }


    @Override

    public synchronized int accountBalance(String accId) throws RemoteException {
        System.out.println("Getting amount of money for account " + accId);
        return this.bank.getCurrentBalanceOfPersonOnAccount(passportNumber, accId);
    }

    @Override

    public synchronized void setAccountBalance(String accId, int changedBalance) throws RemoteException {
        System.out.println("Setting new amount of money for account " + accId);
        this.bank.setAmountToPersonAccount(passportNumber, accId, changedBalance);
    }

    @Override
    public void createNewAccount(String accId) throws RemoteException {
        this.bank.createAccountForPerson(passportNumber, accId);
    }

    @Override
    public ConcurrentHashMap<String, Account> getBankAccounts() throws RemoteException {
        return this.bank.getUserBankAccounts(passportNumber);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        RemotePerson that = (RemotePerson) obj;

        return Objects.equals(name, that.name) &&
                Objects.equals(surname, that.surname) &&
                Objects.equals(passportNumber, that.passportNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, passportNumber);
    }
}
