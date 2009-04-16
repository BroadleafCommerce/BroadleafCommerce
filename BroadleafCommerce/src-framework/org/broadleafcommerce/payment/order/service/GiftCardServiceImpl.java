package org.broadleafcommerce.payment.order.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.payment.order.module.GiftCardModule;
import org.springframework.stereotype.Service;

@Service("giftCardService")
public class GiftCardServiceImpl implements GiftCardService   {

    private Map<String, GiftCardModule> giftCardModules;

    public GiftCardServiceImpl() {
        giftCardModules = new HashMap<String, GiftCardModule>();
    }

    @Override
    public GiftCardModule getGiftCardModuleByName(String giftCardModuleName) {
        return giftCardModules.get(giftCardModuleName);
    }

    @Override
    public List<String> getGiftCardModuleNames() {
        List<String> response = new ArrayList<String>(giftCardModules.keySet());
        return response;
    }

    @Override
    public void setGiftCardModules(List<GiftCardModule> giftCardModules) {
        int length = giftCardModules.size();
        for (int j=0;j<length;j++){
            GiftCardModule temp = giftCardModules.get(j);
            this.giftCardModules.put(temp.getName(), temp);
        }
    }

    @Override
    public List<GiftCardModule> getGiftCardModules() {
        List<GiftCardModule> response = new ArrayList<GiftCardModule>(giftCardModules.values());
        return response;
    }

}
