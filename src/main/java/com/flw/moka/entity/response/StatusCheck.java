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
public class StatusCheck {
    private String statusMessage;
    private Transaction transaction;
    private Refunds refund;
    private PaymentDetail paymentDetail;
}
