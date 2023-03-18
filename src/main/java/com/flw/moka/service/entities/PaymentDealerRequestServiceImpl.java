package com.flw.moka.service.entities;

import org.springframework.stereotype.Service;

import com.flw.moka.entity.PaymentDealerRequest;
import com.flw.moka.entity.ProductRequest;
import com.flw.moka.exception.NoMethodNamePassedException;
import com.flw.moka.repository.PaymentDealerRequestRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PaymentDealerRequestServiceImpl implements PaymentDealerRequestService {

    PaymentDealerRequestRepository paymentDealerRequestRepository;
    PaymentDealerRequest requestPayload;

    @Override
    public PaymentDealerRequest saveRequestPayload(ProductRequest productRequest, String method) {

        if (method == "authorize") {
            requestPayload = saveAuthPayload(productRequest);
        } else if (method == "capture") {
            requestPayload = saveCapturePayload(productRequest);
        } else if (method == "void") {
            requestPayload = saveVoidPayload(productRequest);
        } else if (method == "refund") {
            requestPayload = saveRefundPayload(productRequest);
        } else {
            throw new NoMethodNamePassedException("Please provide method in service when using this service");
        }

        return requestPayload;
    }

    private PaymentDealerRequest saveAuthPayload(ProductRequest productRequest) {
        Long amount = productRequest.getAmount();
        String cardNumber = productRequest.getCardNo();
        String currency = productRequest.getCurrency();
        String cvcNumber = productRequest.getCvv();
        String expiryMonth = productRequest.getExpiryMonth();
        String expiryYear = productRequest.getExpiryYear();
        String transactionReference = productRequest.getTransactionReference();

        return paymentDealerRequestRepository.setAuthorizePayload(amount, cardNumber, currency, cvcNumber, expiryMonth,
                expiryYear, transactionReference);
    }

    private PaymentDealerRequest saveCapturePayload(ProductRequest productRequest) {
        Long amount = productRequest.getAmount();
        String transactionReference = productRequest.getTransactionReference();

        return paymentDealerRequestRepository.setCapturePayload(amount, transactionReference);
    }

    private PaymentDealerRequest saveVoidPayload(ProductRequest productRequest) {
        Long voidRefundReason = productRequest.getVoidRefundReason();
        String transactionReference = productRequest.getTransactionReference();

        return paymentDealerRequestRepository.setVoidPayload(voidRefundReason, transactionReference);
    }

    private PaymentDealerRequest saveRefundPayload(ProductRequest productRequest) {
        Long amount = productRequest.getVoidRefundReason();
        String transactionReference = productRequest.getTransactionReference();

        return paymentDealerRequestRepository.setVoidPayload(amount, transactionReference);
    }
}
