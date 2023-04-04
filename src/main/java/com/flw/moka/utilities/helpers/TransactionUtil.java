package com.flw.moka.utilities.helpers;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.models.Refunds;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.Meta;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.exception.TransactionNotFoundException;
import com.flw.moka.service.entity_service.TransactionService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class TransactionUtil {

    TransactionService transactionService;
    LogsUtil logsUtil;
    RefundsUtil refundsUtil;

    public void saveTransactionToDatabase(ProductRequest productRequest, ProxyResponse proxyResponse,
            Transaction transaction, String method) {

        TimeUtil timeUtility = new TimeUtil();

        transaction.setResponseCode(proxyResponse.getResponseCode());
        transaction.setResponseMessage(proxyResponse.getResponseMessage());
        transaction.setProvider(proxyResponse.getMeta().getProvider());

        switch (method) {
            case Methods.AUTHORIZE:
                transaction.setAmount(productRequest.getAmount());
                transaction.setCountry("TR");
                transaction.setCurrency(productRequest.getCurrency());
                transaction.setMask(productRequest.getCardNumber());
                transaction.setTimeIn(timeUtility.getDateTime());
                transaction.setPayloadReference(productRequest.getPayloadReference());
                transaction.setTransactionReference(proxyResponse.getMeta().getTransactionReference());
                transaction.setNarration("CARD Transaction");
                if (proxyResponse.getMeta().getExternalReference() != null) {
                    transaction.setExternalReference(productRequest.getExternalReference());
                    transaction.setTransactionStatus(Methods.AUTHORIZE.toUpperCase());
                }
                break;
            case Methods.CAPTURE:
                transaction.setResponseCode(proxyResponse.getResponseCode());
                transaction.setResponseMessage(proxyResponse.getResponseMessage());
                if (proxyResponse.getMeta().getExternalReference() != null) {
                    transaction.setExternalReference(proxyResponse.getMeta().getExternalReference());
                    transaction.setTransactionStatus(method.toUpperCase());
                    transaction.setTimeCaptured(timeUtility.getDateTime());
                }
                break;
            case Methods.VOID:
                transaction.setResponseCode(proxyResponse.getResponseCode());
                transaction.setResponseMessage(proxyResponse.getResponseMessage());
                if (proxyResponse.getMeta().getExternalReference() != null) {
                    transaction.setExternalReference(proxyResponse.getMeta().getExternalReference());
                    transaction.setTransactionStatus(method.toUpperCase());
                    transaction.setTimeVoided(timeUtility.getDateTime());
                }
                break;
            default:
                System.out.println("method not recognized.");
                break;
        }

        transactionService.saveTransaction(transaction);
    }

    public Transaction getTransactionIfExistInDB(ProductRequest productRequest, String method) {
        String transactionReference = productRequest.getTransactionReference();
        TimeUtil timeUtility = new TimeUtil();
        Optional<Transaction> transaction = transactionService.getTransaction(transactionReference);

        if (transaction.isPresent()) {

            if (transaction.get().getTransactionStatus() == null) {
                ProxyResponse proxyResponse = prepareResponseIfTransactionDoesNotExist(transactionReference, method);
                logsUtil.setLogs(proxyResponse, productRequest, method);

                saveTransactionToDatabase(productRequest, proxyResponse, transaction.get(), method);
                throw new TransactionNotFoundException(proxyResponse.getResponseMessage());

            }

            return transaction.get();
        } else {
            ProxyResponse proxyResponse = prepareResponseIfTransactionDoesNotExist(transactionReference, method);
            logsUtil.setLogs(proxyResponse, productRequest, method);

            if (productRequest.getTransactionReference().equalsIgnoreCase("null")) {
                throw new TransactionNotFoundException(proxyResponse.getResponseMessage());
            }

            Transaction nonExisitingTransaction = new Transaction();
            nonExisitingTransaction.setAmount(productRequest.getAmount());
            nonExisitingTransaction.setCountry("TR");
            nonExisitingTransaction.setCurrency(productRequest.getCurrency());
            nonExisitingTransaction.setMask(productRequest.getCardNumber());
            nonExisitingTransaction.setTimeIn(timeUtility.getDateTime());
            nonExisitingTransaction.setTransactionReference(productRequest.getTransactionReference());
            nonExisitingTransaction.setNarration("CARD Transaction");

            if (method.equalsIgnoreCase(Methods.REFUND)) {
                Refunds refund = new Refunds();
                refundsUtil.saveRefundToDataBase(proxyResponse, refund, nonExisitingTransaction);
            } else {
                saveTransactionToDatabase(productRequest, proxyResponse, nonExisitingTransaction, method);
            }

            throw new TransactionNotFoundException(proxyResponse.getResponseMessage());
        }
    }

    static ProxyResponse prepareResponseIfTransactionDoesNotExist(String reference, String method) {
        ProxyResponse proxyResponse = new ProxyResponse();
        Meta meta = new Meta();

        proxyResponse
                .setResponseMessage(
                        method.toUpperCase() + " error: Transaction with ref: " + reference + " does not exist");
        proxyResponse.setResponseCode("RR");
        meta.setProvider("MOKA");
        proxyResponse.setMeta(meta);

        return proxyResponse;
    }
}
