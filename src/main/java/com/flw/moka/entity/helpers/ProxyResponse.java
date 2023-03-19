package com.flw.moka.entity.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProxyResponse {

    private String message;
    private String code;
    private String txRef;
    private String exRef;
    private String provider;
    // private String providerResponse;
    private ProviderResponse providerResponse;

    public ProxyResponse(String message) {
        this.message = message;
    }

    public ProxyResponse() {

    }
}
