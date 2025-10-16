package kelompok.dua.maven.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Action {
    private String type;
    private String message;

    @JsonProperty("actionLanguage")
    private ActionLanguage actionLanguage;

    // Constructors
    public Action() {
    }

    public Action(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public Action(ActionLanguage actionLanguage) {
        this.actionLanguage = actionLanguage;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ActionLanguage getActionLanguage() {
        return actionLanguage;
    }

    public void setActionLanguage(ActionLanguage actionLanguage) {
        this.actionLanguage = actionLanguage;
    }

    @Override
    public String toString() {
        if (actionLanguage != null) {
            return String.format("Action{actionLanguage=%s}", actionLanguage);
        } else {
            return String.format("Action{type='%s', message='%s'}", type, message);
        }
    }
}