/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.sandbox;

import org.hibernate.Session;
import org.springframework.stereotype.Component;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

/**
 * @see org.broadleafcommerce.common.sandbox.SandBoxHelper
 * @author Jeff Fischer
 */
@Component("blSandBoxHelper")
public class DefaultSandBoxHelper implements SandBoxHelper {

    @Override
    public Long getSandBoxVersionId(Class<?> linkedObjectType, Long requestedParent) {
        return requestedParent;
    }

    @Override
    public Long getCascadedProductionStateId(Class<?> linkedObjectType, Long requestedParent) {
        return requestedParent;
    }

    @Override
    public Long retrieveCascadedState(Class<?> ceilingImpl, Long requestedParent, EntityManager em) {
        return requestedParent;
    }

    @Override
    public boolean isRelatedToParentCatalogIds(Object entity, Long... candidateRelatedIds) {
        return false;
    }

    @Override
    public List<Long> mergeCloneIds(Class<?> type, Long... originalIds) {
        return Arrays.asList(originalIds);
    }

    @Override
    public BiMap<Long, Long> getSandBoxToOriginalMap(Class<?> type, Long... originalIds) {
        return HashBiMap.create();
    }

    @Override
    public OriginalIdResponse getOriginalId(Class<?> type, Long id) {
        OriginalIdResponse response = new OriginalIdResponse();
        response.setOriginalId(id);
        return response;
    }

    @Override
    public Long getOriginalId(Object test) {
        return null;
    }

    @Override
    public OriginalIdResponse getProductionOriginalId(Class<?> type, Long id) {
        return null;
    }

    @Override
    public boolean isSandBoxable(String className) {
        return false;
    }

    @Override
    public boolean isPromote() {
        return false;
    }

    @Override
    public boolean isReject() {
        return false;
    }

    @Override
    public boolean isReplayOperation() {
        return false;
    }

    @Override
    public void optionallyIncludeDeletedItemsInQueriesAndCollections(Runnable runnable, boolean includeDeleted) {
        runnable.run();
    }

    @Override
    public Long getProductionRecordIdIfApplicable(EntityManager em, Object startFieldValue) {
        return (Long) em.unwrap(Session.class).getIdentifier(startFieldValue);
    }

    @Override
    public <T> T getTopMostOriginalRecord(T record) {
        return record;
    }

    @Override
    public void ignoreCloneCache(boolean ignoreCache) {
        //do nothing
    }
}
