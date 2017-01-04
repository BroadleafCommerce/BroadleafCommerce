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
package org.broadleafcommerce.openadmin.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Chad Harchar (charchar)
 */
public class ListGridFetchRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    protected List<String> fetchFields = new ArrayList<>();
    protected boolean useRefinedFetch = false;
    
    protected String joinType;

    public List<String> getFetchFields() {
        return fetchFields;
    }

    public void setFetchFields(List<String> fetchFields) {
        this.fetchFields = fetchFields;
    }

    public boolean isUseRefinedFetch() {
        return useRefinedFetch;
    }

    public void setUseRefinedFetch(boolean useRefinedFetch) {
        this.useRefinedFetch = useRefinedFetch;
    }

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }
}
