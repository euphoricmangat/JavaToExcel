package com.sxbank.report;

import com.sxbank.report.config.ConfigReader;
import com.sxbank.report.config.Configuration;
import com.sxbank.report.excel.ExcelGenerator;
import com.sxbank.report.parser.FlatFileParser;

import java.util.ArrayList;
import java.util.List;

public class Main {
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -jar excel-generator.jar <config.xml>");
            System.exit(1);
        }
        
        String configPath = args[0];
        
        try {
            // Parse the workbook definition so we know which CSV feeds to stitch together
            System.out.println("Reading configuration from: " + configPath);
            Configuration config = ConfigReader.readConfig(configPath);
            
            List<ExcelGenerator.SheetData> sheetDataList = new ArrayList<>();
            int sheetCounter = 1;
            for (Configuration.SheetConfig sheetConfig : config.getSheets()) {
                Configuration.InputConfig input = sheetConfig.getInput();
                String sheetName = sheetConfig.getName() != null ? sheetConfig.getName() : "Sheet " + sheetCounter;
                System.out.println("Parsing sheet '" + sheetName + "' from file: " + input.getFile());
                List<String[]> rows = FlatFileParser.parseFile(
                    input.getFile(),
                    input.getSeparator(),
                    input.isHasHeader()
                );
                System.out.println("  -> rows: " + rows.size());
                sheetDataList.add(new ExcelGenerator.SheetData(sheetConfig, rows));
                sheetCounter++;
            }
            
            // Build the workbook with the configured styling rules
            System.out.println("Generating workbook: " + config.getWorkbook().getFile());
            ExcelGenerator generator = new ExcelGenerator(config);
            generator.generate(sheetDataList);
            
            // 4. Save output file (already done in generate method)
            System.out.println("Excel file generated successfully!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

