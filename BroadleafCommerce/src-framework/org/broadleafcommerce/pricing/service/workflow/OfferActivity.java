package org.broadleafcommerce.pricing.service.workflow;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.service.OfferService;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class OfferActivity extends BaseActivity {

    @Resource
    private OfferService offerService;

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {

        Order order = ((PricingContext)context).getSeedData();
        /*
         * I commented this out -- there seems to be some problems in the control
         * flow of this call. Mike or Brian - maybe you can take a look.
         */
        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyOffersToOrder(offers, order);
        context.setSeedData(order);

        return context;
    }

}
