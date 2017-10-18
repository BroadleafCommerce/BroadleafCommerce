package org.broadleafcommerce.core.catalog.index;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.service.SiteService;
import org.broadleafcommerce.common.util.BLCCollectionUtils;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.common.util.TypedTransformer;
import org.broadleafcommerce.common.util.tenant.IdentityExecutionUtils;
import org.broadleafcommerce.common.util.tenant.IdentityOperation;
import org.broadleafcommerce.core.catalog.dao.ProductDao;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.search.dao.CatalogStructure;
import org.broadleafcommerce.core.search.dao.IndexFieldDao;
import org.broadleafcommerce.core.search.dao.SolrIndexDao;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.index.BatchMarker;
import org.broadleafcommerce.core.search.index.DocumentBuilder;
import org.broadleafcommerce.core.search.index.QueueEntryProcessor;
import org.broadleafcommerce.core.search.index.SearchIndexProcessStateHolder;
import org.broadleafcommerce.core.search.service.solr.SolrHelperService;
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexCachedOperation;
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexServiceExtensionManager;
import org.broadleafcommerce.core.search.service.solr.index.SolrUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * This component batch reads Products, converts them to SolrInputDocuments, and submits them to Solr.
 * 
 * @author Kelly Tisdell
 *
 */
@Component("blProductSolrIndexPageProcessor")
public class ProductSolrIndexPageProcessor implements QueueEntryProcessor<BatchMarker> {
    
    private static final Log LOG = LogFactory.getLog(ProductSolrIndexPageProcessor.class);
    public static final String LOCALES_KEY = ProductSolrIndexPageProcessor.class.getName() + ".ALL_LOCALES";

    @Resource(name="blSolrProductDocumentBuilder")
    protected DocumentBuilder<Product, SolrInputDocument> docBuilder;
    
    @Resource(name="blProductDao")
    protected ProductDao productDao;
    
    @Resource(name="blSiteService")
    protected SiteService siteService;
    
    @Resource(name = "blTransactionManager")
    protected PlatformTransactionManager transactionManager;
    
    @Resource(name = "blSolrUtil")
    protected SolrUtil solrUtil;
    
    @Resource(name = "blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;
    
    @Resource(name = "blSolrIndexDao")
    protected SolrIndexDao solrIndexDao;
    
    @Resource(name = "blSolrHelperService")
    protected SolrHelperService shs;
    
    @Resource(name = "blSolrIndexServiceExtensionManager")
    protected SolrIndexServiceExtensionManager extensionManager;
    
    @Resource(name = "blLocaleService")
    protected LocaleService localeService;
    
    @Resource(name = "blFieldDao")
    protected IndexFieldDao fieldDao;

    @Override
    public void process(final String processId, final BatchMarker entry) throws ServiceException {
        Assert.notNull(entry, "BatchMarker entry cannot be null.");
        Assert.isTrue(entry.getFieldEntity() != null && entry.getFieldEntity().equals(FieldEntity.PRODUCT.getType()), 
                "The BatchMarker entry had a FieldEntity of " + entry.getFieldEntity() + 
                    " but " + FieldEntity.PRODUCT.getType() + " was expected.");
        Assert.notNull(entry.getFirstValue(), "The BatchMarker firstValue was null.");
        
        final Long firstValue = entry.getFirstValue();
        final Long lastValue;
        if (entry.getLastValue() == null) {
            lastValue = firstValue;
        } else {
            Assert.isTrue(entry.getLastValue() >= entry.getFirstValue(), 
                    "lastValue must be greater than or equal to firstValue.");
            lastValue = entry.getLastValue();
        }
        
        TransactionStatus status = null;
        boolean txFinalized = false;
        try {
            //Begin transaction to ensure we have access to an entity manager when in a background thread.
            status = TransactionUtils.createTransaction("indexProductsInSolr",
                    TransactionDefinition.PROPAGATION_REQUIRED, transactionManager, true);
            
            final Long currentCatalogId = entry.getCatalogId();
            final Catalog currentCatalog;
            if (currentCatalogId != null) {
                currentCatalog = siteService.findCatalogById(currentCatalogId);
            } else {
                currentCatalog = null;
            }
            
            final UpdateRequest[] requestHolder = new UpdateRequest[1];
            performCachedOperation(new SolrIndexCachedOperation.CacheOperation() {
                
                @Override
                public void execute() throws ServiceException {
                    List<SolrInputDocument> docs = buildPage(processId, currentCatalog, firstValue, lastValue);
                    
                    if (docs != null && !docs.isEmpty()) {
                        UpdateRequest request = new UpdateRequest();
                        request.add(docs);
                        requestHolder[0] = request;
                    }
                }
            });
            
            TransactionUtils.finalizeTransaction(status, transactionManager, false);
            txFinalized = true;
            
            //Communicate with Solr outside of the DB transaction...
            if (requestHolder[0] != null) {
                String indexName = (String)SearchIndexProcessStateHolder.getAdditionalProperty(processId, 
                        SearchIndexProcessStateHolder.INDEX_NAME);
                if (indexName == null) {
                    solrUtil.updateIndex(requestHolder[0]);
                } else {
                    solrUtil.updateIndex(requestHolder[0], indexName);
                }
            }
            
        } catch (Exception e) {
            if (!txFinalized) {
                try {
                    TransactionUtils.finalizeTransaction(status, transactionManager, true);
                } catch (Exception rbe) {
                    LOG.error("Error rolling back a read only transaction. Ignorning.", rbe);
                }
            }
            throw new ServiceException("There was an error processing a BatchMarker entry for processId " + processId, e);
        } finally {
            //
        }
    }
    
    protected List<SolrInputDocument> buildPage(final String processId, 
            final Catalog currentCatalog, final Long firstValue, final Long lastValue) {
        
        return IdentityExecutionUtils.runOperationByIdentifier(
                new IdentityOperation<List<SolrInputDocument>, RuntimeException>() {

            @Override
            public List<SolrInputDocument> execute() {
                List<Product> products = null;
                sandBoxHelper.ignoreCloneCache(true);
                try {
                    products = productDao.readProductsInIdRange(firstValue, lastValue);
                    
                    final ArrayList<SolrInputDocument> documents = new ArrayList<>();
                    
                    if (products != null && !products.isEmpty()) {
                        extensionManager.getProxy().startBatchEvent(products);
                        List<Long> productIds = BLCCollectionUtils.collectList(products, new TypedTransformer<Long>() {
                            @Override
                            public Long transform(Object input) {
                                return shs.getCurrentProductId((Indexable) input);
                            }
                        });
                        
                        solrIndexDao.populateProductCatalogStructure(productIds, SolrIndexCachedOperation.getCache());
                        List<Locale> locales = getAllLocales(processId);
                        
                        for (Product product : products) {
                            SolrInputDocument doc = docBuilder.buildDocument(processId, product, locales);
                            if (doc != null) {
                                documents.add(doc);
                            }
                        }
                        
                        if (!documents.isEmpty()) {
                            extensionManager.getProxy().modifyBuiltDocuments(documents, products, 
                                    fieldDao.readFieldsByEntityType(FieldEntity.PRODUCT), locales);
                        }
                    }
                    
                    return documents;
                } finally {
                    try {
                        if (products != null) {
                            extensionManager.getProxy().endBatchEvent(products);
                        }
                    } finally {
                        sandBoxHelper.ignoreCloneCache(false);
                    }
                    
                }
            }
            
        }, null, null, currentCatalog, transactionManager);
    }
    
    @SuppressWarnings("unchecked")
    protected List<Locale> getAllLocales(String processId) {
        List<Locale> out = (List<Locale>)SearchIndexProcessStateHolder.getAdditionalProperty(processId, LOCALES_KEY);
        if (out == null) {
            synchronized(this.getClass()) {
                out = (List<Locale>)SearchIndexProcessStateHolder.getAdditionalProperty(processId, LOCALES_KEY);
                if (out == null) {
                    List<Locale> allLocales = localeService.findAllLocales();
                    
                    if (allLocales == null) {
                        out = new ArrayList<>();
                    } else {
                        Map<String, Locale> processedLocales = new HashMap<>();
                        // Optimize the list of locales we are looking at. If I have an 'en' and 'en_US' in the locale set and I'm
                        // not using the country code to index the values, then I only need to index the locale 'en'
                        for (Locale locale : allLocales) {
                            String localeCode = locale.getLocaleCode();
                            int underscoreLocation = localeCode.indexOf("_");
                            if (underscoreLocation > 0 && Boolean.FALSE.equals(locale.getUseCountryInSearchIndex())) {
                                String localeCodeWithoutCountry = localeCode.substring(0, underscoreLocation);
                                if (!processedLocales.containsKey(localeCodeWithoutCountry)) {
                                    processedLocales.put(localeCodeWithoutCountry, locale);
                                }
                            } else {
                                processedLocales.put(locale.getLocaleCode(), locale);
                            }
                        }
                        out = new ArrayList<>(processedLocales.values());
                    }
                    SearchIndexProcessStateHolder.setAdditionalProperty(processId, LOCALES_KEY, out);
                }
            }
        }
        
        return out;
    }
    
    protected void performCachedOperation(SolrIndexCachedOperation.CacheOperation cacheOperation) throws ServiceException {
        try {
            CatalogStructure cache = new CatalogStructure();
            SolrIndexCachedOperation.setCache(cache);
            cacheOperation.execute();
        } finally {
            SolrIndexCachedOperation.clearCache();
        }
    }
}
