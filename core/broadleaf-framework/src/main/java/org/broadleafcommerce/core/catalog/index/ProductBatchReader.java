package org.broadleafcommerce.core.catalog.index;

import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.service.SiteService;
import org.broadleafcommerce.common.util.tenant.IdentityExecutionUtils;
import org.broadleafcommerce.common.util.tenant.IdentityOperation;
import org.broadleafcommerce.core.catalog.dao.ProductDao;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.index.AbstractBatchReader;
import org.broadleafcommerce.core.search.index.BatchMarker;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

@Component("blProductIndexBatchReader")
public class ProductBatchReader extends AbstractBatchReader<BatchMarker> {
    
    protected static final int DEFAULT_BATCH_MULTIPLIER = 5;
    
    @Resource(name="blProductDao")
    protected ProductDao productDao;
    
    @Resource(name="blSiteService")
    protected SiteService siteService;

    @Override
    protected List<BatchMarker> readBatchInternal(final int page, final int pageSize, final Site site, final Catalog catalog) {
        final ArrayList<BatchMarker> markers = new ArrayList<>();
        final int batchSize;
        if (getBatchMultiplier() < 1) {
            batchSize = pageSize;
        } else {
            batchSize = pageSize * getBatchMultiplier();
        }
        IdentityExecutionUtils.runOperationByIdentifier(new IdentityOperation<Void, RuntimeException>() {

            @Override
            public Void execute() {
                List<Long> ids = productDao.readActiveProductIds(page, batchSize);
                if (!ids.isEmpty()) {
                    List<Long> holder = new ArrayList<>(pageSize);
                    for (Long id : ids) {
                        holder.add(id);
                        if (holder.size() == pageSize) {
                            BatchMarker marker = buildMarker(holder.get(0), holder.get(holder.size() - 1), site, catalog);
                            markers.add(marker);
                            holder.clear();
                        }
                    }
                    
                    if (!holder.isEmpty()) {
                        BatchMarker marker = buildMarker(holder.get(0), holder.get(holder.size() - 1), site, catalog);
                        markers.add(marker);
                        holder.clear();
                    }
                }
                return null;
            }
            
        }, site, catalog);
        
        return markers;
    }
    
    protected BatchMarker buildMarker(Long low, Long high, Site site, Catalog catalog) {
        BatchMarker marker = new BatchMarker();
        marker.setFiendEntity(getFieldEntityString());
        marker.setFirstValue(low);
        if (site != null) {
            marker.setSiteId(site.getId());
        }
        if (catalog != null) {
            marker.setCatalogId(catalog.getId());
        }
        return marker;
    }
    
    /**
     * This facilitates fewer round trips to the database.  This multiplies by the batchSize (or pageSize) to get 
     * provide a parameter for the DB request.  However
     * 
     * If you want the batch size to be returned from the DB, then this method should return 1.
     * 
     * Note that this does not affect the batch sizes returned to the caller.  This just facilitates fewere round trips 
     * to the DB to build the batches (or pages).
     * 
     * The default is 5.
     * 
     * @return
     */
    protected int getBatchMultiplier() {
        return DEFAULT_BATCH_MULTIPLIER;
    }
    
    protected String getFieldEntityString() {
        return FieldEntity.PRODUCT.getType();
    }

    @Override
    protected SiteService getSiteService() {
        return siteService;
    }
}
