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
package org.broadleafcommerce.core.spec.search.service.solr

import org.apache.solr.client.solrj.SolrClient
import org.broadleafcommerce.common.locale.service.LocaleService
import org.broadleafcommerce.common.sandbox.SandBoxHelper
import org.broadleafcommerce.core.catalog.dao.ProductDao
import org.broadleafcommerce.core.catalog.dao.SkuDao
import org.broadleafcommerce.core.search.dao.IndexFieldDao
import org.broadleafcommerce.core.search.dao.SolrIndexDao
import org.broadleafcommerce.core.search.service.solr.SolrHelperService
import org.broadleafcommerce.core.search.service.solr.SolrHelperServiceImpl
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
        service.localeService = mockLocaleService
        service.shs = mockShs
        service.extensionManager = mockExtensionManager
        service.sandBoxHelper = mockSandBoxHelper
    }
    
}
