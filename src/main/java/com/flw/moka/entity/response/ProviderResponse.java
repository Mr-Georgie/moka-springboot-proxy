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
public class ProviderResponse {

    @JsonProperty("Data")
    private ProviderResponseData data;
    @JsonProperty("ResultCode")
    private String resultCode;
    @JsonProperty("ResultMessage")
    private String resultMessage;
    @JsonProperty("Exception")
    private String exception;
}
