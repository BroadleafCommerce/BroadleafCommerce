package org.broadleafcommerce.pricing.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.pricing.module.ShippingModule;
import org.broadleafcommerce.pricing.module.TaxModule;
import org.broadleafcommerce.util.money.Money;
import org.springframework.stereotype.Service;

@Service("pricingService")
public class PricingServiceImpl implements PricingService {

    private TaxModule taxModule;
    private ShippingModule shippingModule;

    @Override
    public Order calculateOrderTotal(Order order) {
        Money total = new Money(BigDecimal.ZERO);
        List<OrderItem> orderItemList = order.getOrderItems();
        for (OrderItem item : orderItemList) {
            if (item.getPrice() == null) {
                item.setPrice(item.getSalePrice());
            }
            total = total.add(item.getPrice());
        }
        order.setSubTotal(total);

        List<FulfillmentGroup> fulfillmentGroupList = order.getFulfillmentGroups();
        for (FulfillmentGroup fulfillmentGroup : fulfillmentGroupList) {
            if (fulfillmentGroup.getRetailPrice() != null) {
                total = total.add(fulfillmentGroup.getRetailPrice());
            }
        }
        order.setTotal(total);
        /*
         * TODO confirm this is the proper way to calculate the order total. This just
         * appears to be taking the order items and shipping total.
         */
        return order;
    }

    @Override
    public Order calculateShippingForOrder(Order order) {
        List<FulfillmentGroup> newGroups = new ArrayList<FulfillmentGroup>();
        List<FulfillmentGroup> groups = order.getFulfillmentGroups();
        Iterator<FulfillmentGroup> itr = groups.iterator();
        while(itr.hasNext()) {
            FulfillmentGroup group = itr.next();
            newGroups.add(shippingModule.calculateShippingForFulfillmentGroup(group));
        }
        order.setFulfillmentGroups(newGroups);

        return order;
    }

    @Override
    public Order calculateTaxForOrder(Order order) {
        return taxModule.calculateTaxForOrder(order);
    }

    public TaxModule getTaxModule() {
        return taxModule;
    }

    public void setTaxModule(TaxModule taxModule) {
        this.taxModule = taxModule;
    }

    public ShippingModule getShippingModule() {
        return shippingModule;
    }

    public void setShippingModule(ShippingModule shippingModule) {
        this.shippingModule = shippingModule;
    }

}
