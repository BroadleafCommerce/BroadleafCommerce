/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.search.dao;

import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.FieldEntity;

import java.util.List;

/**
 * DAO to facilitate interaction with Broadleaf fields.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface FieldDao {

    /**
     * Given an abbreviation, returns the Field object that maps to this abbreviation.
     * Note that the default Broadleaf implementation of Field will enforce a uniqueness
     * constraint on the abbreviation field and this method will reliably return one field
     * 
     * @param abbreviation
     * @return the Field that has this abbreviation
     */
    public Field readFieldByAbbreviation(String abbreviation);

    /**
     * Finds all fields based on the entity type.
     * @param entityType
     * @return
     */
    public List<Field> readFieldsByEntityType(FieldEntity entityType);

    /**
     * Persist an instance to the data layer.
     *
     * @param field the instance to persist
     * @return the instance after it has been persisted
     */
    public Field save(Field field);
}
