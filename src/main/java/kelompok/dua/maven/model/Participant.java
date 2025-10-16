package kelompok.dua.maven.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Participant {
    @JsonProperty("class_name")
    private String className;

    private String role;
    private String multiplicity;

    // Constructors
    public Participant() {
    }

    public Participant(String className, String role, String multiplicity) {
        this.className = className;
        this.role = role;
        this.multiplicity = multiplicity;
    }

    // Getters and Setters
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(String multiplicity) {
        this.multiplicity = multiplicity;
    }

    @Override
    public String toString() {
        return String.format("Participant{className='%s', role='%s', multiplicity='%s'}",
                className, role, multiplicity);
    }
}