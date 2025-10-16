package kelompok.dua.maven.model;

import java.util.List;

public class ActionLanguage {
    private List<Operation> operations;

    // Constructors
    public ActionLanguage() {
    }

    public ActionLanguage(List<Operation> operations) {
        this.operations = operations;
    }

    // Getters and Setters
    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    @Override
    public String toString() {
        return String.format("ActionLanguage{operations=%d}",
                operations != null ? operations.size() : 0);
    }
}