package com.example.omnitrem.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    private static final SimpleDateFormat isoFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", Locale.US);
    private static final SimpleDateFormat isoFormatterShort = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    private static final SimpleDateFormat friendlyFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    static {
        isoFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        isoFormatterShort.setTimeZone(TimeZone.getTimeZone("UTC"));
        friendlyFormatter.setTimeZone(TimeZone.getDefault());
    }

    public static String formatFriendlyDate(String isoDateString) {
        if (isoDateString == null || isoDateString.isEmpty()) {
            return "Data indisponível";
        }
        try {
            Date date;
            try {
                date = isoFormatter.parse(isoDateString);
            } catch (ParseException e) {
                date = isoFormatterShort.parse(isoDateString);
            }
            return friendlyFormatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return isoDateString;
        }
    }
}