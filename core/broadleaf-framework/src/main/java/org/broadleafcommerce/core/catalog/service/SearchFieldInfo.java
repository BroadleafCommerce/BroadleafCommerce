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
