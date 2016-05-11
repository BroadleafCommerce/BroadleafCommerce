/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.offer;

import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.core.offer.service.type.StackabilityType;
import org.testng.annotations.DataProvider;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("deprecation")
public class OfferDataProvider {

    @DataProvider(name = "offerDataProvider")
    public static Object[][] provideBasicOffer(){
        List<Offer> allOffers = new ArrayList<Offer>();
        OfferImpl o = new OfferImpl();
        o.setDiscountType(OfferDiscountType.AMOUNT_OFF);
        o.setValue(new BigDecimal("5.00"));
        o.setName("Some test offer");
        o.setPriority(100);
        o.setStackableWithOtherOffers(StackabilityType.YES);
        o.setStartDate(SystemTime.asDate());
        o.setEndDate(new Date(SystemTime.asMillis()+100000000));
        o.setTargetSystem("WEB");
        o.setType(OfferType.ORDER_ITEM);
        o.setAppliesToOrderRules(
                "package org.broadleafcommerce.core.offer.service;"+
                "import org.broadleafcommerce.core.offer.domain.Offer;"+
                "import org.broadleafcommerce.core.order.domain.Order;"+
                "import org.broadleafcommerce.core.order.domain.OrderItem;"+
                "import org.broadleafcommerce.type.OfferType;"+
                "import java.util.List;"+
                "global List orderItems;"+
                "global List offerPackages;"+
                "rule \"Offer 1 Rule\" "+
                "salience 100"+
                "when "+
                "  orderItem : OrderItem(sku == 1) "+
                "  "+
                " then"+
                "   System.err.println(\"applying offer 1\");"+
                "   orderItem.addRulesCandidateOffer"+
        "end");

        allOffers.add(o);
        o = new OfferImpl();
        o.setDiscountType(OfferDiscountType.AMOUNT_OFF);
        o.setValue(new BigDecimal("5.00"));
        o.setName("Second test offer");
        o.setPriority(100);
        o.setStartDate(SystemTime.asDate());
        o.setEndDate(new Date(SystemTime.asMillis()+100000000));
        o.setTargetSystem("WEB");
        o.setType(OfferType.FULFILLMENT_GROUP);
        o.setAppliesToOrderRules(
                "package org.broadleafcommerce.core.offer.service;"+
                "import org.broadleafcommerce.core.offer.domain.Offer;"+
                "import org.broadleafcommerce.core.order.domain.Order;"+
                "import org.broadleafcommerce.core.order.domain.OrderItem;"+
                "import org.broadleafcommerce.type.OfferType;"+
                "import java.util.List;"+
                "global List orderItems;"+
                "global List offerPackages;"+
                "rule \"Offer 1 Rule\" "+
                "salience 100"+
                "when "+
                "  orderItem : OrderItem(retailPrice &gt= 100)"+
                " then"+
                " System.err.println(\"applying offer 2\");"+
                " insert(offer);"+
        "end");

        allOffers.add(o);
        return new Object[][] {{allOffers}};

    }

}
