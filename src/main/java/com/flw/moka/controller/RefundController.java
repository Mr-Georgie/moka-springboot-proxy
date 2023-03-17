package com.flw.moka.controller;

import java.net.URISyntaxException;
import java.text.ParseException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flw.moka.entity.PaymentDealerRequest;
import com.flw.moka.entity.ProductRequest;
import com.flw.moka.entity.ProviderPayload;
import com.flw.moka.entity.ProxyResponse;
import com.flw.moka.service.controllers.RefundService;
import com.flw.moka.service.entities.PaymentDealerRequestService;
import com.flw.moka.service.entities.ProviderPayloadService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/refund")
public class RefundController {
    ProviderPayloadService providerPayloadService;
    PaymentDealerRequestService paymentDealerRequestService;
    RefundService refundService;

    @PostMapping(path = "", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ProxyResponse> saveCardParams(@RequestBody ProductRequest productRequest)
            throws URISyntaxException, ParseException {

        PaymentDealerRequest paymentDealerRequest = paymentDealerRequestService.saveRequestPayload(
                productRequest,
                "refund");
        ProviderPayload providerPayload = providerPayloadService
                .savePaymentDealerAuthAndReq(paymentDealerRequest);

        ResponseEntity<ProxyResponse> responseEntity = refundService.sendProviderPayload(providerPayload,
                productRequest);

        return responseEntity;

    }
}
