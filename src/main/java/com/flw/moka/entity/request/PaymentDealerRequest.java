package com.flw.moka.entity.request;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class PaymentDealerRequest {

    @JsonProperty("CardNumber")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cardNumber;

    @JsonProperty("ExpMonth")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String expMonth;

    @JsonProperty("ExpYear")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String expYear;

    @JsonProperty("CvcNumber")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cvcNumber;

    @JsonProperty("Amount")
    private Long amount;

    @JsonProperty("VoidRefundReason")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int voidRefundReason;

    @JsonProperty("Currency")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String currency;

    @JsonProperty("OtherTrxCode")
    private String otherTrxCode;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonProperty("IsPoolPayment")
    private int isPoolPayment;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonProperty("IsTokenized")
    private int isTokenized;

    @JsonProperty("Software")
    private String software;

    @JsonProperty("IsPreAuth")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int isPreAuth;
}
