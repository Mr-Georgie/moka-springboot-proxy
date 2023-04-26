package com.flw.moka.service.helper_service;

import org.springframework.stereotype.Service;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.request.PaymentDealerRequest;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.exception.InvalidMethodNamePassedException;
import com.flw.moka.utilities.request.PaymentDealerRequestUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PaymentDealerRequestServiceImpl implements PaymentDealerRequestService {

    PaymentDealerRequestUtil paymentDealerRequestUtil;
    PaymentDealerRequest requestPayload;

    @Override
    public PaymentDealerRequest createRequestPayload(ProductRequest productRequest, String method) {

        switch (method) {
            case Methods.AUTHORIZE:
                requestPayload = saveAuthPayload(productRequest);
                break;
            case Methods.CAPTURE:
                requestPayload = saveCapturePayload(productRequest);
                break;
            case Methods.VOID:
                requestPayload = saveVoidPayload(productRequest);
                break;
            case Methods.REFUND:
                requestPayload = saveRefundPayload(productRequest);
                break;
            case Methods.STATUS:
                requestPayload = saveStatusPayload(productRequest);
                break;
            default:
                throw new InvalidMethodNamePassedException("Please provide a method name when using this service");
        }

        return requestPayload;
    }

    private PaymentDealerRequest saveAuthPayload(ProductRequest productRequest) {
        Long amount = productRequest.getAmount();
        String cardNumber = productRequest.getCardNumber();
        String currency = productRequest.getCurrency();
        String cvcNumber = productRequest.getCvv();
        String expiryMonth = productRequest.getExpiryMonth();
        String expiryYear = productRequest.getExpiryYear();
        String transactionReference = productRequest.getTransactionReference();

        return paymentDealerRequestUtil.setAuthorizePayload(amount, cardNumber, currency, cvcNumber, expiryMonth,
                expiryYear, transactionReference);
    }

    private PaymentDealerRequest saveCapturePayload(ProductRequest productRequest) {
        Long amount = productRequest.getAmount();
        String transactionReference = productRequest.getTransactionReference();

        return paymentDealerRequestUtil.setCapturePayload(amount, transactionReference);
    }

    private PaymentDealerRequest saveVoidPayload(ProductRequest productRequest) {
        int voidRefundReason = 2;
        String transactionReference = productRequest.getTransactionReference();

        return paymentDealerRequestUtil.setVoidPayload(voidRefundReason, transactionReference);
    }

    private PaymentDealerRequest saveRefundPayload(ProductRequest productRequest) {
        Long amount = productRequest.getAmount();
        String transactionReference = productRequest.getTransactionReference();

        return paymentDealerRequestUtil.setRefundPayload(amount, transactionReference);
    }

    private PaymentDealerRequest saveStatusPayload(ProductRequest productRequest) {
        String transactionReference = productRequest.getTransactionReference();

        return paymentDealerRequestUtil.setStatusPayload(transactionReference);
    }
}
