package org.broadleafcommerce.pricing.service.module;

import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.util.money.Money;

public class BandedShippingModule implements ShippingModule {

    public static final String MODULENAME = "bandedShippingModule";

    protected String name = MODULENAME;

    @Override
    // this will need to calculate shipping on each fulfilmentGroup in an order
    public FulfillmentGroup calculateShippingForFulfillmentGroup(
            FulfillmentGroup fulfillmentGroup) {

        System.out.println("*** in BandedShippingModule.calculateShippingForFG()");

        String shippingMethod = fulfillmentGroup.getMethod();
        Address address = fulfillmentGroup.getAddress();

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
            calculateStandardShipping(fulfillmentGroup);
        }

        System.out.println("*** address: " + address);

        fulfillmentGroup.setPrice(new Money(0D));

        return fulfillmentGroup;
    }

    private void calculateStandardShipping(FulfillmentGroup fulfillmentGroup) {
        Address address = fulfillmentGroup.getAddress();
        String state = address.getState().getAbbreviation();

        if (state.equalsIgnoreCase("alsk")) {
            // do alaska
        } else if (state.equalsIgnoreCase("hawi")) {
            // do hawaii
        } else {
            // standard state charges
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

}
