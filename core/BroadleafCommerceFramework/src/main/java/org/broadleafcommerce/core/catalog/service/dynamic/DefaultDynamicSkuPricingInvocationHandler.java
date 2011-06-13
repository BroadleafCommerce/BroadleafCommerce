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
			Field retail = getSingleField(delegate.getClass(), "retailPrice");
			retail.setAccessible(true);
			retailPrice = new Money((BigDecimal) retail.get(delegate));
			Field sale = getSingleField(delegate.getClass(), "salePrice");
			sale.setAccessible(true);
			salePrice = new Money((BigDecimal) sale.get(delegate));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private Field getSingleField(Class<?> clazz, String fieldName) throws IllegalStateException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException nsf) {
            // Try superclass
            if (clazz.getSuperclass() != null) {
                return getSingleField(clazz.getSuperclass(), fieldName);
            }

            return null;
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
