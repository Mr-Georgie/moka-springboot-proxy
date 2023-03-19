package com.flw.moka.service.entities;

import com.flw.moka.entity.helpers.PaymentDealerRequest;
import com.flw.moka.entity.helpers.ProductRequest;

public interface PaymentDealerRequestService {
    PaymentDealerRequest saveRequestPayload(ProductRequest productRequest, String method);
}
