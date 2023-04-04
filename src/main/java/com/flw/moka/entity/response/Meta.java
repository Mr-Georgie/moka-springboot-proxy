package com.flw.moka.entity.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Meta {
    private String transactionReference;
    private String payloadReference;
    private String externalReference;
    private String provider;
}
