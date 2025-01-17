/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.service.solr;

import junit.framework.TestCase;
import org.broadleafcommerce.core.search.dao.IndexFieldDao;
import org.broadleafcommerce.core.search.domain.FieldImpl;
import org.broadleafcommerce.core.search.domain.IndexFieldImpl;
import org.broadleafcommerce.core.search.domain.IndexFieldTypeImpl;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.easymock.EasyMock;

import java.util.Collections;

public class MvelToSearchCriteriaConversionServiceImplTest extends TestCase {

    //Test that "product.?defaultSku.?fulfillmentType.getType()" gets converted to "defaultSku.fulfillmentType.type"
    //Solr uses the attribute and not the method 
    public void testConvertGetTypeFieldName() {
        String getTypeFieldName = "product.?defaultSku.?fulfillmentType.getType()";
        MvelToSearchCriteriaConversionServiceImpl test = new MvelToSearchCriteriaConversionServiceImpl();
        String converted = test.convertFieldName(getTypeFieldName);
        assertEquals("defaultSku.fulfillmentType.type", converted);
    }
    
    public void testConvert() {
        IndexFieldDao indexFieldDao = EasyMock.createMock(IndexFieldDao.class);
        IndexFieldTypeImpl type = new IndexFieldTypeImpl();
        IndexFieldImpl indexField = new IndexFieldImpl();
        FieldImpl field = new FieldImpl();
        field.setAbbreviation("qwer");
        indexField.setField(field);
        type.setFieldType(FieldType.STRING);
        type.setIndexField(indexField);
        EasyMock.expect(indexFieldDao.getIndexFieldTypesByAbbreviationOrPropertyName(EasyMock.anyObject())).andReturn(Collections.singletonList(type));
        MvelToSearchCriteriaConversionServiceImpl test = new MvelToSearchCriteriaConversionServiceImpl();
        test.indexFieldDao=indexFieldDao;
        EasyMock.replay(indexFieldDao);
        SearchCriteria convert = test.convert("CollectionUtils.intersection(product.?defaultSku.?fulfillmentType.getType(),[\"PHYSICAL_SHIP\",\"SPACE_SHIP\"]).size()>0");
        assertEquals("Expect 1 filter query", 1,convert.getFilterQueries().size());
        assertEquals("(qwer_s:(\"PHYSICAL_SHIP\",\"SPACE_SHIP\"))", convert.getFilterQueries().iterator().next());

    }
}
