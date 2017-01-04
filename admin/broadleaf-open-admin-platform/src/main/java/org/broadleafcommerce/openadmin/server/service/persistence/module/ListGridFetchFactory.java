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

import org.broadleafcommerce.openadmin.dto.ListGridFetchRequest;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * @author Chad Harchar (charchar)
 */
public class ListGridFetchFactory {
    
    protected List<ListGridFetchEntity> entities = new ArrayList<>();
    protected List<String> universalFetchFields = new ArrayList<>();

    protected static String CACHE_NAME = "blStandardElements";
    protected static String CACHE_KEY_PREFIX = "refineFetch:";
    protected Cache cache = CacheManager.getInstance().getCache(CACHE_NAME);

    public ListGridFetchRequest getListGridFetchRequest(PersistencePackageRequest request) {
        if (request == null || !request.isListGridFetchRequest()) {
            return null;
        }

        ListGridFetchRequest listGridFetchRequest;

        String ceilingEntity = request.getCeilingEntityClassname();

        if (ceilingEntity.endsWith("Impl")) {
            int pos = ceilingEntity.lastIndexOf("Impl");
            ceilingEntity = ceilingEntity.substring(0, pos);
        }
        
        String cacheKey = CACHE_KEY_PREFIX + ceilingEntity;
        Element cacheElement = cache.get(cacheKey);
        if (cacheElement != null) {
            listGridFetchRequest = (ListGridFetchRequest) cacheElement.getObjectValue();
        } else {
            listGridFetchRequest = createListGridFetchRequest(ceilingEntity);

            cacheElement = new Element(cacheKey, listGridFetchRequest);
            cache.put(cacheElement);
        }

        return listGridFetchRequest;
    }

    public ListGridFetchRequest createListGridFetchRequest(String ceilingEntity) {
        ListGridFetchRequest listGridFetchRequest = new ListGridFetchRequest();
        ListGridFetchEntity matchedEntity = null;

        for (ListGridFetchEntity listGridFetchEntity : getEntities()) {
            if (ceilingEntity.equals(listGridFetchEntity.getEntityTarget())) {
                matchedEntity = listGridFetchEntity;
                break;
            }

            matchedEntity = attemptToFindRegexTarget(ceilingEntity, listGridFetchEntity);

            if (matchedEntity != null) {
                break;
            }
        }

        setAdditionalFieldsToListGridFetchRequest(listGridFetchRequest, matchedEntity);

        return listGridFetchRequest;
    }

    public void setAdditionalFieldsToListGridFetchRequest(ListGridFetchRequest listGridFetchRequest, ListGridFetchEntity matchedEntity) {
        if (matchedEntity != null) {
            List<String> fetchFields = new ArrayList<>();

            fetchFields.addAll(getUniversalFetchFields());
            fetchFields.addAll(matchedEntity.getAdditionalfetchFields());

            listGridFetchRequest.setFetchFields(fetchFields);
            listGridFetchRequest.setUseRefinedFetch(true);
        } else {
            listGridFetchRequest.setUseRefinedFetch(false);
        }
    }

    public ListGridFetchEntity attemptToFindRegexTarget(String ceilingEntity, ListGridFetchEntity listGridFetchEntity) {
        if (listGridFetchEntity.getRegexTarget() != null) {
            Pattern pattern = Pattern.compile(listGridFetchEntity.getRegexTarget());
            Matcher matcher = pattern.matcher(ceilingEntity);

            if (matcher.matches()) {
                return listGridFetchEntity;
            }
        }
        return null;
    }


    public List<String> getUniversalFetchFields() {
        return universalFetchFields;
    }

    public void setUniversalFetchFields(List<String> universalFetchFields) {
        this.universalFetchFields = universalFetchFields;
    }

    public List<ListGridFetchEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<ListGridFetchEntity> entities) {
        this.entities = entities;
    }
}
