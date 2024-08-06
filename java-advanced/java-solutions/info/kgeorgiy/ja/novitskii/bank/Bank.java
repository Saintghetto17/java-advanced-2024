package info.kgeorgiy.ja.novitskii.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ilya Novitskiy
 * @version 1
 */
public interface Bank extends Remote {


    /**
     * Creates a new user with specified identifier accId for account
     * and with passportId if it does not already exist.
     *
     * @param passportId for which to create account
     * @param accId      is the account for creation
     * @return created or existing person account. null if person doesn't exists
     */
    void createAccountForPerson(String passportId, String accId) throws RemoteException;

    /**
     * Returns person by identifier.
     *
     * @param passportId is the passport number of user
     * @param name
     * @param surname
     * @param type       - the mode of person locally or remotely
     * @return person with specified identifier or {@code null} if such person does not exist.
     */
    Person getPerson(String passportId, String name, String surname, Class<? extends Person> type)
            throws RemoteException;

    /**
     * Create new bank user and adds him to bank dataBase
     *
     * @param name     of the new bank user
     * @param surname  of the new bank user
     * @param passport passport number of the new bank user
     * @param accId    bank account for person
     * @return new user of bank
     */

    Person createUserPerson(String name, String surname, String passport, String accId)
            throws RemoteException;

    /**
     * Checks is name and surname are correct for given passport number
     *
     * @param passportId passport number of the bank user
     * @param name       of the new bank user
     * @param surname    of the new bank user
     * @return true if correct and false otherwise
     */

    /**
     * Checks that person by given passport has correct provided name and surname
     *
     * @param passportId
     * @param name
     * @param surname
     * @return
     * @throws RemoteException
     */

    boolean isCorrectDataByPassportId(String passportId, String name, String surname) throws RemoteException;

    /**
     * Getter for port of remote layer that is connected to bank
     *
     * @return
     * @throws RemoteException
     */

    int getPort() throws RemoteException;

    /**
     * Set some specified money to the account of person with passportID
     *
     * @param passportId
     * @param accId
     * @param amount
     * @throws RemoteException
     */
    void setAmountToPersonAccount(String passportId, String accId, int amount) throws RemoteException;

    /**
     * Return the current balance of user
     *
     * @param passportId
     * @param accId
     * @return current balance
     * @throws RemoteException
     */

    int getCurrentBalanceOfPersonOnAccount(String passportId, String accId) throws RemoteException;

    /**
     * Returns the bank accounts of user with given passport
     *
     * @param passport
     * @return null is no accounts was registered or all bank accounts
     * @throws RemoteException
     */
    ConcurrentHashMap<String, Account> getUserBankAccounts(String passport) throws RemoteException;
}