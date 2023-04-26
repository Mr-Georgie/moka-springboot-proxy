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
import com.flw.moka.service.helper_service.ProviderPayloadService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class VoidRefundRouter {

    VoidService voidService;
    RefundService refundService;
    ProviderPayloadService providerPayloadService;

    public ResponseEntity<ProxyResponse> route(ProductRequest productRequest, 
        Boolean isTransactionUpTo24Hours, Transaction transaction) throws ParseException {
        
        if (isTransactionUpTo24Hours) {
            return sendPayload(productRequest, transaction, Methods.REFUND);
        } else {
            return sendPayload(productRequest, transaction, Methods.VOID);
        }
    }

    private ResponseEntity<ProxyResponse> sendPayload(ProductRequest productRequest, 
        Transaction transaction, String method) throws ParseException {

        ProviderPayload providerPayload = providerPayloadService.createNewProviderPayload(productRequest, method);

        if (method.equalsIgnoreCase(Methods.REFUND)) {
            return refundService.sendProviderPayload(providerPayload, productRequest, transaction, Methods.REFUND);
        } else {
            return voidService.sendProviderPayload(providerPayload, productRequest, transaction, Methods.VOID);
        }
    }

}
