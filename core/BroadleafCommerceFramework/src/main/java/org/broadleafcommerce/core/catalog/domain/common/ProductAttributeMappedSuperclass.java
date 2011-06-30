package org.broadleafcommerce.core.catalog.domain.common;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.core.catalog.domain.ProductAttribute;
import org.broadleafcommerce.presentation.AdminPresentation;

@MappedSuperclass
public abstract class ProductAttributeMappedSuperclass implements ProductAttribute {

	private static final long serialVersionUID = 1L;

    /** The id. */
    @Id
    @GeneratedValue(generator = "ProductAttributeId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "ProductAttributeId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "ProductAttributeImpl", allocationSize = 50)
    @Column(name = "PRODUCT_ATTRIBUTE_ID")
    protected Long id;
    
    /** The name. */
    @Column(name = "NAME", nullable=false)
    @AdminPresentation(friendlyName="Attribute Name", order=1, group="Description", prominent=true)
    protected String name;

    /** The value. */
    @Column(name = "VALUE", nullable=false)
    @AdminPresentation(friendlyName="Attribute Value", order=2, group="Description", prominent=true)
    protected String value;

    /** The searchable. */
    @Column(name = "SEARCHABLE")
    @AdminPresentation(friendlyName="Attribute Searchable", order=3, group="Description", prominent=true)
    protected Boolean searchable;
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#getId()
     */
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#getValue()
     */
    public String getValue() {
        return value;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#setValue(java.lang.String)
     */
    public void setValue(String value) {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#getSearchable()
     */
    public Boolean getSearchable() {
        return searchable;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#setSearchable(java.lang.Boolean)
     */
    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return value;
    }
}
