package com.flw.moka.service.entities;

import com.flw.moka.entity.PaymentDealerRequest;
import com.flw.moka.entity.ProviderPayload;

public interface ProviderPayloadService {
    ProviderPayload savePaymentDealerAuthAndReq(PaymentDealerRequest paymentDealerRequest);
}
