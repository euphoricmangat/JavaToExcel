package com.sxbank.report.config;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;

public class ConfigReader {
    
    public static Configuration readConfig(String configPath) throws Exception {
        File configFile = new File(configPath);
        
        if (!configFile.exists()) {
            throw new FileNotFoundException("Configuration file not found: " + configPath);
        }
        
        if (!configFile.isFile()) {
            throw new IllegalArgumentException("Path is not a file: " + configPath);
        }
        
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Configuration config = (Configuration) unmarshaller.unmarshal(configFile);
            
            validateConfiguration(config);
            
            return config;
        } catch (JAXBException e) {
            throw new Exception("Failed to parse XML configuration: " + e.getMessage(), e);
        }
    }
    
    private static void validateConfiguration(Configuration config) throws Exception {
        if (config == null) {
            throw new IllegalArgumentException("Configuration is null");
        }
        
        if (config.getWorkbook() == null || config.getWorkbook().getFile() == null ||
            config.getWorkbook().getFile().trim().isEmpty()) {
            throw new IllegalArgumentException("Workbook output file path is missing");
        }

        if (config.getSheets() == null || config.getSheets().isEmpty()) {
            throw new IllegalArgumentException("At least one <sheet> definition is required");
        }

        for (Configuration.SheetConfig sheet : config.getSheets()) {
            if (sheet.getInput() == null) {
                throw new IllegalArgumentException("Input configuration missing for sheet");
            }

            if (sheet.getInput().getFile() == null || sheet.getInput().getFile().trim().isEmpty()) {
                throw new IllegalArgumentException("Input file path is missing for sheet");
            }

            if (sheet.getInput().getSeparator() == null || sheet.getInput().getSeparator().trim().isEmpty()) {
                throw new IllegalArgumentException("Separator is missing for sheet input: " + sheet.getInput().getFile());
            }

            validateSeparator(sheet.getInput().getSeparator());

            if (sheet.getColumns() == null || sheet.getColumns().getColumn() == null ||
                sheet.getColumns().getColumn().isEmpty()) {
                throw new IllegalArgumentException("Column configuration missing for sheet input: " + sheet.getInput().getFile());
            }

            for (ColumnConfig column : sheet.getColumns().getColumn()) {
                if (column.getIndex() < 0) {
                    throw new IllegalArgumentException("Column index must be non-negative: " + column.getIndex());
                }
            }
        }
    }

    private static void validateSeparator(String separator) {
        if (separator.equals(",") || separator.equals(";") || separator.equals("|") ||
            separator.equals("\t") || separator.equals("\\t")) {
            return;
        }
        throw new IllegalArgumentException("Invalid separator. Supported: , ; | \\t");
    }
}

