/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import java.io.Serializable;

/**
 * This interface is used for representing a FieldType for a SearchField entity
 *
 * @author Nick Crum (ncrum)
 */
public interface SearchFieldType extends Serializable, MultiTenantCloneable<SearchFieldType> {

    FieldType getSearchableFieldType();

    void setSearchableFieldType(FieldType searchableFieldType);

    Long getId();

    void setId(Long id);

    SearchField getSearchField();

    void setSearchField(SearchField searchField);
;
}
