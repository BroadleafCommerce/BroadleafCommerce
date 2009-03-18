package org.broadleafcommerce.pricing.service;

import java.util.List;
import java.util.Vector;

import org.broadleafcommerce.pricing.module.TaxModule;

public class TaxServiceImpl implements TaxService {

	private List<TaxModule> taxModules;
	
	@Override
	public TaxModule getTaxModuleByName(String taxModuleName) {
		for (TaxModule taxModule : taxModules) {
			if(taxModule.getName().equals(taxModuleName)){
				return taxModule;
			}
		}
		return null;
	}

	@Override
	public List<String> getTaxModuleNames() {
		List<String> taxModuleNames = new Vector<String>(); 
		for (TaxModule taxModule : taxModules) {
			taxModuleNames.add(taxModule.getName());
		}
		return taxModuleNames;
	}

	@Override
	public void setTaxModules(List<TaxModule> taxModules) {
		this.taxModules = taxModules;
		
	}

	@Override
	public List<TaxModule> getTaxModules() {
		return this.taxModules;
	}


	
}
