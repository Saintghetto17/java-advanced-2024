package info.kgeorgiy.ja.novitskii.bank;


import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Copy {
    public static ConcurrentHashMap<String, Account> deepCopyUserAccounts
            (Map<String, Account> userAccounts) throws RemoteException {
        ConcurrentHashMap<String, Account> copyUserAccounts = new ConcurrentHashMap<>();
        for (Map.Entry<String, Account> idToAcc : userAccounts.entrySet()) {
            Account copied = new RemoteAccount(idToAcc.getValue().getId());
            copied.setAmount(idToAcc.getValue().getAmount());
            copyUserAccounts.put(idToAcc.getKey(), copied);
        }
        return copyUserAccounts;
    }
}
