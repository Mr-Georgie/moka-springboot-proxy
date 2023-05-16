package com.flw.moka.utilities.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeUtil {

    public String getDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    public Boolean isTransactionUpTo24Hours(String dateString) throws ParseException {
        
        Long transactionTimeMillis = timeInMilliSec(dateString);

        long currentTimeMillis = System.currentTimeMillis();

        int oneDayInMilliseconds = 24 * 60 * 60 * 1000;

        long timeOneDayAgoMillis = currentTimeMillis - oneDayInMilliseconds;

        return timeOneDayAgoMillis >= transactionTimeMillis;
    }

    private static Long timeInMilliSec(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = dateFormat.parse(dateString);

        return date.getTime();
    }

}
