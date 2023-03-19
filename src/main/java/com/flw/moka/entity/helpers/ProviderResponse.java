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
public class ProviderResponse {

    @JsonProperty("Data")
    private ProviderResponseData Data;
    @JsonProperty("ResultCode")
    private String ResultCode;
    @JsonProperty("ResultMessage")
    private String ResultMessage;
    @JsonProperty("Exception")
    private String Exception;
}
