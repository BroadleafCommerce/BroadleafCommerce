/*
 * #%L
 * BroadleafCommerce Integration
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
package org.broadleafcommerce.core.order.dao;

import org.broadleafcommerce.core.catalog.dao.SkuDao;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.order.FulfillmentGroupDataProvider;
import org.broadleafcommerce.core.order.OrderItemDataProvider;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupItemRequest;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.core.dao.CustomerAddressDao;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.test.TestNGSiteIntegrationSetup;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.util.List;

import javax.annotation.Resource;

public class FulfillmentGroupItemDaoTest extends TestNGSiteIntegrationSetup {

    private FulfillmentGroup fulfillmentGroup;
    private Order salesOrder;
    private Long fulfillmentGroupItemId;

    @Resource
    private FulfillmentGroupItemDao fulfillmentGroupItemDao;

    @Resource
    private CustomerService customerService;

    @Resource
    private OrderDao orderDao;
    
    @Resource
    private SkuDao skuDao;
    
    @Resource
    private OrderItemDao orderItemDao;
    
    @Resource
    private CustomerAddressDao customerAddressDao;
    
    @Resource
    private FulfillmentGroupDao fulfillmentGroupDao;
    
    @Resource
    private FulfillmentGroupService fulfillmentGroupService;

    @Test(groups = "createItemFulfillmentGroup", dataProvider = "basicFulfillmentGroup", dataProviderClass = FulfillmentGroupDataProvider.class, dependsOnGroups = { "createOrder", "createCustomerAddress" })
    @Rollback(false)
    @Transactional
    public void createDefaultFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Address address = (customerAddressDao.readActiveCustomerAddressesByCustomerId(customer.getId())).get(0).getAddress();
        salesOrder = orderDao.createNewCartForCustomer(customer);

        FulfillmentGroup newFG = fulfillmentGroupDao.createDefault();
        newFG.setAddress(address);
        newFG.setRetailShippingPrice(fulfillmentGroup.getRetailShippingPrice());
        newFG.setMethod(fulfillmentGroup.getMethod());
        newFG.setService(fulfillmentGroup.getService());
        newFG.setOrder(salesOrder);
        newFG.setReferenceNumber(fulfillmentGroup.getReferenceNumber());

        assert newFG.getId() == null;
        this.fulfillmentGroup = fulfillmentGroupService.save(newFG);
        assert this.fulfillmentGroup.getId() != null;
    }
    
    @Test(groups = { "createFulfillmentGroupItem" }, dataProvider = "basicDiscreteOrderItem", dataProviderClass = OrderItemDataProvider.class, dependsOnGroups = { "createOrder", "createSku", "createItemFulfillmentGroup" })
    @Rollback(false)
    @Transactional
    public void createFulfillmentGroupItem(DiscreteOrderItem orderItem) throws PricingException {        
        Sku si = skuDao.readFirstSku();
        orderItem.setSku(si);
        orderItem = (DiscreteOrderItem) orderItemDao.save(orderItem);
        orderItem.setOrder(salesOrder);
        salesOrder.addOrderItem(orderItem);
        orderDao.save(salesOrder);

        FulfillmentGroupItemRequest fulfillmentGroupItemRequest = new FulfillmentGroupItemRequest();
        fulfillmentGroupItemRequest.setOrderItem(orderItem);
        fulfillmentGroupItemRequest.setFulfillmentGroup(fulfillmentGroup);
        fulfillmentGroupService.addItemToFulfillmentGroup(fulfillmentGroupItemRequest, true);
        
        FulfillmentGroupItem fgi = fulfillmentGroup.getFulfillmentGroupItems().get(fulfillmentGroup.getFulfillmentGroupItems().size()-1);
        assert fgi.getId() != null;
        fulfillmentGroupItemId = fgi.getId();
    }

    @Test(groups = { "readFulfillmentGroupItemsForFulfillmentGroup" }, dependsOnGroups = { "createFulfillmentGroupItem" })
    @Transactional
    public void readFulfillmentGroupItemsForFulfillmentGroup() {
        List<FulfillmentGroupItem> fgis = fulfillmentGroupItemDao.readFulfillmentGroupItemsForFulfillmentGroup(fulfillmentGroup);
        assert fgis != null;
        assert fgis.size() > 0;
    }

    @Test(groups = { "readFulfillmentGroupItemsById" }, dependsOnGroups = { "createFulfillmentGroupItem" })
    @Transactional
    public void readFulfillmentGroupItemsById() {
        FulfillmentGroupItem fgi = fulfillmentGroupItemDao.readFulfillmentGroupItemById(fulfillmentGroupItemId);
        assert fgi != null;
        assert fgi.getId() != null;
    }
}
