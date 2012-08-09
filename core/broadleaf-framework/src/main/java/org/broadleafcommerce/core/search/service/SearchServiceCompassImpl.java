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

package org.broadleafcommerce.core.search.service;

import org.apache.log4j.Logger;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.search.dao.SearchInterceptDao;
import org.broadleafcommerce.core.search.dao.SearchSynonymDao;
import org.broadleafcommerce.core.search.domain.SearchIntercept;
import org.broadleafcommerce.core.search.domain.SearchSynonym;
import org.compass.core.Compass;
import org.compass.core.CompassContext;
import org.compass.core.CompassDetachedHits;
import org.compass.core.CompassIndexSession;
import org.compass.core.CompassSearchSession;
import org.compass.core.engine.SearchEngineIndexManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmclain
 *
 */
@Deprecated
@Service("blSearchService")
public class SearchServiceCompassImpl implements SearchService {

	private static final Logger LOG = Logger.getLogger(SearchServiceCompassImpl.class);
	
    @CompassContext
    protected Compass compass;

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Resource(name = "blSearchInterceptDao")
    protected SearchInterceptDao searchInterceptDao;

    @Resource(name = "blSearchSynonymDao")
    protected SearchSynonymDao searchSynonymDao;

    public List<Product> performSearch(String input) {
        CompassSearchSession session = compass.openSearchSession();
        CompassDetachedHits hits = session.find(input).detach();
        session.close();
        List<Product> results = new ArrayList<Product>(hits.length());
        for (int i = 0; i < hits.length(); i++) {
            Product resourceProduct = (Product) hits.data(i);
            Product ormProduct = catalogService.findProductById(resourceProduct.getId());
            if (ormProduct != null && ormProduct.isActive()) {
                results.add(catalogService.findProductById(resourceProduct.getId()));
            }
        }
        return results;
    }

    public void rebuildProductIndex() {
        LOG.info("Rebuilding product index");
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

    public SearchIntercept getInterceptForTerm(String term) {
        return searchInterceptDao.findInterceptByTerm(term);
    }

    public List<SearchIntercept> getAllSearchIntercepts() {
        return searchInterceptDao.findAllIntercepts();
    }

    public void createSearchIntercept(SearchIntercept intercept) {
        searchInterceptDao.createIntercept(intercept);
    }

    public void deleteSearchIntercept(SearchIntercept intercept) {
        searchInterceptDao.deleteIntercept(intercept);
    }

    public void updateSearchIntercept(SearchIntercept intercept) {
        searchInterceptDao.updateIntercept(intercept);
    }

    public void createSearchSynonym(SearchSynonym synonym) {
        searchSynonymDao.createSynonym(synonym);
    }

    public void deleteSearchSynonym(SearchSynonym synonym) {
        searchSynonymDao.deleteSynonym(synonym);
    }

    public List<SearchSynonym> getAllSearchSynonyms() {
        return searchSynonymDao.getAllSynonyms();
    }

    public void updateSearchSynonym(SearchSynonym synonym) {
        searchSynonymDao.updateSynonym(synonym);
    }

    public Compass getCompass() {
        return compass;
    }

    public void setCompass(Compass compass) {
        this.compass = compass;
    }

    public CatalogService getCatalogService() {
        return catalogService;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public SearchInterceptDao getSearchInterceptDao() {
        return searchInterceptDao;
    }

    public void setSearchInterceptDao(SearchInterceptDao searchInterceptDao) {
        this.searchInterceptDao = searchInterceptDao;
    }

    public SearchSynonymDao getSearchSynonymDao() {
        return searchSynonymDao;
    }

    public void setSearchSynonymDao(SearchSynonymDao searchSynonymDao) {
        this.searchSynonymDao = searchSynonymDao;
    }

}
