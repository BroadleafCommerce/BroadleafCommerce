/*
 * #%L
 * BroadleafCommerce Framework
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
     * Returns the SearchField instance associated with the given field parameter, or null if non exists.
     *
     * @param fieldId the Field we are looking for the SearchField for
     * @return a SearchField instance for the given field
     */
    public IndexField readIndexFieldByFieldId(Long fieldId);
    
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
    List<IndexFieldType> getIndexFieldTypesByAbbreviationAndEntityType(String abbreviation, FieldEntity entityType);

    List<IndexFieldType> getIndexFieldTypesByAbbreviationOrPropertyName(String name);

    List<IndexFieldType> getIndexFieldTypes(FieldType facetFieldType);

    IndexField readIndexFieldByAbbreviation(String abbreviation);
    IndexField readIndexFieldByAbbreviationAndEntityType(String abbreviation, FieldEntity entityType);

}
