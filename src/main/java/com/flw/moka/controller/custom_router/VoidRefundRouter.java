package com.flw.moka.controller.custom_router;

import java.text.ParseException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.request.ProviderPayload;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.service.controller_service.RefundService;
import com.flw.moka.service.controller_service.VoidService;
import com.flw.moka.utilities.helpers.RefundsUtil;
import com.flw.moka.utilities.helpers.TimeUtil;
import com.flw.moka.utilities.helpers.TransactionUtil;
import com.flw.moka.validation.MethodValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class VoidRefundRouter {

    TransactionUtil transactionUtil;
    RefundsUtil refundsUtil;
    VoidService voidService;
    RefundService refundService;
    MethodValidator methodValidator;

    public ResponseEntity<ProxyResponse> route(ProductRequest productRequest,
            ProviderPayload providerPayload) throws ParseException {

        String method;

        if (productRequest.getAmount() == null) {
            method = Methods.VOID;
        } else {
            method = Methods.REFUND;
        }

        Transaction transaction = transactionUtil.getTransactionIfExistInDB(productRequest, method);
        refundsUtil
                .checkIfRefundExistInDB(productRequest, method);

        String transactionTimeCaptured = transaction.getTimeCaptured();

        TimeUtil timeUtility = new TimeUtil();
        Boolean isTransactionUpTo24Hours = timeUtility.isTransactionUpTo24Hours(transactionTimeCaptured);

        if (isTransactionUpTo24Hours) {

            methodValidator.preventVoidOrRefundIfNotCaptured(Methods.REFUND, transaction);
            ResponseEntity<ProxyResponse> responseEntity = refundService.sendProviderPayload(
                    providerPayload,
                    productRequest, transaction);

            return responseEntity;
        } else {

            methodValidator.preventVoidOrRefundIfNotCaptured(Methods.VOID, transaction);

            ResponseEntity<ProxyResponse> responseEntity = voidService.sendProviderPayload(
                    providerPayload,
                    productRequest, transaction);

            return responseEntity;
        }
    }
}
