package com.flw.moka.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDealerAuthentication {

    @JsonProperty("DealerCode")
    private String DealerCode;

    @JsonProperty("Username")
    private String Username;

    @JsonProperty("Password")
    private String Password;

    @JsonProperty("CheckKey")
    private String CheckKey;

}
