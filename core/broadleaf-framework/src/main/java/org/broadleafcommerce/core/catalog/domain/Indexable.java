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
/**
 * 
 */
package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.core.search.dao.FieldDao;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.service.solr.SolrSearchServiceImpl;
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexService;


/**
 * Mainly a marker interface denoting that the entity should be indexed for search
 * 
 * @see {@link SolrIndexService}
 * @see {@link SolrSearchServiceImpl}
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface Indexable {

    /**
     * The primary key for this indexable item that gets stored in the search index
     * 
     * {@see SolrHelperService#getIndexableIdFieldName()}
     */
    public Long getId();

    /**
     * Which type of {@link Field} should be queried for when looking up database-driven search fields to store in the
     * search index
     * 
     * @see SolrIndexService#buildIncrementalIndex(java.util.List, org.apache.solr.client.solrj.SolrClient)
     * @see FieldDao#readFieldsByEntityType(FieldEntity)
     */
    public FieldEntity getFieldEntityType();
    
}
