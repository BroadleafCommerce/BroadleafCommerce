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
package org.broadleafcommerce.common.util.dao;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * Utility class to construct typed query-language queries. This has an advantage over CriteriaQuery in that it will
 * be automatically responsive to polymorphism thanks to Hibernate's handling of query-language strings.
 * 
 * @author Andre Azzolini (apazzolini)
 *
 * @param <T> the class that is being queried for
 */
public class TypedQueryBuilder<T> {
    
    protected Class<T> rootClass;
    protected String rootAlias;
    protected List<TQRestriction> restrictions = new ArrayList<TQRestriction>();
    protected List<TQJoin> joins = new ArrayList<TQJoin>();
    protected Map<String, Object> paramMap = new HashMap<String, Object>();

    /**
     * Creates a new TypedQueryBuilder that will utilize the rootAlias as the named object of the class
     * 
     * @param rootClass
     * @param rootAlias
     */
    public TypedQueryBuilder(Class<T> rootClass, String rootAlias) {
        this.rootClass = rootClass;
        this.rootAlias = rootAlias;
    }
    
    /**
     * Adds a simple restriction to the query. Note that all restrictions present on the TypedQueryBuilder will be joined
     * with an AND clause.
     * 
     * @param expression
     * @param operation
     * @param parameter
     */
    public TypedQueryBuilder<T> addRestriction(String expression, String operation, Object parameter) {
        restrictions.add(new TQRestriction(expression, operation, parameter));
        return this;
    }
    
    /**
     * Adds an explicit TQRestriction object. Note that all restrictions present on the TypedQueryBuilder will be joined
     * with an AND clause.
     * 
     * @param restriction
     * @return
     */
    public TypedQueryBuilder<T> addRestriction(TQRestriction restriction) {
        restrictions.add(restriction);
        return this;
    }

    public TypedQueryBuilder<T> addJoin(TQJoin join) {
        joins.add(join);
        return this;
    }
    
    /**
     * Generates the query string based on the current contents of this builder. As the string is generated, this method
     * will also populate the paramMap, which binds actual restriction values.
     * 
     * Note that this method should typically not be invoked through DAOs. Instead, utilize {@link #toQuery(EntityManager)},
     * which will automatically generate the TypedQuery and populate the required parameters.
     * 
     * @return the QL string
     */
    public String toQueryString() {
        return toQueryString(false);
    }
    
    /**
     * Generates the query string based on the current contents of this builder. As the string is generated, this method
     * will also populate the paramMap, which binds actual restriction values.
     * 
     * Note that this method should typically not be invoked through DAOs. Instead, utilize {@link #toQuery(EntityManager)},
     * which will automatically generate the TypedQuery and populate the required parameters.
     * 
     * If you are using this as a COUNT query, you should look at the corresponding {@link #toCountQuery(EntityManager)}
     * 
     * @param whether or not the resulting query string should be used as a count query or not
     * @return the QL string
     */
    public String toQueryString(boolean count) {
        StringBuilder sb = getSelectClause(new StringBuilder(), count)
                .append(" FROM ").append(rootClass.getName()).append(" ").append(rootAlias);
        if (CollectionUtils.isNotEmpty(joins)) {
            sb.append(" JOIN");
            for (TQJoin join : joins) {
                sb.append(" ");
                sb.append(join.toQl());
            }
        }
        if (CollectionUtils.isNotEmpty(restrictions)) {
            sb.append(" WHERE ");
            for (int i = 0; i < restrictions.size(); i++) {
                TQRestriction r = restrictions.get(i);
                sb.append(r.toQl("p" + i, paramMap));
                if (i != restrictions.size() - 1) {
                    sb.append(" AND ");
                }
            }
        }
        return sb.toString();
    }

    /**
     * Adds the select query from {@link #toQueryString()}
     * 
     * @return <b>sb</b> with the select query appended to it
     */
    protected StringBuilder getSelectClause(StringBuilder sb, boolean count) {
        sb.append("SELECT ");
        if (count) {
            return sb.append("COUNT(*)");
        } else {
            return sb.append(rootAlias);
        }
    }
    
    /**
     * Returns a TypedQuery that represents this builder object. It will already have all of the appropriate parameter
     * values set and is able to be immediately queried against.
     * 
     * @param em
     * @return the TypedQuery
     */
    public TypedQuery<T> toQuery(EntityManager em) {
        TypedQuery<T> q = em.createQuery(toQueryString(), rootClass);
        fillParameterMap(q);
        return q;
    }
    
    public TypedQuery<Long> toCountQuery(EntityManager em) {
        TypedQuery<Long> q = em.createQuery(toQueryString(true), Long.class);
        fillParameterMap(q);
        return q;
    }
    
    protected void fillParameterMap(TypedQuery<?> q) {
        for (Entry<String, Object> entry : paramMap.entrySet()) {
            if (entry.getValue() != null) {
                q.setParameter(entry.getKey(), entry.getValue());
            }
        }
    }
    
    /**
     * @return the paramMap
     */
    public Map<String, Object> getParamMap() {
        return paramMap;
    }
    
}

