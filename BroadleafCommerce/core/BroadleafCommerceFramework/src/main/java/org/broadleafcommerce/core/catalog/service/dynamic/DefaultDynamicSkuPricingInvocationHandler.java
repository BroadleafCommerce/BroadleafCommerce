package org.broadleafcommerce.core.catalog.service.dynamic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.money.Money;

public class DefaultDynamicSkuPricingInvocationHandler implements InvocationHandler {

	private Sku delegate;
	private Money retailPrice;
	private Money salePrice;
	
	public DefaultDynamicSkuPricingInvocationHandler(Sku sku) {
		this.delegate = sku;
		try {
			Field retail = delegate.getClass().getDeclaredField("retailPrice");
			retail.setAccessible(true);
			retailPrice = new Money((BigDecimal) retail.get(delegate));
			Field sale = delegate.getClass().getDeclaredField("salePrice");
			sale.setAccessible(true);
			salePrice = new Money((BigDecimal) sale.get(delegate));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().equals("getRetailPrice")) {
			return retailPrice;
		} else if (method.getName().equals("getSalePrice")) {
			return salePrice;
		} else {
			return method.invoke(delegate, args);
		}
	}

	public void reset() {
		delegate = null;
		retailPrice = null;
		salePrice = null;
	}
}
