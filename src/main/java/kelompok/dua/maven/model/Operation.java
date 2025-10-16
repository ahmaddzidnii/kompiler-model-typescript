package kelompok.dua.maven.model;

import java.util.List;

public class Operation {
    private String name;
    private List<Parameter> parameters;
    private List<ActionStep> actions;

    // Constructors
    public Operation() {
    }

    public Operation(String name, List<Parameter> parameters, List<ActionStep> actions) {
        this.name = name;
        this.parameters = parameters;
        this.actions = actions;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public List<ActionStep> getActions() {
        return actions;
    }

    public void setActions(List<ActionStep> actions) {
        this.actions = actions;
    }

    @Override
    public String toString() {
        return String.format("Operation{name='%s', parameters=%d, actions=%d}",
                name,
                parameters != null ? parameters.size() : 0,
                actions != null ? actions.size() : 0);
    }
}