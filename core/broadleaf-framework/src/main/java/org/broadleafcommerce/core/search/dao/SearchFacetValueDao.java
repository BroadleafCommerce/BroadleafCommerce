/*
 * Copyright 2012 the original author or authors.
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

import java.util.List;

/**
 * Dynamic DAO that is used in conjuction with the DatabaseProductSearchService
 * to generate distinct possible values for certain search facets
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface SearchFacetValueDao {

	/**
	 * Returns the distinct values for the given fieldName inside of the search clas sas a list of the specified 
	 * type. For example, reading the distinct values for "manufacturer" in the ProductImpl class and specifying
	 * the value class as String would search the ProductImpl entity's distinct manufacturers and return a 
	 * List<String> of these values.
	 * 
	 * @param fieldName
	 * @param fieldValueClass
	 * @return  the distinct values for the field
	 */
	public <T> List<T> readDistinctValuesForField(String fieldName, Class<T> fieldValueClass);

}