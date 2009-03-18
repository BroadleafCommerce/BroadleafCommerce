package org.broadleafcommerce.pricing.service;

import java.util.List;
import java.util.Vector;

import org.broadleafcommerce.pricing.module.ShippingModule;

public class ShippingServiceImpl implements ShippingService{

	private List<ShippingModule> shippingModules;
	
	@Override
	public ShippingModule getShippingModuleByName(String name) {
		for (ShippingModule shippingModule : shippingModules) {
			if(shippingModule.getName().equals(name)){
				return shippingModule;
			}
		}
		return null;
	}

	@Override
	public List<String> getShippingModuleNames(){
		List<String> smNames = new Vector<String>();
		for (ShippingModule shippingModule : shippingModules) {
			smNames.add(shippingModule.getName());
		}
		return smNames;
	}
	
	@Override
	public List<ShippingModule> getShippingModules() {
		return this.shippingModules;
	}

	@Override
	public void setShippingModules(List<ShippingModule> shippingModules) {
		this.shippingModules = shippingModules;
		
	}



}
