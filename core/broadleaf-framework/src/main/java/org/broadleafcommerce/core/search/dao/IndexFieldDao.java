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
package org.broadleafcommerce.core.search.dao;

import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.domain.IndexField;
import org.broadleafcommerce.core.search.domain.IndexFieldType;
import org.broadleafcommerce.core.search.domain.solr.FieldType;

import java.util.List;

/**
 * DAO used to interact with the database search fields
 *
 * @author Nick Crum (ncrum)
 */
public interface IndexFieldDao {

    /**
     * Returns the SearchField instance associated with the given field parameter, or null if non exists.
     *
     * @param field the Field we are looking for the SearchField for
     * @return a SearchField instance for the given field
     */
    public IndexField readIndexFieldForField(Field field);
    
    /**
     * Finds all of the {@link IndexField}s based on the entity type.
     * @param entityType
     * @return
     */
    public List<IndexField> readFieldsByEntityType(FieldEntity entityType);

    /**
     * Reads all of the {@link IndexField}s that are searchable on the entity type
     * @param entityType
     * @return
     */
    public List<IndexField> readSearchableFieldsByEntityType(FieldEntity entityType);

    List<IndexFieldType> getIndexFieldTypesByAbbreviation(String abbreviation);

    List<IndexFieldType> getIndexFieldTypes(FieldType facetFieldType);
}
