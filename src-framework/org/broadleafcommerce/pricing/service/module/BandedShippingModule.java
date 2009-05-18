package org.broadleafcommerce.pricing.service.module;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;

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

        System.out.println("*** in BandedShippingModule.calculateShippingForFG()");

        String shippingMethod = fulfillmentGroup.getMethod();
        Address address = fulfillmentGroup.getAddress();

        // TODO Replace these strings with row entries in table.
        // right now the only one in the table is standard.

        if ("truck".equalsIgnoreCase(shippingMethod)) {
            System.out.println("**** price: " + fulfillmentGroup.getPrice());
            fulfillmentGroup.setPrice(new Money(0));
            System.out.println("**** price: " + fulfillmentGroup.getPrice());

            return fulfillmentGroup;
        }

        if ("pickup".equalsIgnoreCase(shippingMethod)) {
            fulfillmentGroup.setPrice(new Money(0));

            return fulfillmentGroup;
        }

        if ("delivery".equalsIgnoreCase(shippingMethod)) {
            throw new UnsupportedOperationException();
        }

        if ("expedited".equalsIgnoreCase(shippingMethod)) {
            throw new UnsupportedOperationException();
        }

        if ("standard".equalsIgnoreCase(shippingMethod)) {
            //throw new UnsupportedOperationException();
            calculateShipping(fulfillmentGroup);
            return fulfillmentGroup;
        }

        System.out.println("*** address: " + address);

        fulfillmentGroup.setPrice(new Money(0D));

        return fulfillmentGroup;
    }

    private void calculateShipping(FulfillmentGroup fulfillmentGroup) {
        Address address = fulfillmentGroup.getAddress();
        String state = address.getState().getAbbreviation();
        BigDecimal retailTotal = new BigDecimal(0);
        String feeType = feeTypeMapping.get(fulfillmentGroup.getMethod());
        String feeSubType = ((feeSubTypeMapping.get(state) == null)? feeSubTypeMapping.get("ALL") : feeSubTypeMapping.get(state));

        for (FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup.getFulfillmentGroupItems()) {
            retailTotal = retailTotal.add(fulfillmentGroupItem.getRetailPrice().getAmount());
        }

        System.out.println("feeType: "+feeType+" feeSubType: "+feeSubType+" retailTotal: "+retailTotal);

        ShippingRate sr = shippingRateDao.readShippingRateByFeeTypesUnityQty(feeType, feeSubType, retailTotal);
        BigDecimal shippingPrice = new BigDecimal(0);
        if(sr.getBandResultPercent().compareTo(0) > 0) {
            BigDecimal percent = new BigDecimal(sr.getBandResultPercent()*100);
            shippingPrice = sr.getBandResultQuantity().add(sr.getBandResultQuantity().multiply(percent));
        }else {
            shippingPrice = sr.getBandResultQuantity();
        }
        fulfillmentGroup.setPrice(new Money(shippingPrice));

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
