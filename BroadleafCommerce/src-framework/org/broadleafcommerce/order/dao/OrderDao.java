package org.broadleafcommerce.order.dao;

import java.util.List;

import org.broadleafcommerce.order.domain.BasketOrder;
import org.broadleafcommerce.order.domain.BroadleafOrder;
import org.broadleafcommerce.order.domain.SubmittedOrder;
import org.broadleafcommerce.profile.domain.Customer;

public interface OrderDao {

    public BroadleafOrder readOrderById(Long orderId);

    public BroadleafOrder maintianOrder(BroadleafOrder order);

    public List<BroadleafOrder> readOrdersForCustomer(Customer customer);

    public List<BroadleafOrder> readOrdersForCustomer(Long id);

    public void deleteOrderForCustomer(BroadleafOrder order);

    public BasketOrder readBasketOrderForCustomer(Customer customer);

    public SubmittedOrder submitOrder(BroadleafOrder basketOrder);
}
