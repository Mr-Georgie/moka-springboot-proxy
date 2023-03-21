package com.flw.moka.service.entities;

import com.flw.moka.entity.helpers.PaymentDealerRequest;
import com.flw.moka.entity.helpers.ProviderPayload;

public interface ProviderPayloadService {
    ProviderPayload savePaymentDealerAuthAndReq(PaymentDealerRequest paymentDealerRequest);
}
