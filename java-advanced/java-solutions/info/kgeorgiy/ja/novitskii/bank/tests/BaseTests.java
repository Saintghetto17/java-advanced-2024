package info.kgeorgiy.ja.novitskii.bank.tests;

import info.kgeorgiy.ja.novitskii.bank.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This class provides basic tests for the bank API
 *
 * @author Ilya Novitskiy
 * @version 1
 */
@DisplayName("BaseTests for bank homework")
public class BaseTests {
    private BaseTests() {
    }

    private enum ResultProcess {
        SUCCESS,
        FAIL
    }

    static Random rand;

    @BeforeAll
    static void beforeAll() {
        rand = new Random();
        Server.main();
    }


    private ResultProcess testGettersAccount(String expected, int amount, Account account) throws RemoteException {
        if (expected.equals(account.getId()) && amount == account.getAmount()) {
            return ResultProcess.SUCCESS;
        }
        return ResultProcess.FAIL;
    }

    private ResultProcess testSetterAccount(int toSet, Account account) throws RemoteException {
        account.setAmount(toSet);
        if (toSet == account.getAmount()) {
            return ResultProcess.SUCCESS;
        }
        return ResultProcess.FAIL;
    }

    @Test
    void testAccountMethods() {
        String expected = String.valueOf(rand.nextInt(10, 1000));
        int toSet = rand.nextInt(100, 1000);
        Account account = new RemoteAccount(expected);
        try {
            Assertions.assertEquals(ResultProcess.SUCCESS, testGettersAccount(expected, 0, account));
            Assertions.assertEquals(ResultProcess.SUCCESS, testSetterAccount(toSet, account));
        } catch (RemoteException ignored) {
        }
    }


    private ResultProcess testGettersPerson(String name, String surname, String passportNumber, Person person)
            throws RemoteException {
        if (name.equals(person.getName()) && surname.equals(person.getSurname())
                && passportNumber.equals(person.getPassportNumber())) {
            return ResultProcess.SUCCESS;
        }
        return ResultProcess.FAIL;
    }

    @Test
    void testRemoteBankGetPerson() {
        final String name = "Igor";
        final String surname = "Panasyk";
        final String passportNumber = "12312";

        final String accId = "1";
        final String fakeName = "Iger";
        final String fakeSurname = "OhiNov";
        try {
            Person person = Server.bank.getPerson(passportNumber, name, surname, RemotePerson.class);
            Assertions.assertNull(person);
            Server.bank.createUserPerson(name, surname, passportNumber, accId);
            Assertions.assertTrue(Server.bank.getUserBankAccounts(passportNumber).containsKey(accId));

            Assertions.assertTrue(Server.bank.isCorrectDataByPassportId(passportNumber, name, surname));
            Assertions.assertFalse(Server.bank.isCorrectDataByPassportId(passportNumber, fakeName, surname));
            Assertions.assertFalse(Server.bank.isCorrectDataByPassportId(passportNumber, name, fakeSurname));

            Person personLocal = Server.bank.getPerson(passportNumber, name, surname, LocalPerson.class);
            Person personRemote = Server.bank.getPerson(passportNumber, name, surname, RemotePerson.class);
            Assertions.assertSame(LocalPerson.class, personLocal.getClass());
            Assertions.assertTrue(personLocal.getName().equals(name) &&
                    personLocal.getSurname().equals(surname) && personLocal.getPassportNumber().equals(passportNumber));

            Assertions.assertEquals(RemotePerson.class, personRemote.getClass());
            Assertions.assertTrue(personRemote.getName().equals(name) &&
                    personRemote.getSurname().equals(surname) && personRemote.getPassportNumber().equals(passportNumber));

            Assertions.assertEquals(0, Server.bank.getCurrentBalanceOfPersonOnAccount(passportNumber, accId));
            Server.bank.setAmountToPersonAccount(passportNumber, accId, 200);
            Assertions.assertEquals(200, Server.bank.getCurrentBalanceOfPersonOnAccount(passportNumber, accId));
            ConcurrentHashMap<String, Account> bankAccount = Server.bank.getUserBankAccounts(passportNumber);
            Assertions.assertNotNull(bankAccount);
            Assertions.assertTrue(bankAccount.containsKey(accId));
        } catch (RemoteException ignored) {
        }

    }

    @Test
    void testRemotePersonMethods() {
        Person person = new RemotePerson("Ilya", "Novitskiy", "1211123",
                (RemoteBank) Server.bank);
        String randomId = String.valueOf(rand.nextInt(100, 1000));
        int randomChange = rand.nextInt(100, 12123);
        try {
            Server.bank.createUserPerson(person.getName(), person.getSurname(), person.getPassportNumber(), randomId);
        } catch (RemoteException ex) {
            System.err.println(ex.getLocalizedMessage());
            return;
        }

        Map<String, Account> bankAccTest = new ConcurrentHashMap<>();
        Account account = new RemoteAccount("1211123" + ":" + randomId);
        bankAccTest.put(randomId, account);
        try {
            Assertions.assertEquals(ResultProcess.SUCCESS, testGettersPerson("Ilya", "Novitskiy", "1211123", person));

            Assertions.assertEquals(bankAccTest, person.getBankAccounts());

            Assertions.assertEquals(0, person.accountBalance(randomId));

            person.setAccountBalance(randomId, randomChange);
            Assertions.assertEquals(randomChange, person.accountBalance(randomId));

            String accIdNew = "123";
            person.createNewAccount(accIdNew);
            Assertions.assertTrue(person.getBankAccounts().containsKey(accIdNew));
        } catch (RemoteException ignored) {
        }
    }

    @Test
    void testLocalCopyPerson() {
        String accId = "100";
        RemotePerson person = new RemotePerson("Ilya", "Ostrich", "00012", (RemoteBank) Server.bank);
        try {
            Server.bank.createUserPerson(person.getName(), person.getSurname(), person.getPassportNumber(), accId);
        } catch (RemoteException ex) {
            System.err.println(ex.getLocalizedMessage());
            return;
        }

        try {
            LocalPerson copy = (LocalPerson) Server.bank.getPerson("00012", person.getName(),
                    person.getSurname(), LocalPerson.class);
            Assertions.assertEquals(ResultProcess.SUCCESS,
                    testGettersPerson("Ilya", "Ostrich", "00012", copy));
            Assertions.assertEquals(person.getBankAccounts(), copy.getBankAccounts());

            Assertions.assertEquals(person.accountBalance(accId), copy.accountBalance(accId));
        } catch (RemoteException ignored) {
        }
    }


    private void register(String name, String surname, String passportNumber,
                          String accId, Class<? extends Person> type) throws RemoteException {
        Server.bank.createUserPerson(name, surname, passportNumber, accId);
        Server.bank.createAccountForPerson(passportNumber, accId);
    }

    private void checkAddingNewAccountVisibility(String passportNumber, String name, String surname, String newAcc)
            throws RemoteException {
        Person personRemote = Server.bank.getPerson(passportNumber, name, surname, RemotePerson.class);
        Person personLocal = Server.bank.getPerson(passportNumber, name, surname, LocalPerson.class);

        Assertions.assertEquals(personRemote.getBankAccounts(), personLocal.getBankAccounts());
        Server.bank.createAccountForPerson(passportNumber, newAcc);
        Assertions.assertNotEquals(personLocal.getBankAccounts(), personRemote.getBankAccounts());
    }

    private void checkSettingNewAmountVisibility(String passportNumberSet, String name, String surname, String accIdSet) throws RemoteException {
        Person personRemoteSet = Server.bank.getPerson(passportNumberSet, name, surname, RemotePerson.class);
        Person personLocalSet = Server.bank.getPerson(passportNumberSet, name, surname, LocalPerson.class);

        int prevLocal = personLocalSet.getBankAccounts().get(accIdSet).getAmount();
        int prevRemote = personRemoteSet.getBankAccounts().get(accIdSet).getAmount();
        Assertions.assertEquals(prevLocal, prevRemote);

        Server.bank.setAmountToPersonAccount(passportNumberSet, accIdSet, 3000);
        int remoteMoney = personRemoteSet.getBankAccounts().get(accIdSet).getAmount();
        int localMoney = personLocalSet.getBankAccounts().get(accIdSet).getAmount();
        int bankMoneyPersonAcc = Server.bank.getCurrentBalanceOfPersonOnAccount(passportNumberSet, accIdSet);
        Assertions.assertEquals(remoteMoney, bankMoneyPersonAcc);
        Assertions.assertNotEquals(localMoney, bankMoneyPersonAcc);
    }

    @Test
    void testGlobalChangingPerson() {
        final String name = "Nikolay";
        final String surname = "Vedernikov";
        final String passportNumber = "401798867";
        final String accId = "2";
        final String newAcc = "3";

        final String nameSet = "Georgiy";
        final String surnameSet = "Korneevich";
        final String passportNumberSet = "401798867";
        final String accIdSet = "300";
        try {
            register(name, surname, passportNumber, accId, RemotePerson.class);
            checkAddingNewAccountVisibility(passportNumber, name, surname, newAcc);

            register(nameSet, surnameSet, passportNumberSet, accIdSet, RemotePerson.class);
            checkSettingNewAmountVisibility(passportNumberSet, nameSet, surnameSet, accIdSet);
        } catch (RemoteException ignored) {
        }
    }

    @Test
    void checkCorrectAccountNames() {
        try {
            Person person = Server.bank.createUserPerson("Mike", "Mirza", "4019", "0");
            String actual = Server.bank.getUserBankAccounts(person.getPassportNumber()).get("0").getId();
            Assertions.assertEquals("4019:0", actual);
        } catch (RemoteException ignored) {
        }
    }

    private Thread generateThread(Person person, int times) {
        return new Thread(() -> {
            try {
                int cnt = 0;
                while (cnt < times) {
                    person.setAccountBalance("1", person.accountBalance("1") + 1);
                    cnt++;
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }


    void testParallelPerson(Person person, int previous) {
        Thread thread_1 = generateThread(person, 5);
        Thread thread_2 = generateThread(person, 5);
        thread_1.start();
        thread_2.start();
        try {
            thread_1.join();
            thread_2.join();
            Assertions.assertEquals(10 + previous, person.accountBalance("1"));
        } catch (InterruptedException | RemoteException ignored) {
        }
    }

    private Thread generateThreadAcc(Person person, List<String> accounts, RemoteBank bank) {
        return new Thread(() -> {
            for (int i = 0; i < accounts.size(); i++) {
                try {
                    bank.createAccountForPerson(person.getPassportNumber(), accounts.get(i));
                } catch (RemoteException ignored) {
                }

            }
        });
    }

    private void testParallelAddingAccounts(Person person, List<String> accounts) {
        RemoteBank bank = (RemoteBank) Server.bank;
        bank.LOGS.clear();

        Thread thread_1 = generateThreadAcc(person, accounts, bank);
        Thread thread_2 = generateThreadAcc(person, accounts, bank);

        thread_1.start();
        thread_2.start();
        try {
            thread_1.join();
            thread_2.join();
            int numberOfCreations = 0;
            // It is important to know, that number of creations == number of accounts
            // because the set of acc can be the same, but the numberOfCreation can be bigger
            // we do not want this behavior.
            for (String log : bank.LOGS) {
                if (log.equals(bank.messageCreation)) {
                    numberOfCreations++;
                }
            }
            Assertions.assertEquals(accounts.size() - 1, numberOfCreations);
            Set<String> uniqueAccounts = Server.bank.getUserBankAccounts(person.getPassportNumber()).keySet();
            Assertions.assertEquals(new HashSet<>(accounts), uniqueAccounts);
        } catch (InterruptedException ignored) {
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testParallel() {
        RemotePerson person = new RemotePerson("Mike", "Perveev", "4015", (RemoteBank) Server.bank);
        int previous = 0;
        try {
            Server.bank.createUserPerson("Mike", "Perveev", "4015", "1");

            previous = person.accountBalance("1");
            testParallelPerson(person, previous);

            previous = person.accountBalance("1");
            LocalPerson personLocal = (LocalPerson) Server.bank.getPerson(person.getPassportNumber(), person.getName(),
                    person.getSurname(), LocalPerson.class);
            testParallelPerson(personLocal, previous);


            List<String> accounts = List.of("0", "2", "3", "4", "5", "6", "7");
            person = new RemotePerson("Nikita", "Golikov", "4016", (RemoteBank) Server.bank);
            Server.bank.createUserPerson("Nikita", "Golikov", "4016", "0");
            testParallelAddingAccounts(person, accounts);
        } catch (RemoteException ignored) {
        }
    }
}
