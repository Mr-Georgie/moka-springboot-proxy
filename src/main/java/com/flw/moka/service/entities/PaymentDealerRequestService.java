package com.flw.moka.service.entities;

import com.flw.moka.entity.helpers.PaymentDealerRequest;
import com.flw.moka.entity.helpers.ProductRequest;

public interface PaymentDealerRequestService {
    PaymentDealerRequest createRequestPayload(ProductRequest productRequest, String method);
}
