package kelompok.dua.maven.model;

public class Action {
    private String type;
    private String message;

    // Constructors
    public Action() {
    }

    public Action(String type, String message) {
        this.type = type;
        this.message = message;
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

    @Override
    public String toString() {
        return String.format("Action{type='%s', message='%s'}", type, message);
    }
}