package com.app.degitalbanking.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
//@DiscriminatorValue("SA")
@Data @AllArgsConstructor
@NoArgsConstructor
public class SavingAccount extends BankAccount{
    private double interestRate;
}
