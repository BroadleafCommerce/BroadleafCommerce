/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
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
