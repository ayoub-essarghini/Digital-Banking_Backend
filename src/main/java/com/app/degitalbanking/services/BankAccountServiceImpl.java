package com.app.degitalbanking.services;

import com.app.degitalbanking.dtos.*;
import com.app.degitalbanking.entities.*;
import com.app.degitalbanking.enums.AccountStatus;
import com.app.degitalbanking.enums.OperationType;
import com.app.degitalbanking.exception.BalanceNotSufficentException;
import com.app.degitalbanking.exception.BankAccountNotFoundException;
import com.app.degitalbanking.exception.CustomerNotFoundException;
import com.app.degitalbanking.mappers.BankAccountMapperImpl;
import com.app.degitalbanking.repositories.AccountOperationsRepository;
import com.app.degitalbanking.repositories.BankAccountRepository;
import com.app.degitalbanking.repositories.CustomerRepository;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationsRepository accountOperationsRepository;

    private BankAccountMapperImpl bankAccountMapper;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new customer");
        Customer customer1 = bankAccountMapper.fromCustomerDTO(customerDTO);
        return bankAccountMapper.fromCustomer(customerRepository.save(customer1));
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Updating new customer");
        Customer customer1 = bankAccountMapper.fromCustomerDTO(customerDTO);
        return bankAccountMapper.fromCustomer(customerRepository.save(customer1));
    }

    @Override
    public void deleteCustomer(Long customerId) {
        log.info("Deleting new customer");
        customerRepository.deleteById(customerId);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        CurrentAccount bankAccount = new CurrentAccount();
        bankAccount.setBalance(initialBalance);
        bankAccount.setCreatedAt(new Date());
        bankAccount.setStatus(AccountStatus.CREATED);
        bankAccount.setOverDraft(overDraft);
        bankAccount.setCustomer(customer);
        bankAccount.setId(UUID.randomUUID().toString());
        return bankAccountMapper.fromCurrentBankAccount(bankAccountRepository.save(bankAccount));
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setBalance(initialBalance);
        savingAccount.setCreatedAt(new Date());
        savingAccount.setStatus(AccountStatus.CREATED);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);
        savingAccount.setId(UUID.randomUUID().toString());
        return bankAccountMapper.fromSavingBankAccount(bankAccountRepository.save(savingAccount));
    }


    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream().map(customer -> bankAccountMapper.fromCustomer(customer)).collect(Collectors.toList());
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() -> new BankAccountNotFoundException("Bank account not found"));
        if (bankAccount instanceof CurrentAccount currentAccount) {
            return bankAccountMapper.fromCurrentBankAccount(currentAccount);
        } else {
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return bankAccountMapper.fromSavingBankAccount(savingAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficentException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() -> new BankAccountNotFoundException("Bank account not found"));
        if (bankAccount.getBalance() < amount) {
            throw new BalanceNotSufficentException("Balance not Sufficent");
        }
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setOperationDate(new Date());
        accountOperation.setDescription(description);
        accountOperation.setBankAccount(bankAccount);
        accountOperationsRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficentException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() -> new BankAccountNotFoundException("Bank account not found"));

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setOperationDate(new Date());
        accountOperation.setDescription(description);
        accountOperation.setBankAccount(bankAccount);
        accountOperationsRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficentException {

        debit(accountIdSource, amount, "Transfer To " + accountIdDestination);
        credit(accountIdDestination, amount, "Transfer From " + accountIdSource);

    }

    @Override
    public List<BankAccountDTO> bankAccountList() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOList = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof CurrentAccount) {
                return bankAccountMapper.fromCurrentBankAccount((CurrentAccount) bankAccount);
            } else {
                return bankAccountMapper.fromSavingBankAccount((SavingAccount) bankAccount);
            }

        }).collect(Collectors.toList());

        return bankAccountDTOList;
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        return bankAccountMapper.fromCustomer(customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found")));
    }
    @Override
    public List<AccountOperationDTO> getAccountOperations(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(()->new BankAccountNotFoundException("Bank account not found"));
        List<AccountOperation> accountOperations = bankAccount.getOperations();
        List<AccountOperationDTO> operations = accountOperations.stream().map(operation ->
                bankAccountMapper.fromAccountOperation(operation)).collect(Collectors.toList());
        return operations;
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if (bankAccount == null)
            throw new BankAccountNotFoundException("Bank account not found");
        Page<AccountOperation> operations = accountOperationsRepository.findByBankAccountId(accountId, PageRequest.of(page, size));

        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        accountHistoryDTO.setAccountId(accountId);
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        List<AccountOperationDTO> operationsDto = operations.getContent().stream().map(operation -> bankAccountMapper.fromAccountOperation(operation)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOS(operationsDto);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setTotalPages(operations.getTotalPages());
        return accountHistoryDTO;
    }


}
