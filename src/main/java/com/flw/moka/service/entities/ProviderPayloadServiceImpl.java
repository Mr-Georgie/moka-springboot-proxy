package com.flw.moka.service.entities;

import org.springframework.stereotype.Service;

import com.flw.moka.entity.helpers.PaymentDealerAuthentication;
import com.flw.moka.entity.helpers.PaymentDealerRequest;
import com.flw.moka.entity.helpers.ProviderPayload;
import com.flw.moka.repository.PaymentDealerAuthenticationRepository;
import com.flw.moka.repository.ProviderPayloadRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ProviderPayloadServiceImpl implements ProviderPayloadService {

    ProviderPayloadRepository providerPayloadRepository;
    PaymentDealerAuthenticationRepository paymentDealerAuthenticationRepository;

    @Override
    public ProviderPayload savePaymentDealerAuthAndReq(PaymentDealerRequest paymentDealerRequest) {

        PaymentDealerAuthentication paymentDealerAuthentication = paymentDealerAuthenticationRepository.setPaymentDealerDetails();
        return providerPayloadRepository.savePayload(paymentDealerAuthentication, paymentDealerRequest);
    }
}
