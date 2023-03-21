package com.flw.moka.repository;

import org.springframework.stereotype.Repository;

import com.flw.moka.entity.helpers.PaymentDealerAuthentication;
import com.flw.moka.entity.helpers.PaymentDealerRequest;
import com.flw.moka.entity.helpers.ProviderPayload;

@Repository
public class ProviderPayloadRepository {
    private ProviderPayload request = new ProviderPayload();

    public ProviderPayload savePayload(PaymentDealerAuthentication paymentDealerAuthentication,
            PaymentDealerRequest paymentDealerRequest) {
        request.setPaymentDealerAuthentication(paymentDealerAuthentication);
        request.setPaymentDealerRequest(paymentDealerRequest);

        return request;
    }
}
