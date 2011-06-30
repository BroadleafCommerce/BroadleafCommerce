package org.broadleafcommerce.core.catalog.domain.common;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.core.catalog.domain.SkuAttribute;

@MappedSuperclass
public abstract class SkuAttributeMappedSuperclass implements SkuAttribute {

	private static final long serialVersionUID = 1L;

	/** The id. */
    @Id
    @GeneratedValue(generator = "SkuAttributeId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "SkuAttributeId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SkuAttributeImpl", allocationSize = 50)
    @Column(name = "SKU_ATTR_ID")
    protected Long id;
    
    /** The name. */
    @Column(name = "NAME", nullable=false)
    protected String name;

    /** The value. */
    @Column(name = "VALUE", nullable=false)
    protected String value;

    /** The searchable. */
    @Column(name = "SEARCHABLE")
    protected Boolean searchable;
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#getId()
     */
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#getValue()
     */
    public String getValue() {
        return value;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#setValue(java.lang.String)
     */
    public void setValue(String value) {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#getSearchable()
     */
    public Boolean getSearchable() {
        return searchable;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#setSearchable(java.lang.Boolean)
     */
    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return value;
    }
}
