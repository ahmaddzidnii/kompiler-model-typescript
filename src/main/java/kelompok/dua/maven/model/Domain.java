package kelompok.dua.maven.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Domain {
    private String name;

    @JsonProperty("key_letter")
    private String keyLetter;

    private List<ClassDefinition> classes;
    private List<Relationship> relationships;

    // Constructors
    public Domain() {
    }

    public Domain(String name, String keyLetter, List<ClassDefinition> classes, List<Relationship> relationships) {
        this.name = name;
        this.keyLetter = keyLetter;
        this.classes = classes;
        this.relationships = relationships;
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

    public List<ClassDefinition> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassDefinition> classes) {
        this.classes = classes;
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<Relationship> relationships) {
        this.relationships = relationships;
    }

    @Override
    public String toString() {
        return String.format("Domain{name='%s', keyLetter='%s', classes=%d, relationships=%d}",
                name, keyLetter,
                classes != null ? classes.size() : 0,
                relationships != null ? relationships.size() : 0);
    }
}