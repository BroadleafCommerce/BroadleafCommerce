package org.broadleafcommerce.core.catalog.index;

import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.service.SiteService;
import org.broadleafcommerce.common.util.tenant.IdentityExecutionUtils;
import org.broadleafcommerce.common.util.tenant.IdentityOperation;
import org.broadleafcommerce.core.catalog.dao.ProductDao;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.index.BatchMarker;
import org.broadleafcommerce.core.search.index.DocumentBuilder;
import org.broadleafcommerce.core.search.index.QueueEntryProcessor;
import org.broadleafcommerce.core.search.index.SearchIndexProcessStateHolder;
import org.broadleafcommerce.core.search.service.solr.index.SolrUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

/**
 * This component batch reads Products, converts them to SolrInputDocuments, and submits them to Solr.
 * 
 * @author Kelly Tisdell
 *
 */
@Component("blProductSolrQueueEntryProcessor")
public class ProductSolrQueueEntryProcessor implements QueueEntryProcessor<BatchMarker> {

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

    @Override
    public void process(final String processId, final BatchMarker entry) throws ServiceException {
        try {
            Assert.notNull(entry, "");
            Assert.isTrue(entry.getFieldEntity() != null && entry.getFieldEntity().equals(FieldEntity.PRODUCT.getType()), 
                    "The BatchMarker entry had a FieldEntity of " + entry.getFieldEntity() + 
                        " but " + FieldEntity.PRODUCT.getType() + " was expected.");
            Assert.notNull(entry.getFirstValue(), "The BatchMarker firstValue was null.");
            
            final Long firstValue = entry.getFirstValue();
            final Long lastValue;
            if (entry.getLastValue() == null) {
                lastValue = firstValue;
            } else {
                lastValue = entry.getLastValue();
            }
            final Long currentCatalogId = entry.getCatalogId();
            final Catalog currentCatalog;
            if (currentCatalogId != null) {
                currentCatalog = siteService.findCatalogById(currentCatalogId);
            } else {
                currentCatalog = null;
            }
            
            List<SolrInputDocument> docs = IdentityExecutionUtils.runOperationByIdentifier(
                    new IdentityOperation<List<SolrInputDocument>, RuntimeException>() {

                @Override
                public List<SolrInputDocument> execute() {
                    ArrayList<SolrInputDocument> documents = new ArrayList<>();
                    List<Product> products = productDao.readProductsInIdRange(firstValue, lastValue);
                    if (products != null && !products.isEmpty()) {
                        for (Product product : products) {
                            documents.add(docBuilder.buildDocument(product));
                        }
                    }
                    
                    return documents;
                }
                
            }, null, null, currentCatalog, transactionManager);
            
            if (!docs.isEmpty()) {
                UpdateRequest request = new UpdateRequest();
                request.add(docs);
                String indexName = (String)SearchIndexProcessStateHolder.getAdditionalProperty(processId, 
                        SearchIndexProcessStateHolder.INDEX_NAME);
                if (indexName == null) {
                    solrUtil.updateIndex(request);
                } else {
                    solrUtil.updateIndex(request, indexName);
                }
            }
            
        } catch (Exception e) {
            throw new ServiceException("There was an error processing a BatchMarker entry for processId " + processId, e);
        }
    }
    
}
