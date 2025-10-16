package kelompok.dua.maven.model;

public class State {
    private String name;

    // Constructors
    public State() {
    }

    public State(String name) {
        this.name = name;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("State{name='%s'}", name);
    }
}