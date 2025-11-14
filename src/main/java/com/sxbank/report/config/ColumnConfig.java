package com.sxbank.report.config;

import javax.xml.bind.annotation.XmlElement;

public class ColumnConfig {
    
    private int index;
    private String name;
    private String dataType;
    private FormatConfig format;

    @XmlElement
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @XmlElement
    public FormatConfig getFormat() {
        return format;
    }

    public void setFormat(FormatConfig format) {
        this.format = format;
    }
}

