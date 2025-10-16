package kelompok.dua.maven.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class StateMachine {
    @JsonProperty("initial_state")
    private String initialState;

    private List<State> states;
    private List<Transition> transitions;

    // Constructors
    public StateMachine() {
    }

    public StateMachine(String initialState, List<State> states, List<Transition> transitions) {
        this.initialState = initialState;
        this.states = states;
        this.transitions = transitions;
    }

    // Getters and Setters
    public String getInitialState() {
        return initialState;
    }

    public void setInitialState(String initialState) {
        this.initialState = initialState;
    }

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }

    @Override
    public String toString() {
        return String.format("StateMachine{initialState='%s', states=%d, transitions=%d}",
                initialState,
                states != null ? states.size() : 0,
                transitions != null ? transitions.size() : 0);
    }
}