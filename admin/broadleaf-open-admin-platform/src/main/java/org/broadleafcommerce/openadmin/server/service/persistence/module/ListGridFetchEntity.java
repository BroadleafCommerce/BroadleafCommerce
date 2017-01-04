/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.service.persistence.module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Chad Harchar (charchar)
 */
public class ListGridFetchEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected String entityTarget;

    protected String regexTarget;

    protected List<String> additionalfetchFields = new ArrayList<>();

    public String getEntityTarget() {
        return entityTarget;
    }

    public void setEntityTarget(String entityTarget) {
        this.entityTarget = entityTarget;
    }

    public String getRegexTarget() {
        return regexTarget;
    }

    public void setRegexTarget(String regexTarget) {
        this.regexTarget = regexTarget;
    }

    public List<String> getAdditionalfetchFields() {
        return additionalfetchFields;
    }

    public void setAdditionalfetchFields(List<String> additionalfetchFields) {
        this.additionalfetchFields = additionalfetchFields;
    }
}
