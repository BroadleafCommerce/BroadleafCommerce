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
package org.broadleafcommerce.core.spec.search.service.solr

import org.apache.solr.client.solrj.SolrClient
import org.broadleafcommerce.common.locale.service.LocaleService
import org.broadleafcommerce.common.sandbox.SandBoxHelper
import org.broadleafcommerce.core.catalog.dao.ProductDao
import org.broadleafcommerce.core.catalog.dao.SkuDao
import org.broadleafcommerce.core.catalog.domain.ProductImpl
import org.broadleafcommerce.core.catalog.domain.Sku
import org.broadleafcommerce.core.catalog.domain.SkuImpl
import org.broadleafcommerce.core.search.dao.IndexFieldDao
import org.broadleafcommerce.core.search.dao.SolrIndexDao
import org.broadleafcommerce.core.search.domain.Field
import org.broadleafcommerce.core.search.domain.FieldEntity
import org.broadleafcommerce.core.search.service.solr.SolrHelperService
import org.broadleafcommerce.core.search.service.solr.SolrHelperServiceImpl
import org.broadleafcommerce.core.search.service.solr.SolrSearchServiceExtensionHandler
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexServiceExtensionHandler
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexServiceExtensionManager
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexServiceImpl
import org.springframework.transaction.PlatformTransactionManager
import spock.lang.Specification

class SolrIndexServiceSpec extends Specification {
    
    SolrIndexServiceImpl service
    SolrIndexDao mockSolrIndexDao = Mock()
    IndexFieldDao mockFieldDao = Mock()
    PlatformTransactionManager mockTransactionManager = Mock()
    ProductDao mockProductDao = Mock()
    SkuDao mockSkuDao = Mock()
    LocaleService mockLocaleService = Mock()
    SolrClient mockSolrClient = Mock()
    SolrHelperService mockShs = Spy(SolrHelperServiceImpl)
    SolrIndexServiceExtensionManager mockExtensionManager = Mock()
    SandBoxHelper mockSandBoxHelper = Mock()
    
    def setup() {
        mockLocaleService.findAllLocales() >> new ArrayList<Locale>()
        mockExtensionManager.getProxy() >> Mock(SolrIndexServiceExtensionHandler)

        service = Spy(SolrIndexServiceImpl)
        service.solrIndexDao = mockSolrIndexDao
        service.indexFieldDao = mockFieldDao
        service.transactionManager = mockTransactionManager
        service.productDao = mockProductDao
        service.skuDao = mockSkuDao
        service.localeService = mockLocaleService
        service.shs = mockShs
        service.extensionManager = mockExtensionManager
        service.sandBoxHelper = mockSandBoxHelper
    }
    
    def "Test that Categories are being properly associated to skus when creating the solr index"(){
        setup:
        mockFieldDao.readFieldsByEntityType(FieldEntity.SKU) >> new ArrayList<Field>()
        
        service.buildDocument(*_) >> null
        service.useSku = true;

        SkuImpl testSku1 = Mock(SkuImpl)
        SkuImpl testSku2 = Mock(SkuImpl)
        ProductImpl testProduct1 = Mock(ProductImpl)
        ProductImpl testProduct2 = Mock(ProductImpl)
        
        testProduct1.getId() >> 1
        testProduct2.getId() >> 2
        
        testSku1.getProduct() >> testProduct1
        testSku2.getProduct() >> testProduct2
        
        List<Sku> skus = [testSku1, testSku2]
        
        List<Long> productIds = [1, 2]
     
        when:
        service.buildIncrementalIndex(skus, mockSolrClient)
        
        then:
        1 * mockSolrIndexDao.populateProductCatalogStructure(productIds, _)
        
    }
    
    def "Test that Categories are being properly associated to skus when creating the solr index for out of order skus"(){
        setup:
        mockFieldDao.readFieldsByEntityType(FieldEntity.SKU) >> new ArrayList<Field>()
        
        service.buildDocument(*_) >> null
        service.useSku = true;

        SkuImpl testSku1 = Mock(SkuImpl)
        SkuImpl testSku2 = Mock(SkuImpl)
        SkuImpl testSku3 = Mock(SkuImpl)
        ProductImpl testProduct1 = Mock(ProductImpl)
        ProductImpl testProduct2 = Mock(ProductImpl)
        ProductImpl testProduct3 = Mock(ProductImpl)
        
        testProduct1.getId() >> 1
        testProduct2.getId() >> 2
        testProduct3.getId() >> 3
        
        testSku1.getProduct() >> testProduct1
        testSku2.getProduct() >> testProduct2
        testSku3.getProduct() >> testProduct3
        
        List<Sku> skus = [testSku3, testSku1, testSku2]
        
        List<Long> productIds = [3, 1, 2]
     
        when:
        service.buildIncrementalIndex(skus, mockSolrClient)
        
        then:
        1 * mockSolrIndexDao.populateProductCatalogStructure(productIds, _)
        
    }
}
