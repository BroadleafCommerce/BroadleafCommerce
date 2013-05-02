package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jeff Fischer
 */
public class FieldPath {

    protected List<String> associationPath = new ArrayList<String>();
    protected List<String> targetPropertyPieces = new ArrayList<String>();
    protected String targetProperty;

    public FieldPath withAssociationPath(List<String> associationPath) {
        setAssociationPath(associationPath);
        return this;
    }

    public FieldPath withTargetPropertyPieces(List<String> targetPropertyPieces) {
        setTargetPropertyPieces(targetPropertyPieces);
        return this;
    }

    public FieldPath withTargetProperty(String targetProperty) {
        setTargetProperty(targetProperty);
        return this;
    }

    public List<String> getAssociationPath() {
        return associationPath;
    }

    public void setAssociationPath(List<String> associationPath) {
        this.associationPath = associationPath;
    }

    public List<String> getTargetPropertyPieces() {
        return targetPropertyPieces;
    }

    public void setTargetPropertyPieces(List<String> targetPropertyPieces) {
        this.targetPropertyPieces = targetPropertyPieces;
    }

    public String getTargetProperty() {
        return targetProperty;
    }

    public void setTargetProperty(String targetProperty) {
        this.targetProperty = targetProperty;
    }
}
