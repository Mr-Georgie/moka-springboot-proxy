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
public class ProviderResponseData {
    @JsonProperty("IsSuccessful")
    private String IsSuccessful;
    @JsonProperty("ResultCode")
    private String ResultCode;
    @JsonProperty("ResultMessage")
    private String ResultMessage;
    @JsonProperty("VirtualPosOrderId")
    private String VirtualPosOrderId;
}
