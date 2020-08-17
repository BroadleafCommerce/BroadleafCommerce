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