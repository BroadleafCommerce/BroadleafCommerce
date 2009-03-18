package org.broadleafcommerce.pricing.service;

import java.util.List;

import org.broadleafcommerce.pricing.module.ShippingModule;

public interface ShippingService {
	public List<ShippingModule> getShippingModules();
	
	public List<String> getShippingModuleNames();
	
	public void setShippingModules(List<ShippingModule> shippingModules);
	
	public ShippingModule getShippingModuleByName(String name);
	
}
