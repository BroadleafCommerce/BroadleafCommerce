/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2019 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
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
