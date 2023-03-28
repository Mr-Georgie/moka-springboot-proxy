package com.flw.moka.service.helper_service;

import com.flw.moka.entity.request.PaymentDealerRequest;
import com.flw.moka.entity.request.ProductRequest;

public interface PaymentDealerRequestService {
    PaymentDealerRequest createRequestPayload(ProductRequest productRequest, String method);
}
