package com.sxbank.report.excel;

import com.sxbank.report.config.ColumnConfig;
import com.sxbank.report.config.Configuration;
import com.sxbank.report.config.FormatConfig;
import com.sxbank.report.config.HeaderFormatConfig;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class ExcelStyler {
    
    private static final String DEFAULT_FONT = "Calibri";
    
    private final Workbook workbook;
    private final Map<String, CellStyle> styleCache = new HashMap<>();
    
    public ExcelStyler(Workbook workbook) {
        this.workbook = workbook;
    }
    
    public Workbook getWorkbook() {
        return workbook;
    }
    
    public CellStyle createHeaderStyle(HeaderFormatConfig headerFormat) {
        if (headerFormat == null) {
            return createDefaultHeaderStyle();
        }
        
        String cacheKey = "header_" + 
            (headerFormat.getBold() != null ? headerFormat.getBold() : "") +
            (headerFormat.getBackgroundColor() != null ? headerFormat.getBackgroundColor() : "") +
            (headerFormat.getFontColor() != null ? headerFormat.getFontColor() : "") +
            (headerFormat.getFontSize() != null ? headerFormat.getFontSize() : "");
        
        if (styleCache.containsKey(cacheKey)) {
            return styleCache.get(cacheKey);
        }
        
        CellStyle style = workbook.createCellStyle();
        short fontSize = headerFormat.getFontSize() != null ? headerFormat.getFontSize().shortValue() : 12;
        boolean bold = headerFormat.getBold() == null || headerFormat.getBold();
        Font font = buildFont(bold, fontSize);
        
        if (headerFormat.getFontColor() != null) {
            setFontColor(font, headerFormat.getFontColor());
        }
        
        if (headerFormat.getBackgroundColor() != null) {
            setBackgroundColor(style, headerFormat.getBackgroundColor());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(false);
        applyThinBorders(style);
        
        styleCache.put(cacheKey, style);
        return style;
    }
    
    public CellStyle createTitleStyle() {
        String cacheKey = "title_style";
        if (styleCache.containsKey(cacheKey)) {
            return styleCache.get(cacheKey);
        }
        
        CellStyle style = workbook.createCellStyle();
        Font font = buildFont(true, (short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(false);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        
        styleCache.put(cacheKey, style);
        return style;
    }

    public CellStyle createTotalsHeaderLabelStyle(Configuration.TotalsHeaderConfig totalsHeader) {
        String background = totalsHeader != null ? totalsHeader.getBackgroundColor() : null;
        String fontColor = totalsHeader != null ? totalsHeader.getFontColor() : null;
        String cacheKey = "totals_header_label_" + (background != null ? background : "") + "_" + (fontColor != null ? fontColor : "");
        
        if (styleCache.containsKey(cacheKey)) {
            return styleCache.get(cacheKey);
        }
        
        CellStyle style = workbook.createCellStyle();
        Font font = buildFont(true, (short) 11);
        if (fontColor != null) {
            setFontColor(font, fontColor);
        } else {
            font.setColor(IndexedColors.WHITE.getIndex());
        }
        
        if (background != null) {
            setBackgroundColor(style, background);
        } else {
            style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        }
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        applyThinBorders(style);
        
        styleCache.put(cacheKey, style);
        return style;
    }
    
    public CellStyle createTotalsRowStyle(ColumnConfig columnConfig) {
        FormatConfig format = columnConfig != null ? columnConfig.getFormat() : null;
        String cacheKey = "totals_row_style_" + (columnConfig != null ? columnConfig.getIndex() : "na") + "_" +
            (format != null && format.getNumberFormat() != null ? format.getNumberFormat() : "") +
            (format != null && format.getDateFormat() != null ? format.getDateFormat() : "") +
            (format != null && format.getAlignment() != null ? format.getAlignment() : "");
        
        if (styleCache.containsKey(cacheKey)) {
            return styleCache.get(cacheKey);
        }
        
        CellStyle style = workbook.createCellStyle();
        Font font = buildFont(true, (short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(false);
        
        if (format != null) {
            if (format.getAlignment() != null) {
                style.setAlignment(parseAlignment(format.getAlignment()));
            }
            if (format.getNumberFormat() != null) {
                DataFormat dataFormat = workbook.createDataFormat();
                style.setDataFormat(dataFormat.getFormat(format.getNumberFormat()));
            }
            if (format.getDateFormat() != null) {
                DataFormat dataFormat = workbook.createDataFormat();
                style.setDataFormat(dataFormat.getFormat(convertDateFormat(format.getDateFormat())));
            }
        } else {
            style.setAlignment(HorizontalAlignment.RIGHT);
        }
        
        applyThinBorders(style);
        
        styleCache.put(cacheKey, style);
        return style;
    }
    
    public CellStyle createColumnStyle(ColumnConfig columnConfig, int rowIndex) {
        if (columnConfig == null || columnConfig.getFormat() == null) {
            // Still apply alternating row colors even without format
            return createAlternatingRowStyle(rowIndex);
        }
        
        FormatConfig format = columnConfig.getFormat();
        boolean isEvenRow = (rowIndex % 2 == 0);
        String cacheKey = "col_" + columnConfig.getIndex() + "_" + isEvenRow + "_" +
            (format.getBold() != null ? format.getBold() : "") +
            (format.getBackgroundColor() != null ? format.getBackgroundColor() : "") +
            (format.getFontColor() != null ? format.getFontColor() : "") +
            (format.getFontSize() != null ? format.getFontSize() : "") +
            (format.getAlignment() != null ? format.getAlignment() : "") +
            (format.getNumberFormat() != null ? format.getNumberFormat() : "") +
            (format.getDateFormat() != null ? format.getDateFormat() : "");
        
        if (styleCache.containsKey(cacheKey)) {
            return styleCache.get(cacheKey);
        }
        
        CellStyle style = workbook.createCellStyle();
        boolean bold = format.getBold() != null && format.getBold();
        short fontSize = format.getFontSize() != null ? format.getFontSize().shortValue() : 11;
        Font font = buildFont(bold, fontSize);
        
        // Font color
        if (format.getFontColor() != null) {
            setFontColor(font, format.getFontColor());
        }
        
        // Background color
        if (format.getBackgroundColor() != null) {
            setBackgroundColor(style, format.getBackgroundColor());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        
        // Alignment
        if (format.getAlignment() != null) {
            HorizontalAlignment alignment = parseAlignment(format.getAlignment());
            style.setAlignment(alignment);
        } else {
            style.setAlignment(HorizontalAlignment.LEFT);
        }
        
        // Number format
        if (format.getNumberFormat() != null) {
            DataFormat dataFormat = workbook.createDataFormat();
            style.setDataFormat(dataFormat.getFormat(format.getNumberFormat()));
        }
        
        // Date format
        if (format.getDateFormat() != null) {
            DataFormat dataFormat = workbook.createDataFormat();
            // Convert Java date format to Excel format
            String excelDateFormat = convertDateFormat(format.getDateFormat());
            style.setDataFormat(dataFormat.getFormat(excelDateFormat));
        }
        
        style.setFont(font);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(false);
        applyThinBorders(style);
        
        // Apply alternating row background color if no specific background color is set
        if (format.getBackgroundColor() == null) {
            if (!isEvenRow) {
                // Light grey for odd rows
                style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
        }
        
        styleCache.put(cacheKey, style);
        return style;
    }
    
    private CellStyle createAlternatingRowStyle(int rowIndex) {
        boolean isEvenRow = (rowIndex % 2 == 0);
        String cacheKey = "alt_row_" + isEvenRow;
        
        if (styleCache.containsKey(cacheKey)) {
            return styleCache.get(cacheKey);
        }
        
        CellStyle style = workbook.createCellStyle();
        Font font = buildFont(false, (short) 11);
        style.setFont(font);
        if (!isEvenRow) {
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setWrapText(false);
        applyThinBorders(style);
        
        styleCache.put(cacheKey, style);
        return style;
    }
    
    private CellStyle createDefaultHeaderStyle() {
        CellStyle style = workbook.createCellStyle();
        Font font = buildFont(true, (short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        applyThinBorders(style);
        return style;
    }
    
    private Font buildFont(boolean bold, short size) {
        Font font = workbook.createFont();
        font.setFontName(DEFAULT_FONT);
        font.setBold(bold);
        font.setFontHeightInPoints(size);
        return font;
    }

    private void applyThinBorders(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
    }
    
    private void setFontColor(Font font, String hexColor) {
        try {
            Color color = Color.decode(hexColor);
            if (workbook instanceof XSSFWorkbook) {
                XSSFColor xssfColor = new XSSFColor(color, null);
                ((org.apache.poi.xssf.usermodel.XSSFFont) font).setColor(xssfColor);
            } else {
                // For HSSF, use indexed colors (limited support)
                font.setColor(getIndexedColor(color));
            }
        } catch (Exception e) {
            // Invalid color, use default
        }
    }
    
    private void setBackgroundColor(CellStyle style, String hexColor) {
        try {
            Color color = Color.decode(hexColor);
            if (workbook instanceof XSSFWorkbook) {
                XSSFColor xssfColor = new XSSFColor(color, null);
                style.setFillForegroundColor(xssfColor);
            } else {
                style.setFillForegroundColor(getIndexedColor(color));
            }
        } catch (Exception e) {
            // Invalid color, use default
        }
    }
    
    private short getIndexedColor(Color color) {
        // Map to closest indexed color (simplified)
        return IndexedColors.GREY_25_PERCENT.getIndex();
    }
    
    private HorizontalAlignment parseAlignment(String alignment) {
        if (alignment == null) {
            return HorizontalAlignment.LEFT;
        }
        
        switch (alignment.toUpperCase()) {
            case "CENTER":
                return HorizontalAlignment.CENTER;
            case "RIGHT":
                return HorizontalAlignment.RIGHT;
            case "LEFT":
            default:
                return HorizontalAlignment.LEFT;
        }
    }
    
    private String convertDateFormat(String javaDateFormat) {
        // Convert Java SimpleDateFormat to Excel date format
        // This is a simplified conversion - may need enhancement for complex formats
        return javaDateFormat
            .replace("yyyy", "yyyy")
            .replace("MM", "mm")
            .replace("dd", "dd")
            .replace("HH", "hh")
            .replace("mm", "mm")
            .replace("ss", "ss");
    }
}

