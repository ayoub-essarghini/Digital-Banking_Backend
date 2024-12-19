package com.app.degitalbanking;

import com.app.degitalbanking.dtos.BankAccountDTO;
import com.app.degitalbanking.dtos.CurrentBankAccountDTO;
import com.app.degitalbanking.dtos.CustomerDTO;
import com.app.degitalbanking.dtos.SavingBankAccountDTO;
import com.app.degitalbanking.entities.*;
import com.app.degitalbanking.enums.AccountStatus;
import com.app.degitalbanking.enums.OperationType;
import com.app.degitalbanking.exception.BalanceNotSufficentException;
import com.app.degitalbanking.exception.BankAccountNotFoundException;
import com.app.degitalbanking.exception.CustomerNotFoundException;
import com.app.degitalbanking.repositories.AccountOperationsRepository;
import com.app.degitalbanking.repositories.BankAccountRepository;
import com.app.degitalbanking.repositories.CustomerRepository;

import com.app.degitalbanking.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EBankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EBankingBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(BankAccountService bankAccountService)
    {
        return args -> {
           Stream.of("Ahmed", "Ayoub","Fatima").forEach(name->{
               CustomerDTO customer = new CustomerDTO();
               customer.setName(name);
               customer.setEmail(name+"@gmail.com");
               bankAccountService.saveCustomer(customer);

           });
           bankAccountService.listCustomers().forEach(customer -> {
               try {
                   bankAccountService.saveCurrentBankAccount(Math.random()*9000,9000,customer.getId());
                   bankAccountService.saveSavingBankAccount(Math.random()*12000,5.5,customer.getId());

               } catch (CustomerNotFoundException e) {
                   e.printStackTrace();

               }
           });

            List<BankAccountDTO> bankAccountList = bankAccountService.bankAccountList();
            for (BankAccountDTO bankAccount : bankAccountList) {
                for (int i = 0; i < 10; i++)
                {
                    String accountId;
                    if (bankAccount instanceof SavingBankAccountDTO)
                    {
                        accountId = ((SavingBankAccountDTO)bankAccount).getId();
                    }
                    else
                        accountId = ((CurrentBankAccountDTO)bankAccount).getId();
                    bankAccountService.credit(accountId,10000 + Math.random() * 120000,"CREDIT");
                    bankAccountService.debit(accountId,1000 + Math.random() * 9000,"DEBIT");
                }
            }
        };
    }


    CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository accountRepository,
                            AccountOperationsRepository operationsRepository, BankAccountRepository bankAccountRepository) {
        return args -> Stream.of("Hassan", "Yassine", "Aicha").forEach(nom -> {
            Customer customer = new Customer();
            customer.setName(nom);
            customer.setEmail(nom + "@gmail.com");
            customerRepository.save(customer);


            CurrentAccount currentAccount = new CurrentAccount();
            currentAccount.setBalance(Math.random() * 9000);  // Assuming balance is required
            currentAccount.setCreatedAt(new Date());  // Assuming creation date is required
            currentAccount.setId(UUID.randomUUID().toString());
            currentAccount.setStatus(AccountStatus.CREATED);  // Assuming the status is required
            currentAccount.setCustomer(customer);  // Link to the customer
            currentAccount.setOverDraft(9000);  // Assuming overdraft is required


            accountRepository.save(currentAccount);

            SavingAccount savingAccount = new SavingAccount();
            savingAccount.setId(UUID.randomUUID().toString());
            savingAccount.setBalance(Math.random() * 90000);
            savingAccount.setCreatedAt(new Date());
            savingAccount.setStatus(AccountStatus.CREATED);
            savingAccount.setCustomer(customer);
            savingAccount.setInterestRate(5.5);
            accountRepository.save(savingAccount);

            bankAccountRepository.findAll().forEach(bankAccount -> {
                for (int i = 0;i < 5; i++)
                {
                    AccountOperation accountOperation = new AccountOperation();
                    accountOperation.setBankAccount(bankAccount);
                    accountOperation.setAmount(Math.random() * 12000);
                    accountOperation.setOperationDate(new Date());
                    accountOperation.setType(Math.random() > 0.5 ?  OperationType.CREDIT :  OperationType.DEBIT);
                    operationsRepository.save(accountOperation);
                }

            });

        });
    }
}
