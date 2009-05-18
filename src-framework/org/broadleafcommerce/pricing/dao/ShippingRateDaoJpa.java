package org.broadleafcommerce.pricing.dao;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.pricing.domain.ShippingRate;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("shippingRatesDao")
public class ShippingRateDaoJpa implements ShippingRateDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName = "blPU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    public Address save(Address address) {
        if (address.getId() == null) {
            em.persist(address);
        } else {
            address = em.merge(address);
        }
        return address;
    }

    @Override
    public ShippingRate save(ShippingRate shippingRate) {
        if(shippingRate.getId() == null) {
            em.persist(shippingRate);
        }else {
            shippingRate = em.merge(shippingRate);
        }
        return shippingRate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ShippingRate readShippingRateById(Long id) {
        return (ShippingRate) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.pricing.domain.ShippingRate"), id);
    }

    @Override
    public ShippingRate readShippingRateByFeeTypesUnityQty(String feeType, String feeSubType, BigDecimal unitQuantity) {
        Query query = em.createNamedQuery("READ_FIRST_SHIPPING_RATE_BY_FEE_TYPES");
        query.setParameter("feeType", feeType);
        query.setParameter("feeSubType", feeSubType);
        query.setParameter("bandUnitQuantity", unitQuantity);
        return(ShippingRate) query.getResultList().get(0);
    }



    /* @Override
    @SuppressWarnings("unchecked")
    public List<ShippingRate> readOrdersForCustomer(Long customerId) {
        Query query = em.createNamedQuery("BC_READ_ORDERS_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        return query.getResultList();
    } */

    /* @Override
    @SuppressWarnings("unchecked")
    public List<Order> readOrdersForCustomer(Customer customer, OrderStatus orderStatus) {
        if (orderStatus == null) {
            return readOrdersForCustomer(customer.getId());
        } else {
            Query query = em.createNamedQuery("BC_READ_ORDERS_BY_CUSTOMER_ID_AND_STATUS");
            query.setParameter("customerId", customer.getId());
            query.setParameter("orderStatus", orderStatus);
            return query.getResultList();
        }

    }

    @Override
    public Order readCartForCustomer(Customer customer, boolean persist) {
        Order order = null;
        Query query = em.createNamedQuery("BC_READ_ORDERS_BY_CUSTOMER_ID_AND_NAME_NULL");
        query.setParameter("customerId", customer.getId());
        query.setParameter("orderStatus", OrderStatus.IN_PROCESS);
        try {
            order = (Order) query.getSingleResult();
            return (Order) query.getSingleResult();
        } catch (NoResultException nre) {
            if (persist) {
                order = (Order) entityConfiguration.createEntityInstance("org.broadleafcommerce.order.domain.Order");
                if (customer.getUsername() == null) {
                    customer.setUsername(String.valueOf(customer.getId()));
                    em.persist(customer);
                }
                order.setCustomer(customer);
                order.setStatus(OrderStatus.IN_PROCESS);
                em.persist(order);
            }
            return order;
        }
    }

    @Override
    public Order readNamedOrderForCustomer(Customer customer, String name) {
        Query query = em.createNamedQuery("BC_READ_NAMED_ORDER_FOR_CUSTOMER");
        query.setParameter("customerId", customer.getId());
        query.setParameter("orderStatus", OrderStatus.NAMED);
        query.setParameter("orderName", name);
        return (Order) query.getSingleResult();
    } */
}
