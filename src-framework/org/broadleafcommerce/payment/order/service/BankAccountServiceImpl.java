package org.broadleafcommerce.payment.order.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.payment.order.module.BankAccountModule;
import org.springframework.stereotype.Service;

@Service("bankAccountService")
public class BankAccountServiceImpl implements BankAccountService  {

    private Map<String, BankAccountModule> bankAccountModules;

    public BankAccountServiceImpl() {
        bankAccountModules = new HashMap<String, BankAccountModule>();
    }

    @Override
    public BankAccountModule getBankAccountModuleByName(String bankAccountModuleName) {
        return bankAccountModules.get(bankAccountModuleName);
    }

    @Override
    public List<String> getBankAccountModuleNames() {
        List<String> response = new ArrayList<String>(bankAccountModules.keySet());
        return response;
    }

    @Override
    public void setBankAccountModules(List<BankAccountModule> bankAccountModules) {
        int length = bankAccountModules.size();
        for (int j=0;j<length;j++){
            BankAccountModule temp = bankAccountModules.get(j);
            this.bankAccountModules.put(temp.getName(), temp);
        }
    }

    @Override
    public List<BankAccountModule> getBankAccountModules() {
        List<BankAccountModule> response = new ArrayList<BankAccountModule>(bankAccountModules.values());
        return response;
    }

}
