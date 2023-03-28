package com.flw.moka.utilities.request;

import org.springframework.stereotype.Repository;

import com.flw.moka.entity.request.PaymentDealerRequest;

@Repository
public class PaymentDealerRequestUtil {
    private PaymentDealerRequest paymentDealerRequest = new PaymentDealerRequest();

    public PaymentDealerRequest setAuthorizePayload(Long amount, String cardNumber, String currency,
            String cvcNumber, String expiryMonth, String expiryYear, String transactionReference) {
        paymentDealerRequest.setAmount(amount);
        paymentDealerRequest.setCardNumber(cardNumber);
        paymentDealerRequest.setCurrency(currency);
        paymentDealerRequest.setCvcNumber(cvcNumber);
        paymentDealerRequest.setExpMonth(expiryMonth);
        paymentDealerRequest.setExpYear(expiryYear);
        paymentDealerRequest.setOtherTrxCode(transactionReference);
        paymentDealerRequest.setIsPoolPayment(0);
        paymentDealerRequest.setIsTokenized(0);
        paymentDealerRequest.setSoftware("Possimulation");
        paymentDealerRequest.setIsPreAuth(1);

        return paymentDealerRequest;
    }

    public PaymentDealerRequest setCapturePayload(Long amount, String transactionReference) {
        paymentDealerRequest.setAmount(amount);
        paymentDealerRequest.setOtherTrxCode(transactionReference);

        return paymentDealerRequest;
    }

    public PaymentDealerRequest setVoidPayload(int voidRefundReason, String transactionReference) {
        paymentDealerRequest.setVoidRefundReason(voidRefundReason);
        paymentDealerRequest.setOtherTrxCode(transactionReference);

        return paymentDealerRequest;
    }

    public PaymentDealerRequest setRefundPayload(Long amount, String transactionReference) {
        paymentDealerRequest.setAmount(amount);
        paymentDealerRequest.setOtherTrxCode(transactionReference);

        return paymentDealerRequest;
    }

    public PaymentDealerRequest setStatusPayload(String transactionReference) {
        paymentDealerRequest.setOtherTrxCode(transactionReference);

        return paymentDealerRequest;
    }
}
