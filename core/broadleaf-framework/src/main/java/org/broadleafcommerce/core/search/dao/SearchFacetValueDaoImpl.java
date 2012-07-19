/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.search.dao;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;

@Repository("blSearchFacetValueDao")
public class SearchFacetValueDaoImpl implements SearchFacetValueDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;
    
	
	@Override
	public <T, T2> List<T> readDistinctValuesForField(String fieldName, Class<T2> facetSearchClass, Class<T> fieldValueClass) {
		CriteriaQuery<T> criteria = em.getCriteriaBuilder().createQuery(fieldValueClass);
		Root<T2> root = criteria.from(facetSearchClass);
		criteria.distinct(true)
			.select(root.get(fieldName).as(fieldValueClass));
		return em.createQuery(criteria).getResultList();
	}

}
