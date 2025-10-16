package kelompok.dua.maven.model;

public class ActionStep {
    private String type;
    private String target;
    private String attribute;
    private String value;
    private String message;

    // Constructors
    public ActionStep() {
    }

    public ActionStep(String type, String target, String attribute, String value, String message) {
        this.type = type;
        this.target = target;
        this.attribute = attribute;
        this.value = value;
        this.message = message;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("ActionStep{type='%s', target='%s', attribute='%s', value='%s', message='%s'}",
                type, target, attribute, value, message);
    }
}