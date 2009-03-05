package org.broadleafcommerce.order.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("paymentInfoDao")
public class PaymentInfoDaoJpa implements PaymentInfoDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName="blPU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    @Override
    public PaymentInfo maintainPaymentInfo(PaymentInfo paymentInfo) {
        if (paymentInfo.getId() == null) {
            em.persist(paymentInfo);
        } else {
            paymentInfo = em.merge(paymentInfo);
        }
        return paymentInfo;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PaymentInfo readPaymentInfoById(Long paymentId) {
        return (PaymentInfo) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.order.domain.PaymentInfo"), paymentId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PaymentInfo> readPaymentInfosForOrder(Order order) {
        Query query = em.createNamedQuery("BC_READ_ORDERS_PAYMENTS_BY_ORDER_ID");
        query.setParameter("orderId", order.getId());
        return query.getResultList();
    }

    @Override
    public PaymentInfo create() {
        return ((PaymentInfo) entityConfiguration.createEntityInstance("paymentInfo"));
    }
}
