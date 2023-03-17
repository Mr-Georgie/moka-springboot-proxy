package com.flw.moka.controller;

import java.net.URISyntaxException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flw.moka.entity.PaymentDealerRequest;
import com.flw.moka.entity.ProductRequest;
import com.flw.moka.entity.ProviderPayload;
import com.flw.moka.entity.ProxyResponse;
import com.flw.moka.service.controllers.CaptureService;
import com.flw.moka.service.entities.PaymentDealerRequestService;
import com.flw.moka.service.entities.ProviderPayloadService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/capture")
public class CaptureController {

        ProviderPayloadService providerPayloadService;
        PaymentDealerRequestService paymentDealerRequestService;
        CaptureService captureService;

        @PostMapping(path = "", consumes = "application/json", produces = "application/json")
        public ResponseEntity<ProxyResponse> saveCardParams(@RequestBody ProductRequest productRequest)
                        throws URISyntaxException {

                PaymentDealerRequest paymentDealerRequest = paymentDealerRequestService.saveRequestPayload(
                                productRequest,
                                "capture");
                ProviderPayload providerPayload = providerPayloadService
                                .savePaymentDealerAuthAndReq(paymentDealerRequest);

                ResponseEntity<ProxyResponse> responseEntity = captureService.sendProviderPayload(providerPayload,
                                productRequest);

                return responseEntity;

        }

}
