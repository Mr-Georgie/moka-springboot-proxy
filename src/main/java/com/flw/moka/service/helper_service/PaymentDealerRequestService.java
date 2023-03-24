package com.flw.moka.service.helper_service;

import com.flw.moka.entity.helpers.PaymentDealerRequest;
import com.flw.moka.entity.helpers.ProductRequest;

public interface PaymentDealerRequestService {
    PaymentDealerRequest createRequestPayload(ProductRequest productRequest, String method);
}
