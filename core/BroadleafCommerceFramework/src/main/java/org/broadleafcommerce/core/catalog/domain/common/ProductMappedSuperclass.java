package org.broadleafcommerce.core.catalog.domain.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductDimension;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.ProductWeight;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.broadleafcommerce.profile.util.DateUtil;
import org.broadleafcommerce.profile.vendor.service.type.ContainerShapeType;
import org.broadleafcommerce.profile.vendor.service.type.ContainerSizeType;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;
import org.hibernate.annotations.Index;

@MappedSuperclass
public abstract class ProductMappedSuperclass implements Product {

	private static final Log LOG = LogFactory.getLog(ProductImpl.class);
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The id. */
    @Id
    @GeneratedValue(generator = "ProductId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "ProductId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "ProductImpl", allocationSize = 50)
    @Column(name = "PRODUCT_ID")
    @SearchableId
    @AdminPresentation(friendlyName="Product ID", group="Primary Key", hidden=true)
    protected Long id;

    /** The name. */
    @Column(name = "NAME", nullable=false)
    @SearchableProperty(name="productName")
    @Index(name="PRODUCT_NAME_INDEX", columnNames={"NAME"})
    @AdminPresentation(friendlyName="Product Name", order=1, group="Product Description", prominent=true, columnWidth="25%", groupOrder=1)
    protected String name;

    /** The description. */
    @Column(name = "DESCRIPTION")
    @AdminPresentation(friendlyName="Product Description", order=2, group="Product Description", prominent=false, largeEntry=true, groupOrder=1)
    protected String description;

    /** The long description. */
    @Column(name = "LONG_DESCRIPTION")
    @SearchableProperty(name="productDescription")
    @AdminPresentation(friendlyName="Product Long Description", order=3, group="Product Description", prominent=false, largeEntry=true, groupOrder=1)
    protected String longDescription;

    /** The active start date. */
    @Column(name = "ACTIVE_START_DATE")
    @AdminPresentation(friendlyName="Product Active Start Date", order=8, group="Active Date Range", groupOrder=2)
    protected Date activeStartDate;

    /** The active end date. */
    @Column(name = "ACTIVE_END_DATE")
    @AdminPresentation(friendlyName="Product Active End Date", order=9, group="Active Date Range", groupOrder=2)
    protected Date activeEndDate;

    /** The product model number */
    @Column(name = "MODEL")
    @SearchableProperty(name="productModel")
    @AdminPresentation(friendlyName="Product Model", order=4, group="Product Description", prominent=true, groupOrder=1)
    protected String model;

    /** The manufacture name */
    @Column(name = "MANUFACTURE")
    @SearchableProperty(name="productManufacturer")
    @AdminPresentation(friendlyName="Product Manufacturer", order=5, group="Product Description", prominent=true, groupOrder=1)
    protected String manufacturer;

    /** The product dimensions **/
    @Embedded
    protected ProductDimension dimension = new ProductDimension();

    /** The product weight **/
    @Embedded
    protected ProductWeight weight = new ProductWeight();
    
    @Column(name = "IS_FEATURED_PRODUCT", nullable=false)
    @AdminPresentation(friendlyName="Is Featured Product", order=6, group="Product Description", prominent=false)
    protected boolean isFeaturedProduct = false;

    @Column(name = "IS_MACHINE_SORTABLE")
    @AdminPresentation(friendlyName="Is Product Machine Sortable", order=7, group="Product Description", prominent=false)
    protected boolean isMachineSortable = true;
    
    /** The skus. */
    @Transient
    protected List<Sku> skus = new ArrayList<Sku>();
    
    @Transient
    protected String promoMessage;
    
    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Product#getId()
     */
    public Long getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Product#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Product#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Product#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Product#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Product#setDescription(java.lang
     * .String)
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Product#getLongDescription()
     */
    public String getLongDescription() {
        return longDescription;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Product#setLongDescription(java.
     * lang.String)
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Product#getActiveStartDate()
     */
    public Date getActiveStartDate() {
        return activeStartDate;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Product#setActiveStartDate(java.
     * util.Date)
     */
    public void setActiveStartDate(Date activeStartDate) {
        this.activeStartDate = activeStartDate;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Product#getActiveEndDate()
     */
    public Date getActiveEndDate() {
        return activeEndDate;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Product#setActiveEndDate(java.util
     * .Date)
     */
    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Product#isActive()
     */
    public boolean isActive() {
        if (LOG.isDebugEnabled()) {
            if (!DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), false)) {
                LOG.debug("product, " + id + ", inactive due to date");
            }
        }
        return DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), false);
    }
    
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public ProductDimension getDimension() {
        return dimension;
    }

    public void setDimension(ProductDimension dimension) {
    	this.dimension = dimension;
    }

    public BigDecimal getWidth() {
        return dimension==null?null:dimension.getWidth();
    }

    public void setWidth(BigDecimal width) {
        dimension.setWidth(width);
    }

    public BigDecimal getHeight() {
        return dimension==null?null:dimension.getHeight();
    }

    public void setHeight(BigDecimal height) {
        dimension.setHeight(height);
    }

    public BigDecimal getDepth() {
        return dimension==null?null:dimension.getDepth();
    }

    public void setDepth(BigDecimal depth) {
        dimension.setDepth(depth);
    }

    public void setGirth(BigDecimal girth) {
        dimension.setGirth(girth);
    }

    public BigDecimal getGirth() {
        return dimension==null?null:dimension.getGirth();
    }

    public ContainerSizeType getSize() {
        return dimension==null?null:dimension.getSize();
    }

    public void setSize(ContainerSizeType size) {
        dimension.setSize(size);
    }

    public ContainerShapeType getContainer() {
        return dimension==null?null:dimension.getContainer();
    }

    public void setContainer(ContainerShapeType container) {
        dimension.setContainer(container);
    }

    /**
     * Returns the product dimensions as a String (assumes measurements are in inches)
     * @return a String value of the product dimensions
     */
    public String getDimensionString() {
        return dimension==null?null:dimension.getDimensionString();
    }
    
    public boolean isFeaturedProduct() {
        return isFeaturedProduct;
    }

    public void setFeaturedProduct(boolean isFeaturedProduct) {
        this.isFeaturedProduct = isFeaturedProduct;
    }

    public boolean isMachineSortable() {
        return isMachineSortable;
    }

    public void setMachineSortable(boolean isMachineSortable) {
        this.isMachineSortable = isMachineSortable;
    }

    public ProductWeight getWeight() {
        return weight;
    }

    public void setWeight(ProductWeight weight) {
        this.weight = weight;
    }

	/**
	 * @return the promoMessage
	 */
	public String getPromoMessage() {
		return promoMessage;
	}

	/**
	 * @param promoMessage the promoMessage to set
	 */
	public void setPromoMessage(String promoMessage) {
		this.promoMessage = promoMessage;
	}
}
