package org.broadleafcommerce.core.catalog.domain.weave;

import org.broadleafcommerce.core.catalog.domain.OrderedSkuMediaXref;
import org.broadleafcommerce.core.catalog.domain.OrderedSkuMediaXrefImpl;
import org.broadleafcommerce.core.catalog.domain.SkuMediaXref;

import javax.persistence.Embedded;
import java.math.BigDecimal;

/**
 * @author Chris Kittrell (ckittrell)
 */
public class WeaveOrderedSkuMediaXrefImpl implements OrderedSkuMediaXref {

    @Embedded
    protected OrderedSkuMediaXrefImpl embeddableOrderedSkuMediaXref;

    @Override
    public BigDecimal getDisplayOrder() {
        return getEmbeddableOrderedSkuMediaXref(false).getDisplayOrder();
    }

    @Override
    public void setDisplayOrder(BigDecimal displayOrder) {
        getEmbeddableOrderedSkuMediaXref(true).setDisplayOrder(displayOrder);
    }

    @Override
    public boolean getShowInGallery() {
        return getEmbeddableOrderedSkuMediaXref(false).getShowInGallery();
    }

    @Override
    public void setShowInGallery(boolean showInGallery) {
        getEmbeddableOrderedSkuMediaXref(true).setShowInGallery(showInGallery);
    }

    @Override
    public SkuMediaXref getSkuMediaXref() {
        return getEmbeddableOrderedSkuMediaXref(false).getSkuMediaXref();
    }

    @Override
    public void setSkuMediaXref(SkuMediaXref skuMediaXref) {
        getEmbeddableOrderedSkuMediaXref(true).setSkuMediaXref(skuMediaXref);
    }

    protected OrderedSkuMediaXrefImpl getEmbeddableOrderedSkuMediaXref(boolean assign) {
        OrderedSkuMediaXrefImpl temp = embeddableOrderedSkuMediaXref;
        if (temp == null) {
            temp = new OrderedSkuMediaXrefImpl();
            if (assign) {
                embeddableOrderedSkuMediaXref = temp;
            }
        }
        if (temp.getSkuMediaXref() == null) {
            temp.setSkuMediaXref((SkuMediaXref) this);
        }
        return temp;
    }
}
