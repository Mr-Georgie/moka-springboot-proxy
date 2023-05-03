package com.flw.moka.service.entity_service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.models.Refunds;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.Meta;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.exception.InvalidRefundRequestException;
import com.flw.moka.exception.TransactionMethodAlreadyDoneException;
import com.flw.moka.repository.RefundsRepository;
import com.flw.moka.service.helper_service.ProxyResponseService;
import com.flw.moka.utilities.entity.LogsUtil;
import com.flw.moka.utilities.entity.RefundsUtil;
import com.flw.moka.utilities.helpers.GenerateReferenceUtil;
import com.flw.moka.utilities.helpers.TimeUtil;
import com.flw.moka.validation.MethodValidator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RefundsEntityServiceImpl implements RefundsEntityService {

    RefundsRepository refundsRepository;
    ProxyResponseService proxyResponseService;
    MethodValidator methodValidator;
    GenerateReferenceUtil generateReferenceUtil;
    RefundsUtil refundsUtil;
    LogsUtil logsUtil;

    @Override
    public void saveRefund(ProxyResponse proxyResponse,
            Refunds existingRefund, Transaction transaction) {

        TimeUtil timeUtility = new TimeUtil();

        Refunds refund = refundsUtil.updateRefund(proxyResponse, existingRefund, transaction);

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

        saveRefundToDataBase(refund);
    }
    
    @Override
    public Refunds getRefund(ProductRequest productRequest, Transaction transaction) {

        String method = Methods.REFUND;
        String reference = productRequest.getTransactionReference();

        Optional<Refunds> optionalRefund = findLastTransactionOccurrence(reference);

        if (optionalRefund.isPresent()) {
            Refunds foundRefund = optionalRefund.get();
            
            ProxyResponse proxyResponse = refundsUtil.checkFoundRefundBalanceAndReturnResponse(foundRefund, productRequest, method);
            
            if (proxyResponse != null) {
                logsUtil.setLogs(proxyResponse, productRequest, method);
                saveRefund(proxyResponse, foundRefund, null);
                throw new TransactionMethodAlreadyDoneException(proxyResponse.getResponseMessage());
            }

            Refunds computedRefunds = refundsUtil.computeRefundedAmount(foundRefund, productRequest);

            throwExceptionIfComputedRefundIsNull(computedRefunds, foundRefund, productRequest);
            
            return computedRefunds;
        }

        return createNewRefund(productRequest, transaction);
    }
    
    @Override
    public Optional<Refunds> getRefundByTransactionReference(String reference) {
        Optional<Refunds> refund = refundsRepository.findByTransactionReference(reference);
        return refund;
    }
    
    @Override
    public Optional<Refunds> findLastTransactionOccurrence(String reference) {
        Optional<Refunds> refund = refundsRepository.findFirstByTransactionReferenceOrderByIdDesc(reference);
        return refund;
    }

    private Refunds saveRefundToDataBase(Refunds refund) {
        return refundsRepository.save(refund);
    }

    private Refunds createNewRefund(ProductRequest productRequest, Transaction transaction){
        Refunds refund = new Refunds();
        String reference = productRequest.getTransactionReference();

        Long requestedAmount = productRequest.getAmount();
        Long transactionAmount = transaction.getAmount();

        if (requestedAmount == null) {
            refund.setRefundedAmount(transactionAmount);
        } else {
            refund.setRefundedAmount(requestedAmount);
        }

        refund.setTransactionReference(reference);
        refund.setResponseCode("RR");
        refund.setResponseMessage("Initiating refund...");

        return refund;
    }

    private void throwExceptionIfComputedRefundIsNull(Refunds computedRefund, Refunds foundRefund, 
        ProductRequest productRequest ){
        String reference = productRequest.getTransactionReference();

        if (computedRefund ==  null) {
            String message = "REFUND Failed: Invalid Amount";
            ProxyResponse proxyResponse = refundsUtil.prepareFailedResponse(
                                        reference, 
                                        Methods.REFUND, 
                                        message);

            logsUtil.setLogs(proxyResponse, productRequest, Methods.REFUND);

            saveRefund(proxyResponse, foundRefund, null);

            throw new InvalidRefundRequestException(proxyResponse.getResponseMessage());
        }
    }
}
