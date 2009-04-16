package org.broadleafcommerce.payment.order.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.payment.order.module.CreditCardModule;
import org.springframework.stereotype.Service;

@Service("creditCardService")
public class CreditCardServiceImpl implements CreditCardService {

    private Map<String, CreditCardModule> creditCardModules;

    public CreditCardServiceImpl() {
        creditCardModules = new HashMap<String, CreditCardModule>();
    }

    @Override
    public CreditCardModule getCreditCardModuleByName(String creditCardModuleName) {
        return creditCardModules.get(creditCardModuleName);
    }

    @Override
    public List<String> getCreditCardModuleNames() {
        List<String> response = new ArrayList<String>(creditCardModules.keySet());
        return response;
    }

    @Override
    public void setCreditCardModules(List<CreditCardModule> creditCardModules) {
        int length = creditCardModules.size();
        for (int j=0;j<length;j++){
            CreditCardModule temp = creditCardModules.get(j);
            this.creditCardModules.put(temp.getName(), temp);
        }
    }

    @Override
    public List<CreditCardModule> getCreditCardModules() {
        List<CreditCardModule> response = new ArrayList<CreditCardModule>(creditCardModules.values());
        return response;
    }

}
