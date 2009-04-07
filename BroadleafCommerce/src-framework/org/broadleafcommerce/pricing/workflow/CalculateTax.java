package org.broadleafcommerce.pricing.workflow;

import javax.annotation.Resource;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.pricing.module.TaxModule;
import org.broadleafcommerce.pricing.service.TaxService;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class CalculateTax extends BaseActivity {
	
	@Resource
	private TaxService taxService;
	
	private String taxModuleName;

	@Override
	public ProcessContext execute(ProcessContext context) throws Exception {
		Order order = ((OfferContext)context).getSeedData();
		
		TaxModule module = taxService.getTaxModuleByName(taxModuleName);
		order = module.calculateTaxForOrder(order);
		
		context.setSeedData(order);		
		return context;
	}

	/**
	 * @return the taxModuleName
	 */
	public String getTaxModuleName() {
		return taxModuleName;
	}

	/**
	 * @param taxModuleName the taxModuleName to set
	 */
	public void setTaxModuleName(String taxModuleName) {
		this.taxModuleName = taxModuleName;
	}

}
