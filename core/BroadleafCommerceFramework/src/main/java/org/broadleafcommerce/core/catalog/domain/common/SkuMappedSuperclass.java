package org.broadleafcommerce.core.catalog.domain.common;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.catalog.service.dynamic.DefaultDynamicSkuPricingInvocationHandler;
import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuPrices;
import org.broadleafcommerce.core.catalog.service.dynamic.SkuPricingConsiderationContext;
import org.broadleafcommerce.gwt.client.presentation.SupportedFieldType;
import org.broadleafcommerce.money.Money;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.broadleafcommerce.profile.util.DateUtil;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;

@MappedSuperclass
public abstract class SkuMappedSuperclass implements Sku {

	private static final Log LOG = LogFactory.getLog(SkuImpl.class);
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The id. */
    @Id
    @GeneratedValue(generator = "SkuId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "SkuId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SandBoxSkuImpl", allocationSize = 50)
    @Column(name = "SKU_ID")
    @SearchableId
    @AdminPresentation(friendlyName="Sku ID", group="Primary Key", hidden=true)
    protected Long id;

    /** The sale price. */
    @Column(name = "SALE_PRICE")
    @AdminPresentation(friendlyName="Sku Sale Price", order=9, group="Price", prominent=true, fieldType=SupportedFieldType.MONEY, groupOrder=3)
    protected BigDecimal salePrice;

    /** The retail price. */
    @Column(name = "RETAIL_PRICE", nullable=false)
    @AdminPresentation(friendlyName="Sku Retail Price", order=10, group="Price", prominent=true, fieldType=SupportedFieldType.MONEY, groupOrder=3)
    protected BigDecimal retailPrice;

    /** The name. */
    @Column(name = "NAME", nullable=false)
    @SearchableProperty
    @AdminPresentation(friendlyName="Sku Name", order=1, group="Sku Description", prominent=true, columnWidth="25%", groupOrder=4)
    protected String name;

    /** The description. */
    @Column(name = "DESCRIPTION")
    @AdminPresentation(friendlyName="Sku Description", order=2, group="Sku Description", largeEntry=true, groupOrder=4)
    protected String description;

    /** The long description. */
    @Column(name = "LONG_DESCRIPTION")
    @AdminPresentation(friendlyName="Sku Large Description", order=3, group="Sku Description", largeEntry=true, groupOrder=4)
    protected String longDescription;

    /** The taxable. */
    @Column(name = "TAXABLE_FLAG")
    @AdminPresentation(friendlyName="Sku Taxable", order=4, group="Sku Description", groupOrder=4)
    protected Character taxable;

    /** The discountable. */
    @Column(name = "DISCOUNTABLE_FLAG")
    @AdminPresentation(friendlyName="Sku Discountable", order=5, group="Sku Description", groupOrder=4)
    protected Character discountable;

    /** The available. */
    @Column(name = "AVAILABLE_FLAG")
    @AdminPresentation(friendlyName="Sku Available", order=6, group="Sku Description", groupOrder=4)
    protected Character available;

    /** The active start date. */
    @Column(name = "ACTIVE_START_DATE")
    @AdminPresentation(friendlyName="Sku Start Date", order=7, group="Sku Description", groupOrder=4)
    protected Date activeStartDate;

    /** The active end date. */
    @Column(name = "ACTIVE_END_DATE")
    @AdminPresentation(friendlyName="Sku End Date", order=8, group="Sku Description", groupOrder=4)
    protected Date activeEndDate;
    
    @Transient
    protected DynamicSkuPrices dynamicPrices = null;
    
    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#getId()
     */
    public Long getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#getSalePrice()
     */
    public Money getSalePrice() {
    	if (dynamicPrices != null) {
    		return dynamicPrices.getSalePrice();
    	}
    	if (
    			SkuPricingConsiderationContext.getSkuPricingConsiderationContext() != null && 
    			SkuPricingConsiderationContext.getSkuPricingConsiderationContext().size() > 0 &&
    			SkuPricingConsiderationContext.getSkuPricingService() != null
    	) {
    		DefaultDynamicSkuPricingInvocationHandler handler = new DefaultDynamicSkuPricingInvocationHandler(this);
    		Sku proxy = (Sku) Proxy.newProxyInstance(getClass().getClassLoader(), getClass().getInterfaces(), handler);
    		dynamicPrices = SkuPricingConsiderationContext.getSkuPricingService().getSkuPrices(proxy, SkuPricingConsiderationContext.getSkuPricingConsiderationContext());
    		handler.reset();
    		return dynamicPrices.getSalePrice();
    	}
        return salePrice == null ? null : new Money(salePrice);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setSalePrice(org.broadleafcommerce.util.money.Money)
     */
    public void setSalePrice(Money salePrice) {
        this.salePrice = Money.toAmount(salePrice);
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#getRetailPrice()
     */
    public Money getRetailPrice() {
    	if (dynamicPrices != null) {
    		return dynamicPrices.getRetailPrice();
    	}
    	if (
    			SkuPricingConsiderationContext.getSkuPricingConsiderationContext() != null && 
    			SkuPricingConsiderationContext.getSkuPricingConsiderationContext().size() > 0 &&
    			SkuPricingConsiderationContext.getSkuPricingService() != null
    	) {
    		DefaultDynamicSkuPricingInvocationHandler handler = new DefaultDynamicSkuPricingInvocationHandler(this);
    		Sku proxy = (Sku) Proxy.newProxyInstance(getClass().getClassLoader(), getClass().getInterfaces(), handler);
    		dynamicPrices = SkuPricingConsiderationContext.getSkuPricingService().getSkuPrices(proxy, SkuPricingConsiderationContext.getSkuPricingConsiderationContext());
    		handler.reset();
    		return dynamicPrices.getRetailPrice();
    	}
        return retailPrice == null ? null : new Money(retailPrice);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setRetailPrice(org.broadleafcommerce
     * .util.money.Money)
     */
    public void setRetailPrice(Money retailPrice) {
        this.retailPrice = Money.toAmount(retailPrice);
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#getListPrice()
     */
    public Money getListPrice() {
        return new Money(retailPrice);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setListPrice(org.broadleafcommerce
     * .util.money.Money)
     */
    public void setListPrice(Money listPrice) {
        this.retailPrice = Money.toAmount(listPrice);
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setDescription(java.lang.String)
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#getLongDescription()
     */
    public String getLongDescription() {
        return longDescription;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setLongDescription(java.lang
     * .String)
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#isTaxable()
     */
    public Boolean isTaxable() {
        if (taxable == null)
            return null;
        return taxable == 'Y' ? Boolean.TRUE : Boolean.FALSE;
    }

    /*
     * This is to facilitate serialization to non-Java clients
     */
    public Boolean getTaxable() {
        return isTaxable();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setTaxable(java.lang.Boolean)
     */
    public void setTaxable(Boolean taxable) {
        if (taxable == null) {
            this.taxable = null;
        } else {
            this.taxable = taxable ? 'Y' : 'N';
        }
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#isDiscountable()
     */
    public Boolean isDiscountable() {
        if (discountable == null)
            return null;
        return discountable == 'Y' ? Boolean.TRUE : Boolean.FALSE;
    }

    /*
     * This is to facilitate serialization to non-Java clients
     */
    public Boolean getDiscountable() {
        return isDiscountable();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setDiscountable(java.lang.Boolean)
     */
    public void setDiscountable(Boolean discountable) {
        if (discountable == null) {
            this.discountable = null;
        } else {
            this.discountable = discountable ? 'Y' : 'N';
        }
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#isAvailable()
     */
    public Boolean isAvailable() {
        if (available == null)
            return null;
        return available == 'Y' ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public Boolean getAvailable() {
    	return isAvailable();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setAvailable(java.lang.Boolean)
     */
    public void setAvailable(Boolean available) {
        if (available == null) {
            this.available = null;
        } else {
            this.available = available ? 'Y' : 'N';
        }
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#getActiveStartDate()
     */
    public Date getActiveStartDate() {
        return activeStartDate;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setActiveStartDate(java.util
     * .Date)
     */
    public void setActiveStartDate(Date activeStartDate) {
        this.activeStartDate = activeStartDate;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#getActiveEndDate()
     */
    public Date getActiveEndDate() {
        return activeEndDate;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Sku#setActiveEndDate(java.util.Date)
     */
    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Sku#isActive()
     */
    public boolean isActive() {
        if (LOG.isDebugEnabled()) {
            if (!DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), false)) {
                LOG.debug("sku, " + id + ", inactive due to date");
            }
        }
        return DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), false);
    }

    public boolean isActive(Product product, Category category) {
        if (LOG.isDebugEnabled()) {
            if (!DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), false)) {
                LOG.debug("sku, " + id + ", inactive due to date");
            } else if (!product.isActive()) {
                LOG.debug("sku, " + id + ", inactive due to product being inactive");
            } else if (!category.isActive()) {
                LOG.debug("sku, " + id + ", inactive due to category being inactive");
            }
        }
        return this.isActive() && product.isActive() && category.isActive();
    }
}
