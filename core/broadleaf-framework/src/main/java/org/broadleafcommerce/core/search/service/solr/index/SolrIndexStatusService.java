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
package org.broadleafcommerce.core.search.service.solr.index;

/**
 * Responsible for reading and writing the status using one or more {@link org.broadleafcommerce.core.search.service.solr.index.SolrIndexStatusProvider}
 * instances. {@link #getSeedStatusInstance()} can be used to provide a custom {@link org.broadleafcommerce.core.search.service.solr.index.IndexStatusInfo}
 * implementation.
 *
 * @author Jeff Fischer
 */
public interface SolrIndexStatusService {

    void setIndexStatus(IndexStatusInfo status);

    IndexStatusInfo getIndexStatus();

    /**
     * Provide a custom IndexStatusInfo instance to be used by the system.
     *
     * @return
     */
    IndexStatusInfo getSeedStatusInstance();

}
