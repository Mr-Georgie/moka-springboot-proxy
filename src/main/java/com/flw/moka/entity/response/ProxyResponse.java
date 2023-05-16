package com.flw.moka.entity.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProxyResponse {

    private String responseMessage;
    private String responseCode;
    private Meta meta;
    @JsonIgnore
    private ProviderResponse providerResponse;

}