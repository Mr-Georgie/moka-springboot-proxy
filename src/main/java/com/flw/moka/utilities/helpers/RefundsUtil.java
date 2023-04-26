package com.flw.moka.utilities.helpers;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.models.Refunds;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.Meta;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.exception.TransactionMethodAlreadyDoneException;
import com.flw.moka.service.entity_service.RefundsEntityService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class RefundsUtil {

    RefundsEntityService refundsEntityService;
    LogsUtil logsUtil;
    GenerateReferenceUtil generateReferenceUtil;

    public void saveRefundToDataBase(ProxyResponse proxyResponse,
            Refunds existingRefund, Transaction transaction) {

        TimeUtil timeUtility = new TimeUtil();

        Refunds refund = computeBalance(proxyResponse, existingRefund, transaction);

        if (transaction == null) {
            refund.setCurrency(existingRefund.getCurrency());
            refund.setMask(existingRefund.getMask());
            refund.setTransactionReference(existingRefund.getTransactionReference());
            refund.setPayloadReference(existingRefund.getPayloadReference());
            refund.setExternalReference(existingRefund.getExternalReference());
        } else {
            refund.setCurrency(transaction.getCurrency());
            refund.setMask(transaction.getMask());
            refund.setTransactionReference(transaction.getTransactionReference());
            refund.setPayloadReference(transaction.getPayloadReference());
            refund.setExternalReference(transaction.getExternalReference());
        }

        refund.setResponseCode(proxyResponse.getResponseCode());
        refund.setResponseMessage(proxyResponse.getResponseMessage());
        refund.setProvider(proxyResponse.getMeta().getProvider());
        refund.setRefundId(proxyResponse.getMeta().getRefundId());
        
        refund.setTimeRefunded(timeUtility.getDateTime());

        refund.setRefundReference(generateReferenceUtil.generateRandom("REF"));
        refund.setNarration("CARD Transaction");
        refund.setCountry("TR");

        Meta meta = proxyResponse.getMeta();

        meta.setTransactionReference(refund.getRefundReference());
        proxyResponse.setMeta(meta);

        refundsEntityService.saveRefund(refund);
    }

    public Refunds checkIfRefundExistInDB(ProductRequest productRequest) {

        String method = Methods.REFUND;
        String refundReference = productRequest.getTransactionReference();

        Optional<Refunds> optionalRefund = refundsEntityService
            .findLastTransactionOccurrence(refundReference);

        if (optionalRefund.isPresent()) {
            Refunds foundRefund = optionalRefund.get();
            String responseCode = foundRefund.getResponseCode();
            
            if (!responseCode.equals("RR")) {

                if (foundRefund.getBalance() == 0) {
                    String message = "This has been refunded";
                    ProxyResponse proxyResponse = prepareResponseIfSuccessfulRefundsExist(refundReference, method, message);
                    logsUtil.setLogs(proxyResponse, productRequest, method);
                    saveRefundToDataBase(proxyResponse, foundRefund, null);
                    throw new TransactionMethodAlreadyDoneException(message);
                }
                
            }
            return computeRefundedAmount(foundRefund, productRequest);
        }

        Refunds refund = new Refunds();
        Long requestedAmount = productRequest.getAmount();

        if (requestedAmount != null) {
                refund.setRefundedAmount(requestedAmount);
        }

        refund.setRefundReference(refundReference);
        refund.setResponseCode("RR");
        refund.setResponseMessage("Initiating refund...");

        return refund;
    }

    private Refunds computeBalance(ProxyResponse proxyResponse, Refunds refund, Transaction transaction) {

        Refunds newRefund = new Refunds();

        if (transaction != null) {
            if (!proxyResponse.getResponseCode().equalsIgnoreCase("RR")) {
                if (refund.getRefundedAmount() == null)
                    newRefund.setRefundedAmount(transaction.getAmount());
                else
                    newRefund.setRefundedAmount(refund.getRefundedAmount());

                if (refund.getBalance() == null || refund.getBalance() != 0) {
                    Long balance = transaction.getAmount() - newRefund.getRefundedAmount();
                    newRefund.setBalance(balance);
                }

                return newRefund;
            } else {
                newRefund.setRefundedAmount(Long.valueOf(0));

                if (refund.getBalance() == null) {
                    newRefund.setBalance(transaction.getAmount());
                } else {
                    newRefund.setBalance(refund.getBalance());
                }
            }

            
        } else {
            newRefund.setRefundedAmount(refund.getRefundedAmount());
            newRefund.setBalance(refund.getBalance());
        }

        return newRefund;
    }

    private Refunds computeRefundedAmount(Refunds refund, ProductRequest productRequest) {

        Refunds newRefund = new Refunds();

        Long requestedAmount = productRequest.getAmount();
        Long totalAmountRefundable = refund.getRefundedAmount() +
                refund.getBalance();

        if (totalAmountRefundable.equals(refund.getRefundedAmount())) {
            return refund;
        }

        if(requestedAmount > refund.getBalance()) {
            String message = "REFUND Failed: Invalid Amount " + requestedAmount;
            ProxyResponse proxyResponse = prepareResponseIfSuccessfulRefundsExist(refund.getTransactionReference(), Methods.REFUND, message);
            logsUtil.setLogs(proxyResponse, productRequest, Methods.REFUND);

            saveRefundToDataBase(proxyResponse, refund, null);

            throw new TransactionMethodAlreadyDoneException(proxyResponse.getResponseMessage());
        }

        Long updatedRefundAmount = requestedAmount +
                refund.getRefundedAmount();

        newRefund.setRefundedAmount(updatedRefundAmount);
        newRefund.setResponseCode(refund.getResponseCode());
        newRefund.setResponseMessage(refund.getResponseMessage());
        newRefund.setProvider(refund.getProvider());

        newRefund.setCurrency(refund.getCurrency());
        newRefund.setMask(refund.getMask());
        newRefund.setTimeRefunded(refund.getTimeRefunded());
        newRefund.setTransactionReference(refund.getTransactionReference());
        newRefund.setRefundReference(refund.getRefundReference());
        newRefund.setExternalReference(refund.getExternalReference());
        newRefund.setNarration(refund.getNarration());
        newRefund.setCountry(refund.getCountry());

        return newRefund;
    }

    private ProxyResponse prepareResponseIfSuccessfulRefundsExist(String reference, String method, String message) {
        ProxyResponse proxyResponse = new ProxyResponse();
        Meta meta = new Meta();

        meta.setProvider("MOKA");
        proxyResponse.setResponseCode("RR");
        proxyResponse.setMeta(meta);
        proxyResponse.setResponseMessage(message);

        return proxyResponse;
    }
}