package org.broadleafcommerce.core.catalog.domain;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Chris Kittrell (ckittrell)
 */
public interface OrderedSkuMediaXref extends Serializable {

    SkuMediaXref getSkuMediaXref();

    void setSkuMediaXref(SkuMediaXref skuMediaXref);

    BigDecimal getDisplayOrder();

    void setDisplayOrder(BigDecimal displayOrder);

    boolean getShowInGallery();

    void setShowInGallery(boolean showInGallery);

}
