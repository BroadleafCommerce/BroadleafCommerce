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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.springframework.stereotype.Service;

@Service("blSearchService")
public class SearchServiceImpl implements SearchService {

    @Resource
    protected CatalogService catalogService;

    public List<Sku> performSearch(String queryString) throws CorruptIndexException, IOException, ParseException {
        Analyzer analyzer = new StandardAnalyzer();
        Directory fsDir = FSDirectory.getDirectory(new File("/temp/search/lucene"));
        IndexSearcher is = new IndexSearcher(fsDir);
        QueryParser parser = new QueryParser("name", analyzer);
        Query query = parser.parse(queryString);
        TopDocCollector collector = new TopDocCollector(100);
        is.search(query, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;
        List<Long> ids = new ArrayList<Long>();
        for (int i = 0; i < hits.length; i++) {
            int docId = hits[i].doc;
            Document d = is.doc(docId);
            String id = d.get("ID");
            ids.add(new Long(id));
        }
        if (ids.size() > 0) {
            return catalogService.findSkusByIds(ids);
        }

        return null;
    }

    public void rebuildSkuIndex() throws CorruptIndexException, LockObtainFailedException, IOException {
        List<Sku> skus = catalogService.findAllSkus();
        if (skus != null) {
            // Create a new indexer to index our files.
            File indexDir = new File("/temp/search/lucene");
            if (!indexDir.exists()) {
                indexDir.mkdir();
            }
            MaxFieldLength mfl = new MaxFieldLength(IndexWriter.DEFAULT_MAX_FIELD_LENGTH);
            IndexWriter writer = new IndexWriter(indexDir, new StandardAnalyzer(), true, mfl);

            for (Iterator<Sku> itr = skus.iterator(); itr.hasNext();) {
                Sku item = itr.next();
                Document doc = new Document();
                // set the sku id as a keyword -- this means that lucene won't try to
                // manipulate this data when it is indexing this doc
                doc.add(new Field("ID", item.getId().toString(), Field.Store.YES, Field.Index.NO));
                doc.add(new Field("name", item.getName(), Field.Store.YES, Field.Index.ANALYZED));
                doc.add(new Field("price", item.getSalePrice() + "", Field.Store.YES, Field.Index.ANALYZED));

                // Add the lucene document to the indexer to do the magic
                writer.addDocument(doc);
            }

            writer.close();
        }
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    // ---------------------- The methods below are if you are using Compass ----------
    /*
     * @Override public void rebuildProductIndex() { Compass compass = null; EntityManagerFactory emf = null; try { CompassConfiguration config = CompassConfigurationFactory.newConfiguration(); config.configure("/META-INF/compass.cfg.xml"); compass = config.buildCompass(); SingleCompassGps gps = new
     * SingleCompassGps(compass); emf = Persistence.createEntityManagerFactory("BroadleafCommercePu"); JpaGpsDevice jpaDevice = new JpaGpsDevice("jpa", emf); jpaDevice.setFetchCount(100); gps.addGpsDevice(jpaDevice); compass.getSearchEngineIndexManager().cleanIndex();
     * compass.getSearchEngineIndexManager().createIndex(); Properties props = new Properties(); gps.setIndexProperties(props); gps.start(); gps.index(); gps.stop(); } finally { if (emf != null) { emf.close(); } } }
     */

    /*
     * public List<Product> performSearch(String criteria){ Compass compass = null; CompassSession session = null; List<Product> products = new ArrayList<Product>(); try { CompassConfiguration config = CompassConfigurationFactory.newConfiguration(); config.configure("/META-INF/compass.cfg.xml");
     * compass = config.buildCompass(); session = compass.openSession(); CompassHits hits = session.find(criteria); CompassDetachedHits detachedHits = hits.detach(); Iterator<CompassHit> iterator = detachedHits.iterator(); while(iterator.hasNext()){ DefaultCompassHit defaultCompassHit =
     * (DefaultCompassHit)iterator.next(); Product item = (Product) defaultCompassHit.getData(); products.add(item); } CompassQueryBuilder builder = session.queryBuilder(); CompassQuery query = builder.bool() .addShould(builder.term("", "")) .addShould(builder.term("", ""))
     * .addShould(builder.fuzzy("", "")) .toQuery(); //CompassHits hits = query.hits(); } finally { session.close(); } return products; }
     */

}
