/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.pricing.service.workflow;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.rule.MvelHelper;
import org.broadleafcommerce.common.util.EfficientLRUMap;
import org.broadleafcommerce.core.catalog.domain.SkuFee;
import org.broadleafcommerce.core.catalog.service.type.SkuFeeType;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupFee;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * 
 * @author Phillip Verheyden
 */
@Component("blConsolidateFulfillmentFeesActivity")
public class ConsolidateFulfillmentFeesActivity extends BaseActivity<ProcessContext<Order>> {
    
    public static final int ORDER = 2000;
    
    @SuppressWarnings("unchecked")
    protected static final Map EXPRESSION_CACHE = new EfficientLRUMap(1000);
    
    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;
    
    public ConsolidateFulfillmentFeesActivity() {
        setOrder(ORDER);
    }

    @Override
    public ProcessContext<Order> execute(ProcessContext<Order> context) throws Exception {
        Order order = context.getSeedData();
        
        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            //create and associate all the Fulfillment Fees
            for (FulfillmentGroupItem item : fulfillmentGroup.getFulfillmentGroupItems()) {
                List<SkuFee> fees = null;
                if (item.getOrderItem() instanceof BundleOrderItem) {
                    fees = ((BundleOrderItem)item.getOrderItem()).getSku().getFees();
                } else if (item.getOrderItem() instanceof DiscreteOrderItem) {
                    fees = ((DiscreteOrderItem)item.getOrderItem()).getSku().getFees();
                }
                
                if (fees != null) {
                    for (SkuFee fee : fees) {
                        if (SkuFeeType.FULFILLMENT.equals(fee.getFeeType())) {
                            if (shouldApplyFeeToFulfillmentGroup(fee, fulfillmentGroup)) {
                                FulfillmentGroupFee fulfillmentFee = fulfillmentGroupService.createFulfillmentGroupFee();
                                fulfillmentFee.setName(fee.getName());
                                fulfillmentFee.setTaxable(fee.getTaxable());
                                fulfillmentFee.setAmount(fee.getAmount());
                                fulfillmentFee.setFulfillmentGroup(fulfillmentGroup);
                                
                                fulfillmentGroup.addFulfillmentGroupFee(fulfillmentFee);
                            }
                        }
                    }
                }
            }
            
            if (fulfillmentGroup.getFulfillmentGroupFees().size() > 0) {
                fulfillmentGroup = fulfillmentGroupService.save(fulfillmentGroup);
            }
        }
        
        context.setSeedData(order);
        return context;
    }

    /**
     * If the SkuFee expression is null or empty, this method will always return true
     * 
     * @param fee
     * @param fulfillmentGroup
     * @return
     */
    protected boolean shouldApplyFeeToFulfillmentGroup(SkuFee fee, FulfillmentGroup fulfillmentGroup) {
        boolean appliesToFulfillmentGroup = true;
        String feeExpression = fee.getExpression();
        
        if (StringUtils.isNotEmpty(feeExpression)) {
            synchronized (EXPRESSION_CACHE) {
                HashMap<String, Object> vars = new HashMap<>();
                vars.put("fulfillmentGroup", fulfillmentGroup);
                MvelHelper.evaluateRule(feeExpression, vars, EXPRESSION_CACHE);
            }
        }
        
        return appliesToFulfillmentGroup;
    }

}
