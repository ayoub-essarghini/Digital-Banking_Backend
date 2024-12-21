package com.app.degitalbanking.web;


import com.app.degitalbanking.dtos.*;
import com.app.degitalbanking.exception.BalanceNotSufficentException;
import com.app.degitalbanking.exception.BankAccountNotFoundException;
import com.app.degitalbanking.services.BankAccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@AllArgsConstructor
public class BankAccountRestController {

    private BankAccountService bankAccountService;


    @GetMapping("/accounts/{accountId}")
    public BankAccountDTO getBankAccountById(@PathVariable(name = "accountId") String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
    }

    @GetMapping("/accounts")
    public List<BankAccountDTO> getAllBankAccounts() {
        return bankAccountService.bankAccountList();
    }

    @GetMapping("/accounts/{id}/operations")
    public List<AccountOperationDTO> getBankAccountOperations(@PathVariable(name = "id") String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getAccountOperations(accountId);
    }

    @GetMapping("/accounts/{accountId}/pageOperations")
    public AccountHistoryDTO getAccountHistory(@PathVariable(name = "accountId") String accountId, @RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "5") int size) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId, page, size);
    }

    @PostMapping("/accounts/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO) throws BankAccountNotFoundException, BalanceNotSufficentException {
        this.bankAccountService.debit(debitDTO.getAccountId(),debitDTO.getAmount(),debitDTO.getDescription());
        return debitDTO;

    }
    @PostMapping("/accounts/credit")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO) throws BankAccountNotFoundException, BalanceNotSufficentException {
        this.bankAccountService.credit(creditDTO.getAccountId(),creditDTO.getAmount(),creditDTO.getDescription());
        return creditDTO;
    }
    @PostMapping("/accounts/transfer")
    public TranserDTO transfer(@RequestBody TranserDTO transerDTO) throws BankAccountNotFoundException, BalanceNotSufficentException {
        this.bankAccountService.transfer(transerDTO.getAccountIdSrc(), transerDTO.getAccountIdDest(), transerDTO.getAmount());
        return transerDTO;
    }
}
