package org.broadleafcommerce.pricing.service.module;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.NotImplementedException;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.pricing.dao.ShippingRateDao;
import org.broadleafcommerce.pricing.domain.ShippingRate;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.util.money.Money;

public class BandedShippingModule implements ShippingModule {

    public static final String MODULENAME = "bandedShippingModule";

    protected String name = MODULENAME;

    @Resource
    private ShippingRateDao shippingRateDao;

    private Map<String, String> feeTypeMapping;

    private Map<String, String> feeSubTypeMapping;

    @Override
    // this will need to calculate shipping on each fulfilmentGroup in an order
    public FulfillmentGroup calculateShippingForFulfillmentGroup(
            FulfillmentGroup fulfillmentGroup) {

        calculateShipping(fulfillmentGroup);
        return fulfillmentGroup;


    }

    private void calculateShipping(FulfillmentGroup fulfillmentGroup) {
        Address address = fulfillmentGroup.getAddress();
        String state = address.getState().getAbbreviation();
        BigDecimal retailTotal = new BigDecimal(0);
        String feeType = feeTypeMapping.get(fulfillmentGroup.getMethod());
        String feeSubType = ((feeSubTypeMapping.get(state) == null)? feeSubTypeMapping.get("ALL") : feeSubTypeMapping.get(state));

        for (FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup.getFulfillmentGroupItems()) {
            BigDecimal price = (fulfillmentGroupItem.getRetailPrice() != null)? fulfillmentGroupItem.getRetailPrice().getAmount().multiply(BigDecimal.valueOf(fulfillmentGroupItem.getQuantity())):null;
            if(price == null) {
                price = fulfillmentGroupItem.getOrderItem().getRetailPrice().getAmount().multiply(BigDecimal.valueOf(fulfillmentGroupItem.getQuantity()));
            }
            retailTotal = retailTotal.add(price);
        }

        ShippingRate sr = shippingRateDao.readShippingRateByFeeTypesUnityQty(feeType, feeSubType, retailTotal);
        if(sr == null) {
            throw new NotImplementedException("Shipping rate "+fulfillmentGroup.getMethod()+" not supported");
        }
        BigDecimal shippingPrice = new BigDecimal(0);
        if(sr.getBandResultPercent().compareTo(0) > 0) {
            BigDecimal percent = new BigDecimal(sr.getBandResultPercent()/100);
            shippingPrice = retailTotal.multiply(percent);
        }else {
            shippingPrice = sr.getBandResultQuantity();
        }
        fulfillmentGroup.setShippingPrice(new Money(shippingPrice));

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public ShippingRateDao getShippingRateDao() {
        return shippingRateDao;
    }

    public void setShippingRateDao(ShippingRateDao shippingRateDao) {
        this.shippingRateDao = shippingRateDao;
    }

    public Map<String, String> getFeeTypeMapping() {
        return feeTypeMapping;
    }

    public void setFeeTypeMapping(Map<String, String> feeTypeMapping) {
        this.feeTypeMapping = feeTypeMapping;
    }

    public Map<String, String> getFeeSubTypeMapping() {
        return feeSubTypeMapping;
    }

    public void setFeeSubTypeMapping(Map<String, String> feeSubTypeMapping) {
        this.feeSubTypeMapping = feeSubTypeMapping;
    }



}
