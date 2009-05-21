package org.broadleafcommerce.order.dao;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.OrderItemImpl;
import org.broadleafcommerce.order.service.type.OrderItemType;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("orderItemDao")
public class OrderItemDaoJpa implements OrderItemDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName="blPU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    @Override
    public OrderItem save(OrderItem orderItem) {
        if (orderItem.getId() == null) {
            em.persist(orderItem);
        } else {
            orderItem = em.merge(orderItem);
        }
        return orderItem;
    }

    @Override
    public OrderItem readOrderItemById(Long orderItemId) {
        return em.find(OrderItemImpl.class, orderItemId);
    }

    @Override
    public void delete(OrderItem orderItem) {
        if (GiftWrapOrderItem.class.isAssignableFrom(orderItem.getClass())) {
            GiftWrapOrderItem giftItem = (GiftWrapOrderItem) orderItem;
            for (OrderItem wrappedItem : giftItem.getWrappedItems()) {
                wrappedItem.setGiftWrapOrderItem(null);
                save(wrappedItem);
            }
        }
        em.remove(orderItem);
    }

    public OrderItem create(OrderItemType orderItemType) {
        return (OrderItem) entityConfiguration.createEntityInstance(orderItemType.getClassName());
    }

    //TODO why do we have a cloneOrderItem method - it's not called
    /*public OrderItem cloneOrderItem(OrderItem orderItem, OrderItemType orderItemType) {
        switch(orderItemType) {
        case DISCRETE:
            return cloneDiscreteOrderItem((DiscreteOrderItem) orderItem);
        case BUNDLE:
            return cloneBundleOrderItem((BundleOrderItem) orderItem);
        default:
            return cloneGiftWrapOrderItem((GiftWrapOrderItem) orderItem);
        }
    }

    protected GiftWrapOrderItem cloneGiftWrapOrderItem(GiftWrapOrderItem orderItem) {
        GiftWrapOrderItem newItem = (GiftWrapOrderItem) create(OrderItemType.GIFTWRAP);
        newItem.setCategory(orderItem.getCategory());
        newItem.setPersonalMessage(orderItem.getPersonalMessage());
        newItem.setProduct(orderItem.getProduct());
        newItem.setQuantity(orderItem.getQuantity());
        newItem.setSku(orderItem.getSku());
        newItem.setPrice(orderItem.getPrice());
        newItem.getWrappedItems().addAll(orderItem.getWrappedItems());

        return newItem;
    }

    protected DiscreteOrderItem cloneDiscreteOrderItem(DiscreteOrderItem orderItem) {
        DiscreteOrderItem newItem = (DiscreteOrderItem) create(OrderItemType.DISCRETE);
        newItem.setCategory(orderItem.getCategory());
        newItem.setPersonalMessage(orderItem.getPersonalMessage());
        newItem.setProduct(orderItem.getProduct());
        newItem.setQuantity(orderItem.getQuantity());
        newItem.setSku(orderItem.getSku());
        newItem.setPrice(orderItem.getPrice());

        return newItem;
    }

    protected BundleOrderItem cloneBundleOrderItem(BundleOrderItem orderItem) {
        BundleOrderItem newItem = (BundleOrderItem) create(OrderItemType.BUNDLE);
        newItem.setCategory(orderItem.getCategory());
        newItem.setName(orderItem.getName());
        newItem.setPersonalMessage(orderItem.getPersonalMessage());
        newItem.setQuantity(orderItem.getQuantity());
        List<DiscreteOrderItem> discreteItems = orderItem.getDiscreteOrderItems();
        ArrayList<DiscreteOrderItem> newDiscreteOrders = new ArrayList<DiscreteOrderItem>();
        for (DiscreteOrderItem discreteItem : discreteItems) {
            newDiscreteOrders.add(cloneDiscreteOrderItem(discreteItem));
        }
        newItem.setDiscreteOrderItems(newDiscreteOrders);
        newItem.setPrice(orderItem.getPrice());

        return newItem;
    }*/
}
