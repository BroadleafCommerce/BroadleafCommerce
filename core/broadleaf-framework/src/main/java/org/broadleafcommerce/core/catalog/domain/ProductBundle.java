package org.broadleafcommerce.core.catalog.domain;

import java.io.Serializable;
import java.util.List;

public interface ProductBundle extends Serializable {

    public String getPricingModel();

    public void setPricingModel(String pricingModel);

    public boolean isAutoBundle();

    public void setAutoBundle(boolean autoBundle);

    public boolean isItemsPromotable();

    public void setItemsPromotable(boolean itemsPromotable);

    public boolean isBundlePromotable();

    public void setBundlePromotable(boolean bundlePromotable);

    public List<ProductBundleItem> getBundleItems();

    public void setBundleItems(List<ProductBundleItem> bundleItems);

}
