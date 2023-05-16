package com.flw.moka.utilities.request;

import org.springframework.stereotype.Repository;

import com.flw.moka.entity.request.PaymentDealerAuthentication;
import com.flw.moka.entity.request.PaymentDealerRequest;
import com.flw.moka.entity.request.ProviderPayload;

@Repository
public class ProviderPayloadUtil {
    private final ProviderPayload request = new ProviderPayload();

    public ProviderPayload savePayload(PaymentDealerAuthentication paymentDealerAuthentication,
            PaymentDealerRequest paymentDealerRequest) {
        request.setPaymentDealerAuthentication(paymentDealerAuthentication);
        request.setPaymentDealerRequest(paymentDealerRequest);

        return request;
    }
}
