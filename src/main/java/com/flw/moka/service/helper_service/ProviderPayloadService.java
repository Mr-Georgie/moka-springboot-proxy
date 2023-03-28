package com.flw.moka.service.helper_service;

import com.flw.moka.entity.request.PaymentDealerRequest;
import com.flw.moka.entity.request.ProviderPayload;

public interface ProviderPayloadService {
    ProviderPayload savePaymentDealerAuthAndReq(PaymentDealerRequest paymentDealerRequest);
}
