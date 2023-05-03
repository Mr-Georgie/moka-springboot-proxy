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
public class Meta {
    private String transactionReference;
    private String payloadReference;
    private String externalReference;
    @JsonIgnore
    private String refundId;
    private String provider;
}
