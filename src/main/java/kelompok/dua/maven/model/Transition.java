package kelompok.dua.maven.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Transition {
    @JsonProperty("from_state")
    private String fromState;

    @JsonProperty("to_state")
    private String toState;

    private String event;
    private List<Action> actions;

    // Constructors
    public Transition() {
    }

    public Transition(String fromState, String toState, String event, List<Action> actions) {
        this.fromState = fromState;
        this.toState = toState;
        this.event = event;
        this.actions = actions;
    }

    // Getters and Setters
    public String getFromState() {
        return fromState;
    }

    public void setFromState(String fromState) {
        this.fromState = fromState;
    }

    public String getToState() {
        return toState;
    }

    public void setToState(String toState) {
        this.toState = toState;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    @Override
    public String toString() {
        return String.format("Transition{fromState='%s', toState='%s', event='%s', actions=%d}",
                fromState, toState, event, actions != null ? actions.size() : 0);
    }
}