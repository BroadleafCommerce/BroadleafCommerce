package org.broadleafcommerce.payment.order.service;

import java.util.List;

import org.broadleafcommerce.payment.order.module.GiftCardModule;

public interface GiftCardService {

    public GiftCardModule getGiftCardModuleByName(String giftCardModuleName);

    public List<String> getGiftCardModuleNames();

    public void setGiftCardModules(List<GiftCardModule> giftCardModules);

    public List<GiftCardModule> getGiftCardModules();

}