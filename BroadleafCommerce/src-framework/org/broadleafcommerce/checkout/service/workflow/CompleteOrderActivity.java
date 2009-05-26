package org.broadleafcommerce.checkout.service.workflow;

import javax.annotation.Resource;

import org.broadleafcommerce.order.service.CartService;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class CompleteOrderActivity extends BaseActivity {

    @Resource(name="cartService")
    private CartService cartService;

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        CheckoutSeed seed = ((CheckoutContext) context).getSeedData();
        seed.getOrder().setStatus(OrderStatus.SUBMITTED);
        cartService.createNewCartForCustomer(seed.getOrder().getCustomer());
        return context;
    }

}
