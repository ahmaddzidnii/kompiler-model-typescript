package kelompok.dua.maven.model;

public class Parameter {
    private String name;
    private String type;

    // Constructors
    public Parameter() {
    }

    public Parameter(String name, String type) {
        this.name = name;
        this.type = type;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("Parameter{name='%s', type='%s'}", name, type);
    }
}