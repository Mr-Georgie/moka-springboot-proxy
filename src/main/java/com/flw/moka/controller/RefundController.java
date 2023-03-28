package com.flw.moka.controller;

import java.net.URISyntaxException;
import java.text.ParseException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flw.moka.controller.custom_router.VoidRefundRouter;
import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.request.PaymentDealerRequest;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.request.ProviderPayload;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.service.helper_service.PaymentDealerRequestService;
import com.flw.moka.service.helper_service.ProviderPayloadService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class RefundController {
        ProviderPayloadService providerPayloadService;
        PaymentDealerRequestService paymentDealerRequestService;
        VoidRefundRouter voidRefundRouterUtil;

        @PostMapping(path = "/refund", consumes = "application/json", produces = "application/json")
        public ResponseEntity<ProxyResponse> saveCardParams(@RequestBody ProductRequest productRequest)
                        throws URISyntaxException, ParseException {

                PaymentDealerRequest newPaymentDealerRequest = paymentDealerRequestService.createRequestPayload(
                                productRequest,
                                Methods.REFUND);
                ProviderPayload newProviderPayload = providerPayloadService
                                .savePaymentDealerAuthAndReq(newPaymentDealerRequest);

                return voidRefundRouterUtil.route(productRequest, newProviderPayload);
        }
}
