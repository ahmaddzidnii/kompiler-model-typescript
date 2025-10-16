package kelompok.dua.maven.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ClassDefinition {
    @JsonProperty("entity_type")
    private String entityType;

    private String name;

    @JsonProperty("key_letter")
    private String keyLetter;

    @JsonProperty("inherits_from")
    private String inheritsFrom;

    @JsonProperty("is_abstract")
    private Boolean isAbstract;

    private String description;
    private List<Attribute> attributes;

    @JsonProperty("state_machine")
    private StateMachine stateMachine;

    // Constructors
    public ClassDefinition() {
    }

    public ClassDefinition(String entityType, String name, String keyLetter) {
        this.entityType = entityType;
        this.name = name;
        this.keyLetter = keyLetter;
    }

    // Getters and Setters
    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

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

    public String getInheritsFrom() {
        return inheritsFrom;
    }

    public void setInheritsFrom(String inheritsFrom) {
        this.inheritsFrom = inheritsFrom;
    }

    public Boolean getIsAbstract() {
        return isAbstract;
    }

    public void setIsAbstract(Boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public StateMachine getStateMachine() {
        return stateMachine;
    }

    public void setStateMachine(StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override
    public String toString() {
        return String.format(
                "ClassDefinition{entityType='%s', name='%s', keyLetter='%s', inheritsFrom='%s', isAbstract=%s}",
                entityType, name, keyLetter, inheritsFrom, isAbstract);
    }
}