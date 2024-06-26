package com.flw.moka.entity.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatusCheckResponse {

    private String statusMessage;
    private StatusMeta statusMeta;
}
