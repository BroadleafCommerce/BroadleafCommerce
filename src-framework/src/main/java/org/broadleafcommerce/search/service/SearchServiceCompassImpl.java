/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.search.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.compass.core.Compass;
import org.compass.core.CompassContext;
import org.compass.core.CompassDetachedHits;
import org.compass.core.CompassIndexSession;
import org.compass.core.CompassSearchSession;
import org.compass.core.engine.SearchEngineIndexManager;
import org.springframework.stereotype.Service;

@Service("blSearchService")
public class SearchServiceCompassImpl implements SearchService {

    @CompassContext
    protected Compass compass;

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    private Logger logger = Logger.getLogger(this.getClass());

    public List<Product> performSearch(String input) {
        CompassSearchSession session = compass.openSearchSession();
        CompassDetachedHits hits = session.find(input).detach();
        session.close();
        List<Product> results = new ArrayList<Product>(hits.length());
        for (int i = 0; i < hits.length(); i++) {
            Product resourceProduct = (Product) hits.data(i);
            results.add(catalogService.findProductById(resourceProduct.getId()));
        }
        return results;
    }

    public void rebuildProductIndex() {
        logger.info("Rebuilding product index");
        List<Product> products = catalogService.findAllProducts();
        SearchEngineIndexManager manager = compass.getSearchEngineIndexManager();
        if (!manager.indexExists()) {
            manager.createIndex();
        }
        CompassIndexSession session = compass.openIndexSession();
        for (Product product : products) {
            session.save(product);
        }
        session.commit();
        session.close();
    }

}
