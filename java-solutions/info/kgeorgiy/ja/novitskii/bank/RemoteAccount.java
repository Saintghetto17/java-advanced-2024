package info.kgeorgiy.ja.novitskii.bank;

import java.rmi.RemoteException;

/**
 * The class represents the instance of Account for bank user
 *
 * @author Ilya Novitskiy
 * @version 1
 */
public class RemoteAccount implements Account {
    private final String id;
    private int amount;

    /**
     * Creates the remote Account for person
     *
     * @param id
     */
    public RemoteAccount(final String id) {
        this.id = id;
        amount = 0;
    }

    /**
     * Getter for account identifier
     *
     * @return the id of account
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Getter for getting amount of money on account
     *
     * @return current balance
     */
    @Override
    public synchronized int getAmount() {
        return amount;
    }

    /**
     * Sets the new amount to bank user account
     *
     * @param amount
     */
    @Override
    public synchronized void setAmount(final int amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Account)) {
            return false;
        }
        Account account = (Account) obj;
        try {
            if (this.id.equals(account.getId()) && this.amount == account.getAmount()) {
                return true;
            } else {
                return false;
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
