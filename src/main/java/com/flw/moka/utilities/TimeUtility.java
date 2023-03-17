package com.flw.moka.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeUtility {

    public String getDateTime() {
        return getCurrentTimeUsingDate();
    }

    public Boolean isTransactionUpTo24Hours(String dateString) throws ParseException {
        Long transactionTimeMillis = timeInMilliSec(dateString);

        Long currentTimeMillis = System.currentTimeMillis();

        int oneDayInMilliseconds = 24 * 60 * 60 * 1000;

        Long timeOneDayAgoMillis = currentTimeMillis - oneDayInMilliseconds;

        return timeOneDayAgoMillis >= transactionTimeMillis;
    }

    static String getCurrentTimeUsingDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    static Long timeInMilliSec(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = dateFormat.parse(dateString);

        Long millis = date.getTime();

        return millis;
    }

}
