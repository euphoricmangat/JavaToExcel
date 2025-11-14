package com.sxbank.report.excel;

import com.sxbank.report.config.ColumnConfig;
import com.sxbank.report.config.Configuration;
import com.sxbank.report.util.DataTypeConverter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelGenerator {

    public static class SheetData {
        private final Configuration.SheetConfig sheetConfig;
        private final List<String[]> rows;

        public SheetData(Configuration.SheetConfig sheetConfig, List<String[]> rows) {
            this.sheetConfig = sheetConfig;
            this.rows = rows;
        }

        public Configuration.SheetConfig getSheetConfig() {
            return sheetConfig;
        }

        public List<String[]> getRows() {
            return rows;
        }
    }

    private final Configuration config;
    private final Workbook workbook;
    private final ExcelStyler styler;

    public ExcelGenerator(Configuration config) {
        this.config = config;
        this.workbook = new XSSFWorkbook();
        this.styler = new ExcelStyler(workbook);
    }

    public void generate(List<SheetData> sheetsData) throws IOException {
        int sheetCounter = 1;
        for (SheetData sheetData : sheetsData) {
            Configuration.SheetConfig sheetConfig = sheetData.getSheetConfig();
            String sheetName = sheetConfig.getName();
            if (sheetName == null || sheetName.trim().isEmpty()) {
                sheetName = "Sheet " + sheetCounter;
            }
            Sheet sheet = workbook.createSheet(sheetName);
            renderSheet(sheet, sheetConfig, sheetData.getRows());
            sheetCounter++;
        }

        try (FileOutputStream outputStream = new FileOutputStream(config.getWorkbook().getFile())) {
            workbook.write(outputStream);
        }
        workbook.close();
    }

    private void renderSheet(Sheet sheet, Configuration.SheetConfig sheetConfig, List<String[]> dataRows) {
        int rowCursor = 0;

        String title = sheetConfig.getTitle();
        if (title != null && !title.trim().isEmpty()) {
            rowCursor = createTitleRow(sheet, sheetConfig, title, rowCursor);
        }

        Configuration.TotalsHeaderConfig totalsHeader = sheetConfig.getTotalsHeader();
        if (totalsHeader != null) {
            rowCursor = createTotalsHeaderRow(sheet, sheetConfig, totalsHeader, rowCursor);
        }

        // Aggregate totals before the header so the summary row stays aligned
        Map<Integer, Double> totals = computeTotals(sheetConfig, dataRows);
        if (!totals.isEmpty()) {
            rowCursor = createTotalsValuesRow(sheet, sheetConfig, totals, rowCursor);
        }

        rowCursor = createHeaderRow(sheet, sheetConfig, rowCursor);

        for (int i = 0; i < dataRows.size(); i++) {
            Row excelRow = sheet.createRow(rowCursor++);
            writeDataRow(excelRow, sheetConfig, dataRows.get(i), i);
        }

        setColumnWidths(sheet, sheetConfig);
    }

    private int createTitleRow(Sheet sheet, Configuration.SheetConfig sheetConfig, String title, int rowIndex) {
        Row titleRow = sheet.createRow(rowIndex);
        titleRow.setHeightInPoints(24);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(styler.createTitleStyle());

        int lastCol = getLastColumnIndex(sheetConfig);
        if (lastCol > 0) {
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, lastCol));
        }
        return rowIndex + 1;
    }

    private int createTotalsValuesRow(Sheet sheet, Configuration.SheetConfig sheetConfig,
                                      Map<Integer, Double> totals, int rowIndex) {
        Row totalsRow = sheet.createRow(rowIndex);
        totalsRow.setHeightInPoints(18);
        for (ColumnConfig column : sheetConfig.getColumns().getColumn()) {
            int colIndex = column.getIndex();
            Cell cell = totalsRow.createCell(colIndex);
            if (totals.containsKey(colIndex)) {
                cell.setCellValue(totals.get(colIndex));
            }
            cell.setCellStyle(styler.createTotalsRowStyle(column));
        }
        return rowIndex + 1;
    }

    private int createTotalsHeaderRow(Sheet sheet, Configuration.SheetConfig sheetConfig,
                                      Configuration.TotalsHeaderConfig totalsHeader, int rowIndex) {
        Row totalsHeaderRow = sheet.createRow(rowIndex);
        totalsHeaderRow.setHeightInPoints(20);
        int lastColumn = getLastColumnIndex(sheetConfig);
        int start = totalsHeader.getStartIndex() != null ? totalsHeader.getStartIndex() : Math.max(0, lastColumn - 3);
        int end = totalsHeader.getEndIndex() != null ? totalsHeader.getEndIndex() : lastColumn;
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }

        start = Math.max(0, Math.min(start, lastColumn));
        end = Math.max(0, Math.min(end, lastColumn));

        Cell totalsLabelCell = totalsHeaderRow.createCell(start);
        String label = totalsHeader.getLabel() != null ? totalsHeader.getLabel() : "Totals";
        totalsLabelCell.setCellValue(label);
        totalsLabelCell.setCellStyle(styler.createTotalsHeaderLabelStyle(totalsHeader));
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, start, end));

        CellStyle fillerStyle = styler.createHeaderStyle(sheetConfig.getHeaderFormat());
        for (ColumnConfig column : sheetConfig.getColumns().getColumn()) {
            int idx = column.getIndex();
            if (idx < start || idx > end) {
                Cell filler = totalsHeaderRow.createCell(idx);
                filler.setCellStyle(fillerStyle);
            }
        }

        return rowIndex + 1;
    }

    private int createHeaderRow(Sheet sheet, Configuration.SheetConfig sheetConfig, int rowIndex) {
        Row headerRow = sheet.createRow(rowIndex);
        headerRow.setHeightInPoints(20);
        CellStyle headerStyle = styler.createHeaderStyle(sheetConfig.getHeaderFormat());

        for (ColumnConfig column : sheetConfig.getColumns().getColumn()) {
            Cell cell = headerRow.createCell(column.getIndex());
            cell.setCellValue(column.getName());
            if (headerStyle != null) {
                cell.setCellStyle(headerStyle);
            }
        }
        return rowIndex + 1;
    }

    private void writeDataRow(Row row, Configuration.SheetConfig sheetConfig, String[] data, int rowIndex) {
        row.setHeightInPoints(18);
        for (ColumnConfig column : sheetConfig.getColumns().getColumn()) {
            int colIndex = column.getIndex();
            if (colIndex >= data.length) {
                continue;
            }

            Cell cell = row.createCell(colIndex);
            String value = data[colIndex];

            Object convertedValue = DataTypeConverter.convertValue(
                value,
                column.getDataType(),
                column.getFormat() != null ? column.getFormat().getDateFormat() : null
            );
            setCellValue(cell, convertedValue, column.getDataType());

            CellStyle style = styler.createColumnStyle(column, rowIndex);
            if (style != null) {
                cell.setCellStyle(style);
            }
        }
    }

    private int getLastColumnIndex(Configuration.SheetConfig sheetConfig) {
        int maxIndex = 0;
        for (ColumnConfig column : sheetConfig.getColumns().getColumn()) {
            if (column.getIndex() > maxIndex) {
                maxIndex = column.getIndex();
            }
        }
        return maxIndex;
    }

    private void setColumnWidths(Sheet sheet, Configuration.SheetConfig sheetConfig) {
        for (ColumnConfig column : sheetConfig.getColumns().getColumn()) {
            if (column.getFormat() != null && column.getFormat().getWidth() != null) {
                int width = column.getFormat().getWidth() * 256;
                sheet.setColumnWidth(column.getIndex(), width);
            } else {
                sheet.autoSizeColumn(column.getIndex());
            }
        }
    }

    private Map<Integer, Double> computeTotals(Configuration.SheetConfig sheetConfig, List<String[]> dataRows) {
        Map<Integer, Double> totals = new HashMap<>();
        for (ColumnConfig column : sheetConfig.getColumns().getColumn()) {
            if (shouldTotal(column)) {
                totals.put(column.getIndex(), 0.0);
            }
        }
        if (totals.isEmpty()) {
            return totals;
        }

        for (String[] row : dataRows) {
            for (ColumnConfig column : sheetConfig.getColumns().getColumn()) {
                if (!shouldTotal(column)) {
                    continue;
                }
                int idx = column.getIndex();
                if (idx >= row.length) {
                    continue;
                }
                Object convertedValue = DataTypeConverter.convertValue(
                    row[idx],
                    column.getDataType(),
                    column.getFormat() != null ? column.getFormat().getDateFormat() : null
                );
                Double numeric = toDouble(convertedValue);
                if (numeric != null) {
                    totals.put(idx, totals.get(idx) + numeric);
                }
            }
        }
        return totals;
    }

    private boolean shouldTotal(ColumnConfig column) {
        return column.getFormat() != null &&
            Boolean.TRUE.equals(column.getFormat().getIncludeTotals()) &&
            (column.getDataType().equalsIgnoreCase("NUMBER") ||
             column.getDataType().equalsIgnoreCase("CURRENCY"));
    }

    private Double toDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            String sanitized = value.toString().replaceAll("[^0-9\\-\\.]", "");
            if (sanitized.isEmpty()) {
                return null;
            }
            return Double.parseDouble(sanitized);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void setCellValue(Cell cell, Object value, String dataType) {
        if (value == null) {
            cell.setBlank();
            return;
        }

        switch (dataType.toUpperCase()) {
            case "NUMBER":
                if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                } else {
                    cell.setCellValue(value.toString());
                }
                break;
            case "DATE":
                if (value instanceof Date) {
                    cell.setCellValue((Date) value);
                } else {
                    cell.setCellValue(value.toString());
                }
                break;
            case "CURRENCY":
                if (value instanceof java.math.BigDecimal) {
                    cell.setCellValue(((java.math.BigDecimal) value).doubleValue());
                } else if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                } else {
                    cell.setCellValue(value.toString());
                }
                break;
            case "TEXT":
            default:
                cell.setCellValue(value.toString());
                break;
        }
    }
}

