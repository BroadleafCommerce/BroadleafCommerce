package org.broadleafcommerce.pricing.workflow;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.pricing.module.ShippingModule;
import org.broadleafcommerce.pricing.service.ShippingService;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class CalculateShipping extends BaseActivity {

    @Resource
    private ShippingService shippingService;

    private String shippingModuleName;

    public void setShippingModuleName(String shippingModuleName) {
        this.shippingModuleName = shippingModuleName;
    }

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        Order order = ((OfferContext)context).getSeedData();

        System.out.println("*** in CalculateShipping.execute()");
        System.out.println("*** order: " + order);

        ShippingModule shippingModule = shippingService.getShippingModuleByName(
                shippingModuleName);
        /*
         * 1. Get FGs from Order
         * 2. take each FG and call shipping module with the shipping svc
         * 3. add FG back to order
         */

        List<FulfillmentGroup> fulfillmentGroups = order.getFulfillmentGroups();
        Iterator<FulfillmentGroup> itr = fulfillmentGroups.iterator();

        while ( itr.hasNext() ) {
            FulfillmentGroup fulfillmentGroup = itr.next();
            System.out.println("**** FG b4 change: " + fulfillmentGroup.getMethod());
            //fulfillmentGroup.getMethod();
            fulfillmentGroup =
                shippingModule.calculateShippingForFulfillmentGroup(fulfillmentGroup);

            System.out.println("**** FG after change: " + fulfillmentGroup.getMethod());
            order.setFulfillmentGroups(fulfillmentGroups);
            System.out.println("**** order after change: " + order);
        }

        // TODO Add code to calculate shipping
        context.setSeedData(order);
        return context;
    }

}
