/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.client.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Generic persistent entity criteria transfer object
 * used by the {@link CriteriaTransferObjectConverter}.
 * 
 * <p>
 * 
 * Criteria transfer object pattern allows client-side
 * components to construct <em>serializable</em> versions
 * of {@link PersistentEntityCriteria} instances, which can
 * then be passed through the chosen communication mechanism
 * to the remote (server-side) component. This way, the user
 * can create persistent entity criteria on the client side
 * and use {@link CriteriaTransferObjectConverter} to transform
 * them into corresponding {@link PersistentEntityCriteria}
 * instances seamlessly on the server.
 * 
 * <p>
 * 
 * This class essentially acts as map-based container for
 * {@link FilterAndSortCriteria} instances, defining basic
 * entity criteria for target entity properties on the client.
 * 
 * @see FilterAndSortCriteria
 * @see CriteriaTransferObjectConverter
 * @see PersistentEntityCriteria
 * 
 * @author vojtech.szocs
 */
public class CriteriaTransferObject implements IsSerializable, Serializable {

    private static final long serialVersionUID = 8405827510072180355L;
    
    private Integer firstResult;
    private Integer maxResults;
    
    private Map<String, FilterAndSortCriteria> criteriaMap = new HashMap<String, FilterAndSortCriteria>();
    
    /**
     * @return Index of the starting element or <tt>null</tt>
     * representing no constraints on this paging parameter.
     */
    public Integer getFirstResult() {
        return firstResult;
    }
    
    /**
     * @param firstResult Index of the starting element or
     * <tt>null</tt> representing no constraints on this
     * paging parameter.
     */
    public void setFirstResult(Integer firstResult) {
        this.firstResult = firstResult;
    }
    
    /**
     * @return Maximum number of elements to return or
     * <tt>null</tt> representing no constraints on this
     * paging parameter.
     */
    public Integer getMaxResults() {
        return maxResults;
    }
    
    /**
     * @param maxResults Maximum number of elements to return
     * or <tt>null</tt> representing no constraints on this
     * paging parameter.
     */
    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }
    
    /**
     * Adds the given {@link FilterAndSortCriteria} instance
     * to this transfer object.
     * 
     * <p>
     * 
     * Note that the <tt>propertyId</tt> of the given
     * {@link FilterAndSortCriteria} instance must be unique
     * within the transfer object (in other words, existing
     * {@link FilterAndSortCriteria} with same <tt>propertyId</tt>
     * will be replaced by this method).
     * 
     * @param criteria {@link FilterAndSortCriteria} instance
     * to add.
     */
    public void add(FilterAndSortCriteria criteria) {
        criteriaMap.put(criteria.getPropertyId(), criteria);
    }
    
    /**
     * Returns a {@link FilterAndSortCriteria} instance
     * with the given <tt>propertyId</tt>.
     * 
     * <p>
     * 
     * When not found, the method creates and adds
     * an empty {@link FilterAndSortCriteria} instance
     * to the transfer object automatically.
     * 
     * @param propertyId Symbolic persistent entity property
     * identifier.
     * @return {@link FilterAndSortCriteria} instance with
     * the given <tt>propertyId</tt>.
     */
    public FilterAndSortCriteria get(String propertyId) {
        if (!criteriaMap.containsKey(propertyId))
            add(new FilterAndSortCriteria(propertyId));
        
        return criteriaMap.get(propertyId);
    }
    
    /**
     * Returns a set of symbolic persistent entity property
     * identifiers (<tt>propertyId</tt> values) for
     * {@link FilterAndSortCriteria} instances contained
     * within this transfer object.
     * 
     * @return Set of symbolic persistent entity property
     * identifiers (<tt>propertyId</tt> values).
     */
    public Set<String> getPropertyIdSet() {
        return criteriaMap.keySet();
    }
    
}
