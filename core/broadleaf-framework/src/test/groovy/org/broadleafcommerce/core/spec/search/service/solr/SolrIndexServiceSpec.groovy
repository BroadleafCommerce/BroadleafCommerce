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
package org.broadleafcommerce.core.spec.search.service.solr

import org.broadleafcommerce.common.locale.service.LocaleService
import org.broadleafcommerce.common.sandbox.SandBoxHelper
import org.broadleafcommerce.core.catalog.dao.ProductDao
import org.broadleafcommerce.core.catalog.dao.SkuDao
import org.broadleafcommerce.core.catalog.domain.ProductImpl
import org.broadleafcommerce.core.catalog.domain.Sku
import org.broadleafcommerce.core.catalog.domain.SkuImpl
import org.broadleafcommerce.core.search.dao.FieldDao
import org.broadleafcommerce.core.search.dao.SolrIndexDao
import org.broadleafcommerce.core.search.domain.Field
import org.broadleafcommerce.core.search.service.solr.SolrHelperService
import org.broadleafcommerce.core.search.service.solr.SolrIndexServiceImpl
import org.broadleafcommerce.core.search.service.solr.SolrSearchServiceExtensionHandler
import org.broadleafcommerce.core.search.service.solr.SolrSearchServiceExtensionManager
import org.springframework.transaction.PlatformTransactionManager

import spock.lang.Specification


class SolrIndexServiceSpec extends Specification {
    
    SolrIndexServiceImpl service
    SolrIndexDao mockSolrIndexDao = Mock()
    FieldDao mockFieldDao = Mock()
    PlatformTransactionManager mockTransactionManager = Mock()
    ProductDao mockProductDao = Mock()
    SkuDao mockSkuDao = Mock()
    LocaleService mockLocaleService = Mock()
    SolrHelperService mockShs = Mock()
    SolrSearchServiceExtensionManager mockExtensionManager = Mock()
    SandBoxHelper mockSandBoxHelper = Mock()
    
    
    def setup() {
        mockLocaleService.findAllLocales() >> new ArrayList<Locale>()
        mockExtensionManager.getProxy() >> Mock(SolrSearchServiceExtensionHandler)
        
        service = Spy(SolrIndexServiceImpl)
        service.solrIndexDao = mockSolrIndexDao
        service.fieldDao = mockFieldDao
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
        mockFieldDao.readAllSkuFields() >> new ArrayList<Field>()
        
        service.buildDocument(*_) >> null
        
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
        service.buildIncrementalSkuIndex(skus,false)
        
        then:
        1 * mockSolrIndexDao.populateProductCatalogStructure(productIds, _)
        
    }
    
    def "Test that Categories are being properly associated to skus when creating the solr index for out of order skus"(){
        setup:
        mockFieldDao.readAllSkuFields() >> new ArrayList<Field>()
        
        service.buildDocument(*_) >> null
        
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
        service.buildIncrementalSkuIndex(skus,false)
        
        then:
        1 * mockSolrIndexDao.populateProductCatalogStructure(productIds, _)
        
    }
}
