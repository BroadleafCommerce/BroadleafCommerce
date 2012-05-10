package org.broadleafcommerce.core.catalog.domain;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRODUCT_BUNDLE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "ProductImpl_bundleProduct")
public class ProductBundleImpl extends ProductImpl implements ProductBundle {

    private static final long serialVersionUID = 1L;

    @Column(name = "PRICING_MODEL")
    // TODO: use new data-driven enum for possible values of BUNDLE, ITEM_SUM
    protected String pricingModel;

    @Column(name = "AUTO_BUNDLE")
    @AdminPresentation(friendlyName = "Auto Bundle")
    protected Boolean autoBundle;

    @Column(name = "ITEMS_PROMOTABLE")
    @AdminPresentation(friendlyName = "Items are promotable")
    protected Boolean itemsPromotable;

    @Column(name = "BUNDLE_PROMOTABLE")
    @AdminPresentation(friendlyName = "Bundle is promotable")
    protected Boolean bundlePromotable;

    @OneToMany(mappedBy = "bundle", targetEntity = ProductBundleItemImpl.class, cascade = { CascadeType.ALL })
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
    @BatchSize(size = 50)
    protected List<ProductBundleItem> bundleItems;

    public String getPricingModel() {
        return pricingModel;
    }

    public void setPricingModel(String pricingModel) {
        this.pricingModel = pricingModel;
    }
    
    public Money getRetailPrice() {
        if (getPricingModel().equals("ITEM_SUM")) {
            return getBundleItemsRetailPrice();
        } else if (getPricingModel().equals(("BUNDLE"))) {
            return super.getDefaultSku().getRetailPrice();
        }
        return null;
    }
    
    public Money getSalePrice() {
        if (getPricingModel().equals("ITEM_SUM")) {
            return getBundleItemsSalePrice();
        } else if (getPricingModel().equals(("BUNDLE"))) {
            return super.getDefaultSku().getSalePrice();
        }
        return null;
    }
    
    public Money getBundleItemsRetailPrice() {
        Money price = new Money(BigDecimal.ZERO);
        for (ProductBundleItem item : bundleItems){
            price.add(item.getProduct().getDefaultSku().getRetailPrice());
        }
        return price;
    }
    
    public Money getBundleItemsSalePrice() {
        Money price = new Money(BigDecimal.ZERO);
        for (ProductBundleItem item : bundleItems){
            price.add(item.getSalePrice());
        }
        return price;
    }

    public Boolean getAutoBundle() {
        return autoBundle;
    }

    public void setAutoBundle(Boolean autoBundle) {
        this.autoBundle = autoBundle;
    }

    public Boolean getItemsPromotable() {
        return itemsPromotable;
    }

    public void setItemsPromotable(Boolean itemsPromotable) {
        this.itemsPromotable = itemsPromotable;
    }

    public Boolean getBundlePromotable() {
        return bundlePromotable;
    }

    public void setBundlePromotable(Boolean bundlePromotable) {
        this.bundlePromotable = bundlePromotable;
    }

    public List<ProductBundleItem> getBundleItems() {
        return bundleItems;
    }

    public void setBundleItems(List<ProductBundleItem> bundleItems) {
        this.bundleItems = bundleItems;
    }

}
