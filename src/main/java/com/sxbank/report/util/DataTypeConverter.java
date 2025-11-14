package com.sxbank.report.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataTypeConverter {
    
    public static Object convertValue(String value, String dataType, String dateFormat) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        
        value = value.trim();
        
        try {
            switch (dataType.toUpperCase()) {
                case "NUMBER":
                    return parseNumber(value);
                    
                case "DATE":
                    return parseDate(value, dateFormat);
                    
                case "CURRENCY":
                    return parseCurrency(value);
                    
                case "TEXT":
                default:
                    return value;
            }
        } catch (Exception e) {
            // If conversion fails, return as text
            return value;
        }
    }
    
    private static Double parseNumber(String value) {
        try {
            return Double.parseDouble(value.replaceAll(",", ""));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cannot convert to number: " + value);
        }
    }
    
    private static Date parseDate(String value, String dateFormat) {
        if (dateFormat == null || dateFormat.trim().isEmpty()) {
            // Try common formats
            String[] formats = {"yyyy-MM-dd", "MM/dd/yyyy", "dd-MM-yyyy", "yyyy/MM/dd"};
            for (String format : formats) {
                try {
                    return new SimpleDateFormat(format).parse(value);
                } catch (ParseException ignored) {
                }
            }
            throw new IllegalArgumentException("Cannot parse date: " + value);
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            return sdf.parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Cannot parse date with format " + dateFormat + ": " + value);
        }
    }
    
    private static BigDecimal parseCurrency(String value) {
        try {
            // Remove currency symbols and commas
            String cleaned = value.replaceAll("[^\\d.-]", "").replace(",", "");
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cannot convert to currency: " + value);
        }
    }
}

