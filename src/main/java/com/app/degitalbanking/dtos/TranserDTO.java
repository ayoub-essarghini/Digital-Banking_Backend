package com.app.degitalbanking.dtos;

import lombok.Data;

@Data
public class TranserDTO {
    private String accountIdSrc;
    private String accountIdDest;
    private double amount;
}
