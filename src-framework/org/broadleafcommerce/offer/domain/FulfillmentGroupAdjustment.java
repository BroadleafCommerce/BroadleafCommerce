package org.broadleafcommerce.offer.domain;

import org.broadleafcommerce.order.domain.FulfillmentGroup;

public interface FulfillmentGroupAdjustment extends Adjustment {

    public FulfillmentGroup getFulfillmentGroup();

    public void computeAdjustmentValue();

}
