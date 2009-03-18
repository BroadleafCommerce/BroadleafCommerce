package org.broadleafcommerce.pricing.service;

import java.util.List;

import org.broadleafcommerce.pricing.module.TaxModule;

public interface TaxService {
	public List<TaxModule> getTaxModules();
	
	public void setTaxModules(List<TaxModule> taxModules);

	public List<String> getTaxModuleNames();
	
	public TaxModule getTaxModuleByName(String taxModuleName);
	
}
