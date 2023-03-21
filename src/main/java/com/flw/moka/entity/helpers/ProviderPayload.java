package com.flw.moka.entity.helpers;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProviderPayload {

    @JsonProperty("PaymentDealerAuthentication")
    private PaymentDealerAuthentication paymentDealerAuthentication;

    @JsonProperty("PaymentDealerRequest")
    private PaymentDealerRequest paymentDealerRequest;

}
