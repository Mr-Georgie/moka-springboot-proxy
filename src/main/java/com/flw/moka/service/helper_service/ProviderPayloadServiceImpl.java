package com.flw.moka.service.helper_service;

import org.springframework.stereotype.Service;

import com.flw.moka.entity.request.PaymentDealerAuthentication;
import com.flw.moka.entity.request.PaymentDealerRequest;
import com.flw.moka.entity.request.ProviderPayload;
import com.flw.moka.utilities.request.PaymentDealerAuthenticationUtil;
import com.flw.moka.utilities.request.ProviderPayloadUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ProviderPayloadServiceImpl implements ProviderPayloadService {

    ProviderPayloadUtil providerPayloadRepository;
    PaymentDealerAuthenticationUtil paymentDealerAuthenticationRepository;

    @Override
    public ProviderPayload savePaymentDealerAuthAndReq(PaymentDealerRequest paymentDealerRequest) {

        PaymentDealerAuthentication paymentDealerAuthentication = paymentDealerAuthenticationRepository.setPaymentDealerDetails();
        return providerPayloadRepository.savePayload(paymentDealerAuthentication, paymentDealerRequest);
    }
}
