package com.flw.moka.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.request.PaymentDealerRequest;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.request.ProviderPayload;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.exception.InvalidProductRequestException;
import com.flw.moka.service.controller_service.AuthorizeService;
import com.flw.moka.service.helper_service.PaymentDealerRequestService;
import com.flw.moka.service.helper_service.ProviderPayloadService;
import com.flw.moka.utilities.helpers.GenerateReferenceUtil;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/moka")
public class AuthorizeController {

        ProviderPayloadService providerPayloadService;
        PaymentDealerRequestService paymentDealerRequestService;
        AuthorizeService authService;
        GenerateReferenceUtil generateReferenceUtil;

        @PostMapping(path = "/authorize", consumes = "application/json", produces = "application/json")
        public ResponseEntity<ProxyResponse> saveCardParams(@Valid @RequestBody ProductRequest productRequest,
                        BindingResult bindingResult) throws Exception {

                if (bindingResult.hasErrors()) {
                        throw new InvalidProductRequestException(bindingResult);
                }

                productRequest.setTransactionReference(generateReferenceUtil.generateRandom("MRN"));

                PaymentDealerRequest newPaymentDealerRequest = paymentDealerRequestService.createRequestPayload(
                                productRequest, Methods.AUTHORIZE);

                ProviderPayload newProviderPayload = providerPayloadService
                                .savePaymentDealerAuthAndReq(newPaymentDealerRequest);

                ResponseEntity<ProxyResponse> responseEntity = authService.sendProviderPayload(
                                newProviderPayload, productRequest);

                return responseEntity;

        }

}