package org.broadleafcommerce.core.order.domain;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.broadleafcommerce.core.catalog.domain.Sku;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_EXTN_PRICED_DISCRETE_ORDER_ITEM")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
public class DynamicPriceDiscreteOrderItemImpl extends DiscreteOrderItemImpl implements DynamicPriceDiscreteOrderItem {

	private static final long serialVersionUID = 1L;

	@Override
	public void setSku(Sku sku) {
		this.sku = sku;
		this.name = sku.getName();
	}

	@Override
	public boolean updatePrices() {
		return false;
	}

}
