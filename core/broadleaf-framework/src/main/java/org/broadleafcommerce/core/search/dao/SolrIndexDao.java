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

import java.util.List;

/**
 * Provides some specialized catalog retrieval methods for {@link org.broadleafcommerce.core.search.service.solr.index.SolrIndexService} for maximum
 * efficiency of solr document creation during indexing.
 *
 * @author Jeff Fischer
 */
public interface SolrIndexDao {

    /**
     * Populate the contents of a lightweight catalog structure for a list of products.
     *
     * @param productIds
     * @param catalogStructure lightweight container defining product and category hierarchies
     * @see org.broadleafcommerce.core.search.dao.CatalogStructure
     */
    void populateProductCatalogStructure(List<Long> productIds, CatalogStructure catalogStructure);

}
