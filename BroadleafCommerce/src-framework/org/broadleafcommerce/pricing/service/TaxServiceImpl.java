package org.broadleafcommerce.pricing.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.pricing.module.TaxModule;
import org.springframework.stereotype.Service;

@Service("taxService")
public class TaxServiceImpl implements TaxService {

	private Map<String, TaxModule> taxModules;
	
	public TaxServiceImpl() {
		taxModules = new HashMap<String, TaxModule>();
	}
	
	@Override
	public TaxModule getTaxModuleByName(String taxModuleName) {
		return taxModules.get(taxModuleName);
	}

	@Override
	public List<String> getTaxModuleNames() {
		List<String> response = new ArrayList<String>(taxModules.keySet());
		return response;
	}

	@Override
	public void setTaxModules(List<TaxModule> taxModules) {
		int length = taxModules.size();
		for (int j=0;j<length;j++){
			TaxModule temp = taxModules.get(j);
			this.taxModules.put(temp.getName(), temp);
		}
	}

	@Override
	public List<TaxModule> getTaxModules() {
		List<TaxModule> response = new ArrayList<TaxModule>(taxModules.values());
		return response;
	}

}
