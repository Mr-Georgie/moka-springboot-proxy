package com.flw.moka.utilities.helpers;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class GenerateReferenceUtil {

    public String generateRandom(String prefix) {
        Random randomDigits = new Random();
        return prefix + (randomDigits.nextInt(100000000)) + (randomDigits.nextInt(100000000));
    }
}