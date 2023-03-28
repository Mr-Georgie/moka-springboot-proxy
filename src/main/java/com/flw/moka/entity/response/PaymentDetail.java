package com.flw.moka.entity.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetail {

    @JsonProperty("DealerPaymentId")
    private Long dealerPaymentId;

    @JsonProperty("OtherTrxCode")
    private String otherTrxCode;

    @JsonProperty("CardHolderFullName")
    private String cardHolderFullName;

    @JsonProperty("CardNumberFirstSix")
    private String cardNumberFirstSix;

    @JsonProperty("CardNumberLastFour")
    private String cardNumberLastFour;

    @JsonProperty("PaymentDate")
    private String paymentDate;

    @JsonProperty("Amount")
    private int amount;

    @JsonProperty("RefAmount")
    private int refAmount;

    @JsonProperty("CurrencyCode")
    private String currencyCode;

    @JsonProperty("InstallmentNumber")
    private int installmentNumber;

    @JsonProperty("DealerCommissionAmount")
    private float dealerCommissionAmount;

    @JsonProperty("IsThreeD")
    private Boolean isThreeD;

    @JsonProperty("Description")
    private String Description;

    @JsonProperty("PaymentStatus")
    private int paymentStatus;

    @JsonProperty("TrxStatus")
    private int trxStatus;
}
