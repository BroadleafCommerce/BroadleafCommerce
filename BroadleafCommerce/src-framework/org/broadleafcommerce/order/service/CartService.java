package org.broadleafcommerce.order.service;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.call.MergeCartResponse;
import org.broadleafcommerce.order.service.call.ReconstructCartResponse;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.domain.Customer;

public interface CartService extends OrderService {

    public Order createNewCartForCustomer(Customer customer);

    public Order findCartForCustomer(Customer customer);

    public Order addAllItemsToCartFromNamedOrder(Order namedOrder) throws PricingException;

    public OrderItem moveItemToCartFromNamedOrder(Order order, OrderItem orderItem) throws PricingException;

    public OrderItem moveItemToCartFromNamedOrder(Long customerId, String orderName, Long orderItemId, Integer quantity) throws PricingException;

    public Order moveAllItemsToCartFromNamedOrder(Order namedOrder) throws PricingException;

    /**
     * Merge the anonymous cart with the customer's cart taking into
     * consideration sku activation
     * @param customer the customer whose cart is to be merged
     * @param anonymousCartId the anonymous cart id
     * @return the response containing the cart, any items added to the cart,
     *         and any items removed from the cart
     */
    public MergeCartResponse mergeCart(Customer customer, Long anonymousCartId) throws PricingException;

    /**
     * Reconstruct the cart using previous stored state taking into
     * consideration sku activation
     * @param customer the customer whose cart is to be reconstructed
     * @return the response containing the cart and any items removed from the
     *         cart
     */
    public ReconstructCartResponse reconstructCart(Customer customer) throws PricingException;

}
