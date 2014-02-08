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

import java.util.List;

import javax.persistence.EntityManager;

/**
 * @author Jeff Fischer
 */
public interface SandBoxHelper {

    public static class QueryHints {

        public static final String FILTER_INCLUDE = "filterInclude";

    }

    List<Long> mergeCloneIds(EntityManager em, Class<?> type, Long... originalIds);

    Long getSandBoxVersionId(EntityManager entityManager, Class<?> linkedObjectType, Long requestedParent);

    Long getOriginalId(EntityManager em, Class<?> type, Long id);

    void setupSandBoxState(Object clone, EntityManager em);

    void archiveObject(Object start, EntityManager em);

    String[] getSandBoxDiscriminatorFieldList();

    boolean isSandBoxable(String className);

    boolean isPromote();

    boolean isReject();

    void optionallyIncludeDeletedItemsInQueriesAndCollections(Runnable runnable, boolean includeDeleted);

}
