package com.flw.moka.entity.helpers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    @NotNull(message = "Amount cannot be null/blank")
    @Positive(message = "Amount cannot be zero or negative")
    private Long amount;

    @NotNull(message = "Card number cannot be null")
    @NotBlank(message = "Card number cannot be blank")
    private String cardNo;

    @NotNull(message = "Country cannot be null")
    @NotBlank(message = "Country cannot be blank")
    private String country;

    @NotNull(message = "ExpiryMonth cannot be null")
    @NotBlank(message = "ExpiryMonth cannot be blank")
    private String expiryMonth;

    @NotNull(message = "authModel cannot be null")
    @NotBlank(message = "authModel cannot be blank")
    private String authModel;

    @NotNull(message = "ExpiryYear cannot be null")
    @NotBlank(message = "ExpiryYear cannot be blank")
    private String expiryYear;

    @NotNull(message = "Currency cannot be null")
    @NotBlank(message = "Currency cannot be blank")
    private String currency;

    @NotNull(message = "CVV cannot be null")
    @NotBlank(message = "CVV cannot be blank")
    private String cvv;

    private String transactionReference;
    private String customerIp;
    private String responseUrl;
    private String externalReference;
    private int voidRefundReason;
}
