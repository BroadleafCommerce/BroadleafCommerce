package org.broadleafcommerce.payment.order.service;

import java.util.List;

import org.broadleafcommerce.payment.order.module.CreditCardModule;

public interface CreditCardService {

    public List<CreditCardModule> getCreditCardModules();

    public void setCreditCardModules(List<CreditCardModule> creditCardModuleModules);

    public List<String> getCreditCardModuleNames();

    public CreditCardModule getCreditCardModuleByName(String creditCardModuleName);

}
