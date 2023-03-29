package com.flw.moka.entity.response;

import com.flw.moka.entity.models.Refunds;
import com.flw.moka.entity.models.Transaction;

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
    private Transaction transactionDetail;
    private Refunds refundDetail;
    private PaymentDetail providerResponse;
}
