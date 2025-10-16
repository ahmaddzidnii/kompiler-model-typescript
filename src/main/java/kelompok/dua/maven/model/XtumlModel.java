package kelompok.dua.maven.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class XtumlModel {
    @JsonProperty("system_name")
    private String systemName;

    private String version;
    private List<Domain> domains;

    // Constructors
    public XtumlModel() {
    }

    public XtumlModel(String systemName, String version, List<Domain> domains) {
        this.systemName = systemName;
        this.version = version;
        this.domains = domains;
    }

    // Getters and Setters
    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Domain> getDomains() {
        return domains;
    }

    public void setDomains(List<Domain> domains) {
        this.domains = domains;
    }

    @Override
    public String toString() {
        return String.format("XtumlModel{systemName='%s', version='%s', domains=%d}",
                systemName, version, domains != null ? domains.size() : 0);
    }
}