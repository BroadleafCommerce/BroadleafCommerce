/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

//    @Override
//    public Long getCombinedSandBoxVersionId(Class<?> linkedObjectType, Long requestedParent) {
//        return requestedParent;
//    }

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

    //    @Override
//    public void setupSandBoxState(Object clone, EntityManager em) {
//        //do nothing
//    }
//
//    @Override
//    public void archiveObject(Object start, EntityManager em) {
//        //do nothing
//    }
//
//    @Override
//    public String[] getSandBoxDiscriminatorFieldList() {
//        return new String[]{};
//    }

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
    public void optionallyIncludeDeletedItemsInQueriesAndCollections(Runnable runnable, boolean includeDeleted) {
        runnable.run();
    }

    @Override
    public Long getProductionRecordIdIfApplicable(EntityManager em, Object startFieldValue) {
        return (Long) em.unwrap(Session.class).getIdentifier(startFieldValue);
    }
}
