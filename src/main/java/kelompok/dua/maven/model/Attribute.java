package kelompok.dua.maven.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Attribute {
    private String name;

    @JsonProperty("data_type")
    private String dataType;

    @JsonProperty("attribute_type")
    private String attributeType;

    @JsonProperty("default_value")
    private String defaultValue;

    // Constructors
    public Attribute() {
    }

    public Attribute(String name, String dataType, String attributeType) {
        this.name = name;
        this.dataType = dataType;
        this.attributeType = attributeType;
    }

    public Attribute(String name, String dataType, String attributeType, String defaultValue) {
        this.name = name;
        this.dataType = dataType;
        this.attributeType = attributeType;
        this.defaultValue = defaultValue;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return String.format("Attribute{name='%s', dataType='%s', attributeType='%s', defaultValue='%s'}",
                name, dataType, attributeType, defaultValue);
    }
}