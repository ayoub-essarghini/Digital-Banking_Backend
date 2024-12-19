package com.app.degitalbanking.dtos;

import com.app.degitalbanking.entities.AccountOperation;
import com.app.degitalbanking.entities.Customer;
import com.app.degitalbanking.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@Data
public class SavingBankAccountDTO extends BankAccountDTO {

    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus status;
    private CustomerDTO customerDTO;
    private double interestRate;

}
