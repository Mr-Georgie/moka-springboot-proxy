package com.flw.moka.controller;

import java.net.URISyntaxException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.request.PaymentDealerRequest;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.request.ProviderPayload;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.service.controller_service.CaptureService;
import com.flw.moka.service.helper_service.PaymentDealerRequestService;
import com.flw.moka.service.helper_service.ProviderPayloadService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/moka")
public class CaptureController {

        ProviderPayloadService providerPayloadService;
        PaymentDealerRequestService paymentDealerRequestService;
        CaptureService captureService;

        @PostMapping(path = "/capture", consumes = "application/json", produces = "application/json")
        public ResponseEntity<ProxyResponse> saveCardParams(@RequestBody ProductRequest productRequest)
                        throws URISyntaxException {

                PaymentDealerRequest newPaymentDealerRequest = paymentDealerRequestService.createRequestPayload(
                                productRequest,
                                Methods.CAPTURE);
                ProviderPayload newProviderPayload = providerPayloadService
                                .savePaymentDealerAuthAndReq(newPaymentDealerRequest);

                ResponseEntity<ProxyResponse> responseEntity = captureService.sendProviderPayload(
                                newProviderPayload,
                                productRequest);

                return responseEntity;

        }

}
