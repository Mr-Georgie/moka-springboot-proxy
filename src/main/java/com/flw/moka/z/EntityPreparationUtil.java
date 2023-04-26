package com.flw.moka.z;
// package com.flw.moka.utilities.helpers;

// import com.flw.moka.entity.constants.Methods;
// import com.flw.moka.entity.models.Logs;
// import com.flw.moka.entity.models.Refunds;
// import com.flw.moka.entity.models.Transaction;
// import com.flw.moka.entity.request.ProductRequest;
// import com.flw.moka.entity.response.ProxyResponse;
// import com.flw.moka.service.entity_service.TransactionService;
// import com.google.gson.Gson;

// import lombok.AllArgsConstructor;

// @AllArgsConstructor
// public class EntityPreparationUtil {

// String method;
// TransactionService transactionService;

// public EntityPreparationUtil(String method) {
// this.method = method;
// }

// public Logs setLogs(ProxyResponse proxyResponse, ProductRequest
// productRequest) {

// TimeUtil timeUtility = new TimeUtil();
// Logs log = new Logs();
// Gson gson = new Gson();

// String jsonProductRequest = gson.toJson(productRequest);
// String jsonProxyResponse = gson.toJson(proxyResponse);

// log.setExternalReference(proxyResponse.getExternalReference());
// log.setBody(jsonProductRequest);
// log.setMethod(method);
// log.setResponse(jsonProxyResponse);
// log.setTransactionReference(proxyResponse.getTransactionReference());
// log.setTimeIn(timeUtility.getDateTime());

// return log;
// }

// public Transaction setTransaction(ProductRequest productRequest,
// ProxyResponse proxyResponse,
// Transaction transaction) {

// TimeUtil timeUtility = new TimeUtil();

// transaction.setResponseCode(proxyResponse.getCode());
// transaction.setResponseMessage(proxyResponse.getMessage());
// transaction.setProvider(proxyResponse.getProvider());

// switch (method) {
// case Methods.AUTHORIZE:
// transaction.setAmount(productRequest.getAmount());
// transaction.setCountry("TR");
// transaction.setCurrency(productRequest.getCurrency());
// transaction.setMask(productRequest.getCardNumber());
// transaction.setTimeIn(timeUtility.getDateTime());
// transaction.setTransactionReference(productRequest.getTransactionReference());
// transaction.setNarration("CARD Transaction");
// if (proxyResponse.getExternalReference() != "null") {
// transaction.setExternalReference(productRequest.getExternalReference());
// transaction.setTransactionStatus(Methods.AUTHORIZE.toUpperCase());
// }
// break;
// case Methods.CAPTURE:
// transaction.setResponseCode(proxyResponse.getCode());
// transaction.setResponseMessage(proxyResponse.getMessage());
// if (proxyResponse.getExternalReference() != "null") {
// transaction.setExternalReference(proxyResponse.getExternalReference());
// transaction.setTransactionStatus(method.toUpperCase());
// transaction.setTimeCaptured(timeUtility.getDateTime());
// }
// break;
// case Methods.VOID:
// transaction.setResponseCode(proxyResponse.getCode());
// transaction.setResponseMessage(proxyResponse.getMessage());
// if (proxyResponse.getExternalReference() != "null") {
// transaction.setExternalReference(proxyResponse.getExternalReference());
// transaction.setTransactionStatus(method.toUpperCase());
// transaction.setTimeVoided(timeUtility.getDateTime());
// }
// break;
// default:
// System.out.println("method not recognized.");
// break;
// }

// return transaction;
// }

// public Refunds setRefund(Transaction transaction, ProxyResponse
// proxyResponse) {

// Refunds refund = new Refunds();
// TimeUtil timeUtility = new TimeUtil();

// refund.setExternalReference(proxyResponse.getExternalReference());
// refund.setTransactionReference(transaction.getTransactionReference());
// refund.setResponseCode(proxyResponse.getCode());
// refund.setResponseMessage(proxyResponse.getMessage());
// refund.setTimeRefunded(timeUtility.getDateTime());
// refund.setAmount(transaction.getAmount());
// refund.setCountry(transaction.getCountry());
// refund.setCurrency(transaction.getCurrency());
// refund.setMask(transaction.getMask());
// refund.setNarration("Card transaction");
// refund.setProvider("Moka");

// return refund;
// }

// // public Transaction updateTransactionStatus(ProductRequest productRequest,
// // Transaction transaction,
// // ProxyResponse proxyResponse) {

// // TimeUtil timeUtility = new TimeUtil();

// //
// transaction.setTransactionReference(productRequest.getTransactionReference());
// // transaction.setResponseCode(proxyResponse.getCode());
// // transaction.setResponseMessage(proxyResponse.getMessage());

// // if (proxyResponse.getExternalReference() != "null") {
// // transaction.setExternalReference(proxyResponse.getExternalReference());
// // transaction.setTransactionStatus(method.toUpperCase());

// // if (method.equalsIgnoreCase(Methods.CAPTURE)) {
// // transaction.setTimeCaptured(timeUtility.getDateTime());
// // } else if (method.equalsIgnoreCase(Methods.REFUND)) {
// // transaction.setTimeRefunded(timeUtility.getDateTime());
// // } else if (method.equalsIgnoreCase(Methods.VOID)) {
// // transaction.setTimeVoided(timeUtility.getDateTime());
// // }
// // }

// // return transaction;
// // }

// }
