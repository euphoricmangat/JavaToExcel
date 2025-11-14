package com.sxbank.report.parser;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {
    
    public static List<String[]> parseFile(String filePath, String separator, boolean hasHeader) throws IOException {
        List<String[]> rows = new ArrayList<>();
        
        char separatorChar = parseSeparator(separator);
        
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            // OpenCSV doesn't directly support custom separator in CSVReader constructor
            // We'll use a different approach
            List<String[]> allRows = reader.readAll();
            
            if (hasHeader && !allRows.isEmpty()) {
                // Skip header row
                allRows.remove(0);
            }
            
            // If separator is not comma, we need to re-parse
            if (separatorChar != ',') {
                rows = reparseWithSeparator(allRows, separatorChar);
            } else {
                rows = allRows;
            }
            
        } catch (CsvException e) {
            throw new IOException("Error parsing CSV file: " + e.getMessage(), e);
        }
        
        return rows;
    }
    
    private static char parseSeparator(String separator) {
        if (separator == null || separator.trim().isEmpty()) {
            return ',';
        }
        
        String sep = separator.trim();
        if (sep.equals("\\t") || sep.equals("\t")) {
            return '\t';
        }
        if (sep.length() == 1) {
            return sep.charAt(0);
        }
        
        return ',';
    }
    
    private static List<String[]> reparseWithSeparator(List<String[]> rows, char separator) {
        List<String[]> result = new ArrayList<>();
        for (String[] row : rows) {
            if (row.length == 1) {
                // Re-split with correct separator
                String[] newRow = row[0].split(String.valueOf(separator), -1);
                result.add(newRow);
            } else {
                result.add(row);
            }
        }
        return result;
    }
}

