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
import org.broadleafcommerce.catalog.domain.SellableItem;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.springframework.stereotype.Service;

@Service("searchService")
public class SearchServiceImpl implements SearchService {
	@Resource
	private CatalogService catalogService;

	@Override
	public List<SellableItem> performSearch(String queryString) {
		try {
			Analyzer analyzer = new StandardAnalyzer();
			Directory fsDir = FSDirectory.getDirectory(new File ("/temp/search/lucene" ));
			IndexSearcher is = new IndexSearcher (fsDir);
			QueryParser parser = new QueryParser("name", analyzer);
			Query query = parser.parse(queryString);
			TopDocCollector collector = new TopDocCollector(100);
			is.search(query, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			List<Long> ids = new ArrayList<Long>();
			for (int i=0; i<hits.length; i++){
			     int docId = hits[i].doc;
			     Document d = is.doc(docId);
			     String id = d.get("ID");
			     ids.add(new Long(id));
			}
			if (ids.size()>0){
				return catalogService.readSellableItemsByIds(ids);
			}

		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void rebuildSellableItemIndex() {
	    try {

	    	List<SellableItem> sellableItems = catalogService.readAllSellableItems();
			if (sellableItems != null){
		    	// Create a new indexer to index our files.
				File indexDir = new File ( "/temp/search/lucene" );
				if ( !indexDir.exists() ) {
					indexDir.mkdir();
				}
				MaxFieldLength mfl = new MaxFieldLength(IndexWriter.DEFAULT_MAX_FIELD_LENGTH);
				IndexWriter writer = new IndexWriter(indexDir, new StandardAnalyzer(), true, mfl);

				for (Iterator<SellableItem> itr = sellableItems.iterator(); itr.hasNext();){
					SellableItem item = itr.next();
					Document doc = new Document();
			        // set the sellable item id as a keyword -- this means that lucene won't try to
			        // manipulate this data when it is indexing this doc
			        doc.add ( new Field( "ID" , item.getId().toString(), Field.Store.YES, Field.Index.NO ) );
			        doc.add ( new Field( "name" , item.getName(), Field.Store.YES, Field.Index.ANALYZED ) );
			        doc.add ( new Field( "price" , item.getPrice()+"", Field.Store.YES, Field.Index.ANALYZED ) );

			        // Add the lucene document to the indexer to do the magic
			        writer.addDocument ( doc );
				}

			    writer.close ();
			}
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//---------------------- The methods below are if you are using Compass ----------
/*	@Override
	public void rebuildCatalogItemIndex() {
		Compass compass = null;

		EntityManagerFactory emf = null;
		try {
			CompassConfiguration config = CompassConfigurationFactory.newConfiguration();
			config.configure("/META-INF/compass.cfg.xml");
			compass = config.buildCompass();

			SingleCompassGps gps = new SingleCompassGps(compass);
			emf = Persistence.createEntityManagerFactory("BroadleafCommercePu");

			JpaGpsDevice jpaDevice = new JpaGpsDevice("jpa", emf);
			jpaDevice.setFetchCount(100);
			gps.addGpsDevice(jpaDevice);

			compass.getSearchEngineIndexManager().cleanIndex();
			compass.getSearchEngineIndexManager().createIndex();
			Properties props = new Properties();
			gps.setIndexProperties(props);
			gps.start();
			gps.index();
			gps.stop();
		} finally {
			if (emf != null) {
				emf.close();
			}
		}
	}*/

/*	public List<CatalogItem> performSearch(String criteria){
		Compass compass = null;
		CompassSession session = null;
		List<CatalogItem> catalogItems = new ArrayList<CatalogItem>();
		try {
			CompassConfiguration config = CompassConfigurationFactory.newConfiguration();
			config.configure("/META-INF/compass.cfg.xml");
			compass = config.buildCompass();
			session = compass.openSession();

			CompassHits hits = session.find(criteria);
            CompassDetachedHits detachedHits = hits.detach();

            Iterator<CompassHit> iterator = detachedHits.iterator();

            while(iterator.hasNext()){
            	DefaultCompassHit defaultCompassHit = (DefaultCompassHit)iterator.next();
            	CatalogItem item = (CatalogItem) defaultCompassHit.getData();
            	catalogItems.add(item);
            }

			CompassQueryBuilder builder = session.queryBuilder();
			CompassQuery query = builder.bool()
				.addShould(builder.term("", ""))
				.addShould(builder.term("", ""))
				.addShould(builder.fuzzy("", ""))
				.toQuery();

			//CompassHits hits = query.hits();



		} finally {
			session.close();
		}

		return catalogItems;
	}*/

}
