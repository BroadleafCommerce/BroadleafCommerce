package org.broadleafcommerce.payment.order.service;

import java.util.List;

import org.broadleafcommerce.payment.order.module.BankAccountModule;

public interface BankAccountService {

    public BankAccountModule getBankAccountModuleByName(String bankAccountModuleName);

    public List<String> getBankAccountModuleNames();

    public void setBankAccountModules(List<BankAccountModule> bankAccountModules);

    public List<BankAccountModule> getBankAccountModules();

}