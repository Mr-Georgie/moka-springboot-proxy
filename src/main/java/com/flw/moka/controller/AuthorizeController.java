package com.flw.moka.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.request.ProviderPayload;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.exception.InvalidProductRequestException;
import com.flw.moka.service.controller_service.AuthorizeService;
import com.flw.moka.service.helper_service.ProviderPayloadService;
import com.flw.moka.utilities.helpers.GenerateReferenceUtil;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/moka")
public class AuthorizeController {

    ProviderPayloadService providerPayloadService;
    AuthorizeService authService;
    GenerateReferenceUtil generateReferenceUtil;

    @PostMapping(path = "/authorize", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ProxyResponse> authorize(@Valid @RequestBody ProductRequest productRequest,
                                                        BindingResult bindingResult) {

        String method = Methods.AUTHORIZE;

        if (bindingResult.hasErrors()) {
            throw new InvalidProductRequestException(bindingResult);
        }

        productRequest.setTransactionReference(generateReferenceUtil.generateRandom("MRN"));

        ProviderPayload newProviderPayload = providerPayloadService
                .createNewProviderPayload(productRequest, method);

        return authService.sendProviderPayload(
                newProviderPayload, productRequest, method);

    }

}