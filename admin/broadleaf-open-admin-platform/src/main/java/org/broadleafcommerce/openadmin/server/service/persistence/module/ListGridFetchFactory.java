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

import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.broadleafcommerce.openadmin.dto.ListGridFetchRequest;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Chad Harchar (charchar)
 */
public class ListGridFetchFactory {
    
    protected Map<String, ListGridFetchEntity> entities = new HashMap<>();
    protected List<String> universalFetchFields = new ArrayList<>();


    public ListGridFetchRequest getListGridFetchRequest(PersistencePackageRequest request) {
        if (request == null || !request.isListGridFetchRequest()) {
            return null;
        }

        ListGridFetchRequest listGridFetchRequest = new ListGridFetchRequest();

        String ceilingEntity = request.getCeilingEntityClassname();

        if (ceilingEntity.endsWith("Impl")) {
            int pos = ceilingEntity.lastIndexOf("Impl");
            ceilingEntity = ceilingEntity.substring(0, pos);
        }
        
        ListGridFetchEntity listGridFetchEntity = getEntities().get(ceilingEntity);
        
        if (listGridFetchEntity != null) {
            
            List<String> fetchFields = new ArrayList<>();
            
            fetchFields.addAll(getUniversalFetchFields());
            fetchFields.addAll(listGridFetchEntity.getAdditionalfetchFields());
            
            listGridFetchRequest.setFetchFields(fetchFields);
            listGridFetchRequest.setUseRefinedFetch(true);
        } else {
            listGridFetchRequest.setUseRefinedFetch(false);
        }
        
        return listGridFetchRequest;
    }


    public List<String> getUniversalFetchFields() {
        return universalFetchFields;
    }

    public void setUniversalFetchFields(List<String> universalFetchFields) {
        this.universalFetchFields = universalFetchFields;
    }

    public Map<String, ListGridFetchEntity> getEntities() {
        return entities;
    }

    public void setEntities(Map<String, ListGridFetchEntity> entities) {
        this.entities = entities;
    }
}
