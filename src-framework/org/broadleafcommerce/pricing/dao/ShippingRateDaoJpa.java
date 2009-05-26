package org.broadleafcommerce.pricing.dao;

import java.math.BigDecimal;
import java.util.List;

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

    @SuppressWarnings("unchecked")
    @Override
    public ShippingRate readShippingRateByFeeTypesUnityQty(String feeType, String feeSubType, BigDecimal unitQuantity) {
        Query query = em.createNamedQuery("READ_FIRST_SHIPPING_RATE_BY_FEE_TYPES");
        query.setParameter("feeType", feeType);
        query.setParameter("feeSubType", feeSubType);
        query.setParameter("bandUnitQuantity", unitQuantity);
        List<ShippingRate> returnedRates = query.getResultList();
        if(returnedRates.size() > 0) {
            return returnedRates.get(0);
        }else {
            return null;
        }
    }
}
