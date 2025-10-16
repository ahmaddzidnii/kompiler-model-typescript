package kelompok.dua.maven.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class AssociationClass {
    private String name;

    @JsonProperty("key_letter")
    private String keyLetter;

    private List<Attribute> attributes;

    // Constructors
    public AssociationClass() {
    }

    public AssociationClass(String name, String keyLetter, List<Attribute> attributes) {
        this.name = name;
        this.keyLetter = keyLetter;
        this.attributes = attributes;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyLetter() {
        return keyLetter;
    }

    public void setKeyLetter(String keyLetter) {
        this.keyLetter = keyLetter;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return String.format("AssociationClass{name='%s', keyLetter='%s', attributes=%d}",
                name, keyLetter, attributes != null ? attributes.size() : 0);
    }
}