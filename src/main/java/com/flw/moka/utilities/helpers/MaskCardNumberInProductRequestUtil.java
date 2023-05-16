package com.flw.moka.utilities.helpers;

import org.springframework.stereotype.Component;

import com.flw.moka.entity.request.ProductRequest;

@Component
public class MaskCardNumberInProductRequestUtil {

    public ProductRequest mask(ProductRequest productRequest) {
        String maskedCardNumber = maskHandler(productRequest.getCardNumber());

        productRequest.setCardNumber(maskedCardNumber);
        productRequest.setCvv("***");
        return productRequest;
    }

    private static String maskHandler(String cardNumber) {
        String firstSixChars = cardNumber.substring(0, 6);
        String lastFourChars = cardNumber.substring(cardNumber.length() - 4);

        return firstSixChars + "******" + lastFourChars;
    }

}
