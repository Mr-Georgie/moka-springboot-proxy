package com.flw.moka.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flw.moka.entity.helpers.Methods;
import com.flw.moka.entity.helpers.PaymentDealerRequest;
import com.flw.moka.entity.helpers.ProductRequest;
import com.flw.moka.entity.helpers.ProviderPayload;
import com.flw.moka.entity.helpers.ProxyResponse;
import com.flw.moka.exception.InvalidProductRequestException;
import com.flw.moka.service.controllers.AuthService;
import com.flw.moka.service.entities.PaymentDealerRequestService;
import com.flw.moka.service.entities.ProviderPayloadService;
import com.flw.moka.utilities.GenerateReferenceUtil;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthorizeController {

        ProviderPayloadService providerPayloadService;
        PaymentDealerRequestService paymentDealerRequestService;
        AuthService authService;
        GenerateReferenceUtil generateReferenceUtil;

        @PostMapping(path = "/authorize", consumes = "application/json", produces = "application/json")
        public ResponseEntity<ProxyResponse> saveCardParams(@Valid @RequestBody ProductRequest productRequest,
                        BindingResult bindingResult)
                        throws Exception {

                if (bindingResult.hasErrors()) {
                        throw new InvalidProductRequestException(bindingResult);
                }

                productRequest.setTransactionReference(generateReferenceUtil.generateRandom("TUR"));

                PaymentDealerRequest newPaymentDealerRequest = paymentDealerRequestService.createRequestPayload(
                                productRequest,
                                Methods.AUTHORIZE);

                ProviderPayload newProviderPayload = providerPayloadService
                                .savePaymentDealerAuthAndReq(newPaymentDealerRequest);

                ResponseEntity<ProxyResponse> responseEntity = authService.sendProviderPayload(
                                newProviderPayload,
                                productRequest);

                return responseEntity;

        }

}