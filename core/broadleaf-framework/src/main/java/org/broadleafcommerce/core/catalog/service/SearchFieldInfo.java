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
package org.broadleafcommerce.core.catalog.service;

import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.broadleafcommerce.core.search.service.type.SearchFieldType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Chad Harchar (charchar)
 */
public class SearchFieldInfo {

    protected SearchFieldInfo(){
        //Protected constructor to hide the implicit public one.
    }

    public static final Map<String, FieldType> SEARCH_FIELD_SOLR_FIELD_TYPE = new HashMap<String, FieldType>();
    static {
        SEARCH_FIELD_SOLR_FIELD_TYPE.put(SearchFieldType.BOOLEAN.getType(), FieldType.BOOLEAN);
        SEARCH_FIELD_SOLR_FIELD_TYPE.put(SearchFieldType.DATE.getType(), FieldType.DATE);
        SEARCH_FIELD_SOLR_FIELD_TYPE.put(SearchFieldType.DECIMAL.getType(), FieldType.DOUBLE);
        SEARCH_FIELD_SOLR_FIELD_TYPE.put(SearchFieldType.INTEGER.getType(), FieldType.INT);
        SEARCH_FIELD_SOLR_FIELD_TYPE.put(SearchFieldType.MONEY.getType(), FieldType.PRICE);
        SEARCH_FIELD_SOLR_FIELD_TYPE.put(SearchFieldType.STRING.getType(), FieldType.TEXT);
    }

}
