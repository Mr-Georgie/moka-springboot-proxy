package com.flw.moka.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flw.moka.entity.helpers.PaymentDealerRequest;
import com.flw.moka.entity.helpers.ProductRequest;
import com.flw.moka.entity.helpers.ProviderPayload;
import com.flw.moka.entity.helpers.ProxyResponse;
import com.flw.moka.exception.InvalidProductRequestException;
import com.flw.moka.service.controllers.AuthService;
import com.flw.moka.service.entities.PaymentDealerRequestService;
import com.flw.moka.service.entities.ProviderPayloadService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthorizeController {

        ProviderPayloadService providerPayloadService;
        PaymentDealerRequestService paymentDealerRequestService;
        AuthService authService;

        @PostMapping(path = "/authorize", consumes = "application/json", produces = "application/json")
        public ResponseEntity<ProxyResponse> saveCardParams(@Valid @RequestBody ProductRequest productRequest,
                        BindingResult bindingResult)
                        throws Exception {

                if (bindingResult.hasErrors()) {
                        throw new InvalidProductRequestException(bindingResult);
                }

                PaymentDealerRequest paymentDealerRequest = paymentDealerRequestService.saveRequestPayload(
                                productRequest,
                                "authorize");
                ProviderPayload providerPayload = providerPayloadService
                                .savePaymentDealerAuthAndReq(paymentDealerRequest);

                ResponseEntity<ProxyResponse> responseEntity = authService.sendProviderPayload(providerPayload,
                                productRequest);

                return responseEntity;

        }

}