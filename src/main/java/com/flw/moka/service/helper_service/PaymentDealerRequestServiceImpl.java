package com.flw.moka.service.helper_service;

import org.springframework.stereotype.Service;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.request.PaymentDealerRequest;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.exception.NoMethodNamePassedException;
import com.flw.moka.utilities.request.PaymentDealerRequestUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PaymentDealerRequestServiceImpl implements PaymentDealerRequestService {

    PaymentDealerRequestUtil paymentDealerRequestUtil;
    PaymentDealerRequest requestPayload;

    @Override
    public PaymentDealerRequest createRequestPayload(ProductRequest productRequest, String method) {

        if (method.equalsIgnoreCase(Methods.AUTHORIZE)) {
            requestPayload = saveAuthPayload(productRequest);
        } else if (method.equalsIgnoreCase(Methods.CAPTURE)) {
            requestPayload = saveCapturePayload(productRequest);
        } else if (method.equalsIgnoreCase(Methods.VOID)) {
            requestPayload = saveVoidPayload(productRequest);
        } else if (method.equalsIgnoreCase(Methods.REFUND)) {
            requestPayload = saveRefundPayload(productRequest);
        } else if (method.equalsIgnoreCase(Methods.STATUS)) {
            requestPayload = saveStatusPayload(productRequest);
        } else {
            throw new NoMethodNamePassedException("Please provide method in service when using this service");
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
