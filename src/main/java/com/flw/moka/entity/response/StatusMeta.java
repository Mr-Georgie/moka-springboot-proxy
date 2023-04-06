package com.flw.moka.entity.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatusMeta {
    String transactionReference;
    String cardholderName;
    String mask;
    String paymentDate;
    Long amount;
    Long refundedAmount;
    String currency;
}
