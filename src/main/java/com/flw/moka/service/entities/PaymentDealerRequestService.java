package com.flw.moka.service.entities;

import com.flw.moka.entity.PaymentDealerRequest;
import com.flw.moka.entity.ProductRequest;

public interface PaymentDealerRequestService {
    PaymentDealerRequest saveRequestPayload(ProductRequest productRequest, String method);
}
