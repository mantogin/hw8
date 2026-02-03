package ru.mantogin.hw8.billing.app;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AppController {

    private record Account(Integer number, Integer balance) {}

    private Integer accountSequence = 0;

    private Map<Integer, Account> accounts = new HashMap<>();

    private Integer transactionNumber = 0;

    private Map<Integer, Integer> transactions = new HashMap<>();

    @PostMapping(value = "/billing/account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> createAccount() {

        System.out.printf("account creation.");

        Account account = new Account(++accountSequence, 0);
        accounts.put(account.number, account);

        System.out.printf("account: " + account + " created.");

        return new ResponseEntity<>(account.number, HttpStatus.CREATED);
    }

    @PutMapping(value = "/billing/account/{number}")
    public ResponseEntity<Integer> changeAccountBalance(@PathVariable Integer number, @RequestBody Integer amount) {

        System.out.printf("amount: " + amount + " to change the account: " + number + " balance.");

        Account currentAccount = accounts.get(number);
        Integer newBalance = currentAccount.balance + amount;
        if (newBalance < 0) {
            System.out.printf("The account balance is insufficient.");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Account changedAccount = new Account(currentAccount.number, newBalance);
        transactions.put(++transactionNumber, amount);
        accounts.put(changedAccount.number, changedAccount);

        System.out.printf("account: " + changedAccount + " balance changed.");

        return new ResponseEntity<>(transactionNumber, HttpStatus.OK);
    }

    @DeleteMapping(value = "/billing/account/{accountNumber}/transactions/{transactionNumber}")
    public ResponseEntity<Void> rollbackAccountTransaction(@PathVariable Integer accountNumber,
                                                              @PathVariable Integer transactionNumber) {

        if (!accounts.containsKey(accountNumber)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (transactions.containsKey(transactionNumber)) {
            Integer amount = transactions.get(transactionNumber);
            if (amount != 0) {
                Account currentAccount = accounts.get(accountNumber);
                Integer newBalance = currentAccount.balance + (-amount);
                Account changedAccount = new Account(currentAccount.number, newBalance);
                accounts.put(changedAccount.number, changedAccount);
                transactions.put(transactionNumber, 0);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping(value = "/billing/account/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getAccountBalance(@PathVariable Integer number) {

        System.out.printf("Get balance for account: " + number);

        Account account = accounts.get(number);

        return new ResponseEntity<>(account.balance, HttpStatus.OK);
    }
}
