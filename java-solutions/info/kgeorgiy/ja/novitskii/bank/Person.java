package info.kgeorgiy.ja.novitskii.bank;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Person interface of bank user
 *
 * @author Ilya Novitskiy
 * @version 1
 */

public interface Person extends Remote, Serializable {

    /**
     * Getter for name
     *
     * @return name
     * @throws RemoteException
     */
    String getName() throws RemoteException;

    /**
     * Getter fro surname
     *
     * @return the surname
     * @throws RemoteException
     */
    String getSurname() throws RemoteException;

    /**
     * Getter for passport number
     *
     * @return the passport number
     * @throws RemoteException
     */

    String getPassportNumber() throws RemoteException;

    /**
     * Returns the current balance of the specified acc
     *
     * @param accId account id
     * @return the amount of money on acc
     * @throws RemoteException
     */

    int accountBalance(String accId) throws RemoteException;

    /**
     * Sets the new amount to bank specified account of person
     *
     * @param accId
     * @param change
     * @throws RemoteException
     */

    void setAccountBalance(String accId, int change) throws RemoteException;

    /**
     * Creates new account for person
     *
     * @param accId
     * @throws RemoteException
     */
    void createNewAccount(String accId) throws RemoteException;

    /**
     * Returns bank accounts of the user
     *
     * @return
     * @throws RemoteException
     */
    ConcurrentHashMap<String, Account> getBankAccounts() throws RemoteException;
}
