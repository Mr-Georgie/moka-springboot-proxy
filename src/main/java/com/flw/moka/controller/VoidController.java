package com.flw.moka.controller;

import java.net.URISyntaxException;
import java.text.ParseException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flw.moka.entity.helpers.PaymentDealerRequest;
import com.flw.moka.entity.helpers.ProductRequest;
import com.flw.moka.entity.helpers.ProviderPayload;
import com.flw.moka.entity.helpers.ProxyResponse;
import com.flw.moka.service.controllers.VoidService;
import com.flw.moka.service.entities.PaymentDealerRequestService;
import com.flw.moka.service.entities.ProviderPayloadService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class VoidController {
        ProviderPayloadService providerPayloadService;
        PaymentDealerRequestService paymentDealerRequestService;
        VoidService voidService;

        @PostMapping(path = "/void", consumes = "application/json", produces = "application/json")
        public ResponseEntity<ProxyResponse> saveCardParams(@RequestBody ProductRequest productRequest)
                        throws URISyntaxException, ParseException {

                PaymentDealerRequest paymentDealerRequest = paymentDealerRequestService.saveRequestPayload(
                                productRequest,
                                "void");
                ProviderPayload providerPayload = providerPayloadService
                                .savePaymentDealerAuthAndReq(paymentDealerRequest);

                ResponseEntity<ProxyResponse> responseEntity = voidService.sendProviderPayload(providerPayload,
                                productRequest);

                return responseEntity;

        }
}
