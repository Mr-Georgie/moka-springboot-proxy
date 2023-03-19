package com.flw.moka.entity.helpers;

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
    private String CardNumber;

    @JsonProperty("ExpMonth")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String ExpMonth;

    @JsonProperty("ExpYear")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String ExpYear;

    @JsonProperty("CvcNumber")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String CvcNumber;

    @JsonProperty("Amount")
    private Long Amount;

    @JsonProperty("VoidRefundReason")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long VoidRefundReason;

    @JsonProperty("Currency")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String Currency;

    @JsonProperty("OtherTrxCode")
    private String OtherTrxCode;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonProperty("IsPoolPayment")
    private int IsPoolPayment;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonProperty("IsTokenized")
    private int IsTokenized;

    @JsonProperty("Software")
    private String Software;

    @JsonProperty("IsPreAuth")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int IsPreAuth;
}
