package com.app.degitalbanking.web;


import com.app.degitalbanking.dtos.AccountHistoryDTO;
import com.app.degitalbanking.dtos.AccountOperationDTO;
import com.app.degitalbanking.dtos.BankAccountDTO;
import com.app.degitalbanking.exception.BankAccountNotFoundException;
import com.app.degitalbanking.services.BankAccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BankAccountRestController {

    private BankAccountService bankAccountService;


    public BankAccountRestController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/accounts/{accountId}")
    public BankAccountDTO getBankAccountById(@PathVariable(name = "accountId")  String accountId) throws BankAccountNotFoundException {
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
    public AccountHistoryDTO getAccountHistory(@PathVariable(name = "accountId") String accountId, @RequestParam(name = "page",defaultValue = "0") int page, @RequestParam(name = "size",defaultValue = "5") int size) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId,page,size);
    }
}
