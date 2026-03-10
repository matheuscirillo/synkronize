package io.synkronize.commons.model;

public class DeploymentProperty {

    private DeploymentPropertyType type;
    private String value;
    private String secretRef;

    public DeploymentProperty() {
    }

    public DeploymentPropertyType getType() {
        return type;
    }

    public void setType(DeploymentPropertyType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSecretRef() {
        return secretRef;
    }

    public void setSecretRef(String secretRef) {
        this.secretRef = secretRef;
    }
}
