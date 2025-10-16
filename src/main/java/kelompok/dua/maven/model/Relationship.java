package kelompok.dua.maven.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Relationship {
    @JsonProperty("relationship_id")
    private String relationshipId;

    private String description;
    private List<Participant> participants;

    @JsonProperty("association_class")
    private AssociationClass associationClass;

    // Constructors
    public Relationship() {
    }

    public Relationship(String relationshipId, String description, List<Participant> participants) {
        this.relationshipId = relationshipId;
        this.description = description;
        this.participants = participants;
    }

    // Getters and Setters
    public String getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public AssociationClass getAssociationClass() {
        return associationClass;
    }

    public void setAssociationClass(AssociationClass associationClass) {
        this.associationClass = associationClass;
    }

    @Override
    public String toString() {
        return String.format("Relationship{relationshipId='%s', description='%s', participants=%d}",
                relationshipId, description, participants != null ? participants.size() : 0);
    }
}