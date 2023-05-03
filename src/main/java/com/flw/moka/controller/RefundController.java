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
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.service.entity_service.TransactionService;
import com.flw.moka.utilities.helpers.TimeUtil;
import com.flw.moka.validation.MethodValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/moka")
public class RefundController {
    
    TransactionService transactionService;
    VoidRefundRouter voidRefundRouter;
    MethodValidator methodValidator;

    @PostMapping(path = "/refund", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ProxyResponse> saveCardParams(@RequestBody ProductRequest productRequest)
        throws URISyntaxException, ParseException {

        Transaction transaction = transactionService.getTransaction(productRequest, Methods.REFUND);

        methodValidator.preventVoidOrRefundIfNotCaptured(transaction);

        String transactionTimeCaptured = transaction.getTimeCaptured();

        TimeUtil timeUtility = new TimeUtil();
        Boolean isTransactionUpTo24Hours = timeUtility.isTransactionUpTo24Hours(transactionTimeCaptured);

        return voidRefundRouter.route(productRequest, isTransactionUpTo24Hours, transaction);
    }
}
