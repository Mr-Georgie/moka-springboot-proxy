package com.flw.moka.entity.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProxyResponse {

    private String message;
    private String code;
    private String transactionReference;
    private String externalReference;
    private String provider;
    private ProviderResponse providerResponse;

}
