package com.flw.moka.utilities;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class GenerateReferenceUtil {

    public String generateRandom(String prefix) {
        Random randomDigits = new Random();
        String reference = prefix + (randomDigits.nextInt(100000000)) + (randomDigits.nextInt(100000000));
        return reference;
    }
}