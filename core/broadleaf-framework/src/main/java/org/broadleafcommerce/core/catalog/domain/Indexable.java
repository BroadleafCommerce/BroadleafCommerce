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
/**
 * 
 */
package org.broadleafcommerce.core.catalog.domain;

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
     * {@see SolrIndexService#buildIncrementalIndex(java.util.List, org.apache.solr.client.solrj.SolrServer)}
     * {@see FieldDao#readFieldsByEntityType(FieldEntity)}
     */
    public FieldEntity getFieldEntityType();
    
}
