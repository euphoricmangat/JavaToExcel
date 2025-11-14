package com.sxbank.report.config;

import javax.xml.bind.annotation.XmlElement;

public class FormatConfig {
    
    private Boolean bold;
    private String backgroundColor;
    private String fontColor;
    private Integer width;
    private String alignment;
    private String numberFormat;
    private String dateFormat;
    private Integer fontSize;
    private Boolean includeTotals;

    @XmlElement
    public Boolean getBold() {
        return bold;
    }

    public void setBold(Boolean bold) {
        this.bold = bold;
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

    @XmlElement
    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    @XmlElement
    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    @XmlElement
    public String getNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat(String numberFormat) {
        this.numberFormat = numberFormat;
    }

    @XmlElement
    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @XmlElement
    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    @XmlElement
    public Boolean getIncludeTotals() {
        return includeTotals;
    }

    public void setIncludeTotals(Boolean includeTotals) {
        this.includeTotals = includeTotals;
    }
}

