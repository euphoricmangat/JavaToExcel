package com.sxbank.report.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlatFileParser {
    
    public static List<String[]> parseFile(String filePath, String separator, boolean hasHeader) throws IOException {
        List<String[]> rows = new ArrayList<>();
        
        char separatorChar = parseSeparator(separator);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (hasHeader && firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                
                String[] fields = line.split(String.valueOf(separatorChar), -1);
                rows.add(fields);
                firstLine = false;
            }
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
}

