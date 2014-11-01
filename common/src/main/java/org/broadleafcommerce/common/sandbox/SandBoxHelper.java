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

import com.google.common.collect.BiMap;

import java.util.List;

import javax.persistence.EntityManager;

/**
 * Utility class for working with sandboxable entities. The functionality provided
 * by {@link org.broadleafcommerce.common.sandbox.DefaultSandBoxHelper} in core is generally
 * innocuous, with more interesting sandbox related functionality being provided
 * in the commercial enterprise module.
 *
 * @see org.broadleafcommerce.common.sandbox.DefaultSandBoxHelper
 * @author Jeff Fischer
 */
public interface SandBoxHelper {

    /**
     * A query hint that can be passed to Query.setHint(). Should
     * be a regular expression matching Hibernate filter names. This
     * drives which sandbox filters will be active for the query.
     */
    public static class QueryHints {

        public static final String FILTER_INCLUDE = "filterInclude";

    }

    /**
     * Retrieve a list of values that includes the the original ids passed in and any
     * sandbox versions of those ids, if available. This is useful for some queries that
     * require search values for both the original id and the sandbox id.
     *
     * @param em the Entity Manager
     * @param type the type of the entity in question
     * @param originalIds one or more ids values for which sandbox versions should be included
     * @return the merged id list
     */
    List<Long> mergeCloneIds(EntityManager em, Class<?> type, Long... originalIds);

    /**
     * Retrieve a map keyed by sandbox id, with the value being the matching original
     * item id for that sandbox item. Only members from the ids list passed
     * in that have a sandbox counterpart are included.
     *
     * @param em the Entity Manager
     * @param type the type of the entity in question
     * @param ids list of ids to check
     * @return the map of sandbox to original ids
     */
    BiMap<Long, Long> getSandBoxToOriginalMap(EntityManager em, Class<?> type, Long... ids);

    /**
     * Return the sandbox version id for the requested original id. Will return null
     * if no sandbox version is available.
     *
     * @param entityManager the Entity Manager
     * @param linkedObjectType the type of the entity in question
     * @param requestedParent the id to check
     * @return the sandbox version, or null
     */
    Long getSandBoxVersionId(EntityManager entityManager, Class<?> linkedObjectType, Long requestedParent);

    /**
     * Return the sandbox version id for the requested original id. Will return null
     * if no sandbox version is available.
     *
     * @param entityManager the Entity Manager
     * @param linkedObjectType the type of the entity in question
     * @param requestedParent the id to check
     * @param includeSandBoxInheritance override whether or not parent sandbox ids should be included in the query. Can be null - True by default.
     * @return the sandbox version, or null
     */
    Long getSandBoxVersionId(EntityManager entityManager, Class<?> linkedObjectType, Long requestedParent, Boolean includeSandBoxInheritance);

    /**
     * Return the original id for the requested id. Will return the passed in id if
     * the type is not sandboxable. Will return null if the passed in id
     * is not a sandbox record, or if it's a sandbox add.
     *
     * @param em the Entity Manager
     * @param type the type of the entity in question
     * @param id the id to check
     * @return the original id for the requested sandbox id
     */
    OriginalIdResponse getOriginalId(EntityManager em, Class<?> type, Long id);

    /**
     * Setup basic required fields for sandbox support
     *
     * @param clone the entity instance to setup
     * @param em the Entity Manager
     */
    void setupSandBoxState(Object clone, EntityManager em);

    /**
     * Archive an object so that it is no longer recognized
     * by the sandbox support
     *
     * @param start the object to archive
     * @param em the Entity Manager
     */
    void archiveObject(Object start, EntityManager em);

    /**
     * Retrieve the field names related to sandbox support
     *
     * @return the sandbox support fields
     */
    String[] getSandBoxDiscriminatorFieldList();

    /**
     * Whether or not the class is sandboxable
     *
     * @param className the classname to check
     * @return whether or not it's sandboxable
     */
    boolean isSandBoxable(String className);

    /**
     * Is the current thread involved in a promote operation?
     *
     * @return whether or not a promote is currently taking place
     */
    boolean isPromote();

    /**
     * Is the current thread involved in a reject operation?
     *
     * @return whether or not a reject is currently taking place
     */
    boolean isReject();

    /**
     * For the passed in code block (Runnable), whether or not sandbox entities marked
     * as deleted should be included in any results from queries or lazy collection
     * retrievals.
     *
     * @param runnable the block of code for which this setting should be in effect
     * @param includeDeleted whether or not to include deleted sandbox items
     */
    void optionallyIncludeDeletedItemsInQueriesAndCollections(Runnable runnable, boolean includeDeleted);

    public class OriginalIdResponse {

        private boolean recordFound = false;
        private Long originalId;

        public boolean isRecordFound() {
            return recordFound;
        }

        public void setRecordFound(boolean recordFound) {
            this.recordFound = recordFound;
        }

        public Long getOriginalId() {
            return originalId;
        }

        public void setOriginalId(Long originalId) {
            this.originalId = originalId;
        }
    }
}
