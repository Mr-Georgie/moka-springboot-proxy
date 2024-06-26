package com.flw.moka.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.request.ProviderPayload;
import com.flw.moka.entity.response.StatusCheckResponse;
import com.flw.moka.service.helper_service.ProviderPayloadService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/moka")
public class StatusCheckController {

    ProviderPayloadService providerPayloadService;

    @GetMapping("/status-check/{reference}")
    public ResponseEntity<StatusCheckResponse> statusCheck(@PathVariable String reference) {

        ProductRequest productRequest = new ProductRequest();

        productRequest.setTransactionReference(reference);

        ProviderPayload newProviderPayload = providerPayloadService
                        .createNewProviderPayload(productRequest, Methods.STATUS);

        return ResponseEntity.status(HttpStatus.OK).body(new StatusCheckResponse());
        
    }
}
