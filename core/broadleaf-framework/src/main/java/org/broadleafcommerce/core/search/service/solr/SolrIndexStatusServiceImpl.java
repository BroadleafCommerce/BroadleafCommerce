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
package org.broadleafcommerce.core.search.service.solr;

import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * @author Jeff Fischer
 */
@Service("blSolrIndexStatusService")
public class SolrIndexStatusServiceImpl implements SolrIndexStatusService {

    @Resource(name="blSolrIndexStatusProviders")
    List<SolrIndexStatusProvider> providers;

    @Override
    public synchronized void setIndexStatus(IndexStatusInfo status) {
        for (SolrIndexStatusProvider provider : providers) {
            provider.handleUpdateIndexStatus(status);
        }
    }

    @Override
    public synchronized IndexStatusInfo getIndexStatus() {
        IndexStatusInfo status = getSeedStatusInstance();
        for (SolrIndexStatusProvider provider : providers) {
            provider.readIndexStatus(status);
        }
        return status;
    }

    @Override
    public IndexStatusInfo getSeedStatusInstance() {
        return new IndexStatusInfoImpl();
    }
}
