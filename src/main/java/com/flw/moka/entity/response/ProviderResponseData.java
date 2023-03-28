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
public class ProviderResponseData {
    @JsonProperty("IsSuccessful")
    private String isSuccessful;

    @JsonProperty("ResultCode")
    private String resultCode;

    @JsonProperty("ResultMessage")
    private String resultMessage;

    @JsonProperty("VirtualPosOrderId")
    private String virtualPosOrderId;

    @JsonProperty("PaymentDetail")
    private PaymentDetail paymentDetail;
}
