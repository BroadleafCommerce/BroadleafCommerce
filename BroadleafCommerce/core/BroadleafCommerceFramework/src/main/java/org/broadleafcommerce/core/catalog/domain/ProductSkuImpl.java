package org.broadleafcommerce.core.catalog.domain;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRODUCT_SKU")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class ProductSkuImpl extends ProductImpl implements ProductSku {

	private static final Log LOG = LogFactory.getLog(ProductSkuImpl.class);
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    @OneToOne(optional = true, targetEntity = SkuImpl.class)
    @JoinTable(name = "BLC_PRODUCT_SKU_ONE_XREF", joinColumns = @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "PRODUCT_ID"), inverseJoinColumns = @JoinColumn(name = "SKU_ID", referencedColumnName = "SKU_ID"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    protected Sku sku;

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.catalog.domain.ProductSku#getSku()
	 */
	public Sku getSku() {
		return sku;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.catalog.domain.ProductSku#setSku(org.broadleafcommerce.core.catalog.domain.Sku)
	 */
	public void setSku(Sku sku) {
		this.sku = sku;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((sku == null) ? 0 : sku.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductSkuImpl other = (ProductSkuImpl) obj;
		if (sku == null) {
			if (other.sku != null)
				return false;
		} else if (!sku.equals(other.sku))
			return false;
		return true;
	}
    
}
