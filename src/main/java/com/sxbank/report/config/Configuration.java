package com.sxbank.report.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "configuration")
public class Configuration {
    
    private WorkbookConfig workbook;
    private List<SheetConfig> sheets;

    @XmlElement(name = "workbook")
    public WorkbookConfig getWorkbook() {
        return workbook;
    }

    public void setWorkbook(WorkbookConfig workbook) {
        this.workbook = workbook;
    }

    @XmlElementWrapper(name = "sheets")
    @XmlElement(name = "sheet")
    public List<SheetConfig> getSheets() {
        return sheets;
    }

    public void setSheets(List<SheetConfig> sheets) {
        this.sheets = sheets;
    }

    public static class WorkbookConfig {
        private String file;

        @XmlElement
        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }
    }

    public static class SheetConfig {
        private String name;
        private String title;
        private InputConfig input;
        private ColumnsConfig columns;
        private HeaderFormatConfig headerFormat;
        private TotalsHeaderConfig totalsHeader;

        @XmlElement
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @XmlElement
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @XmlElement
        public InputConfig getInput() {
            return input;
        }

        public void setInput(InputConfig input) {
            this.input = input;
        }

        @XmlElement
        public ColumnsConfig getColumns() {
            return columns;
        }

        public void setColumns(ColumnsConfig columns) {
            this.columns = columns;
        }

        @XmlElement(name = "headerFormat")
        public HeaderFormatConfig getHeaderFormat() {
            return headerFormat;
        }

        public void setHeaderFormat(HeaderFormatConfig headerFormat) {
            this.headerFormat = headerFormat;
        }

        @XmlElement(name = "totalsHeader")
        public TotalsHeaderConfig getTotalsHeader() {
            return totalsHeader;
        }

        public void setTotalsHeader(TotalsHeaderConfig totalsHeader) {
            this.totalsHeader = totalsHeader;
        }
    }

    public static class InputConfig {
        private String file;
        private String separator;
        private boolean hasHeader;

        @XmlElement
        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        @XmlElement
        public String getSeparator() {
            return separator;
        }

        public void setSeparator(String separator) {
            this.separator = separator;
        }

        @XmlElement
        public boolean isHasHeader() {
            return hasHeader;
        }

        public void setHasHeader(boolean hasHeader) {
            this.hasHeader = hasHeader;
        }
    }

    public static class ColumnsConfig {
        private List<ColumnConfig> column;

        @XmlElement(name = "column")
        public List<ColumnConfig> getColumn() {
            return column;
        }

        public void setColumn(List<ColumnConfig> column) {
            this.column = column;
        }
    }

    public static class TotalsHeaderConfig {
        private String label;
        private Integer startIndex;
        private Integer endIndex;
        private String backgroundColor;
        private String fontColor;

        @XmlElement
        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        @XmlElement
        public Integer getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(Integer startIndex) {
            this.startIndex = startIndex;
        }

        @XmlElement
        public Integer getEndIndex() {
            return endIndex;
        }

        public void setEndIndex(Integer endIndex) {
            this.endIndex = endIndex;
        }

        @XmlElement
        public String getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        @XmlElement
        public String getFontColor() {
            return fontColor;
        }

        public void setFontColor(String fontColor) {
            this.fontColor = fontColor;
        }
    }
}

