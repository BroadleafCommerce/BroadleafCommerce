/*-
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.offer.service;

import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.common.i18n.domain.ISOCountryImpl;
import org.broadleafcommerce.common.i18n.service.ISOService;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItemImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.CountryImpl;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.core.domain.StateImpl;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.StateService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Chad Harchar (charchar)
 */
public class CreateOrderEntityUtility {

    private CountryService countryService;
    private StateService stateService;
    private ISOService isoService;
    private CatalogService catalogService;
    private OrderItemService orderItemService;

    public CreateOrderEntityUtility(CatalogService catalogService, OrderItemService orderItemService,
                              ISOService isoService, StateService stateService, CountryService countryService) {
        this.catalogService = catalogService;
        this.orderItemService = orderItemService;
        this.isoService = isoService;
        this.stateService = stateService;
        this.countryService = countryService;
    }


    public List<FulfillmentGroup> createFulfillmentGroups(FulfillmentOption option, Double shippingPrice, Order order) {
        List<FulfillmentGroup> groups = new ArrayList<FulfillmentGroup>();
        FulfillmentGroup group = createFulfillmentGroup1(option, shippingPrice, order);

        groups.add(group);

        for (OrderItem orderItem : order.getOrderItems()) {
            FulfillmentGroupItem fgItem = new FulfillmentGroupItemImpl();
            fgItem.setFulfillmentGroup(group);
            fgItem.setOrderItem(orderItem);
            fgItem.setQuantity(orderItem.getQuantity());
            group.addFulfillmentGroupItem(fgItem);
        }

        return groups;
    }

    public FulfillmentGroup createFulfillmentGroup1(FulfillmentOption option, Double shippingPrice, Order order) {
        FulfillmentGroup group = new FulfillmentGroupImpl();
        group.setFulfillmentOption(option);
        group.setRetailShippingPrice(new Money(shippingPrice));
        group.setOrder(order);

        Address address = new AddressImpl();
        address.setAddressLine1("123 Test Rd");
        address.setCity("Dallas");
        address.setFirstName("Jeff");
        address.setLastName("Fischer");
        address.setPostalCode("75240");
        address.setPrimaryPhone("972-978-9067");

        Country country = new CountryImpl();
        country.setAbbreviation("US");
        country.setName("United States");
        countryService.save(country);

        ISOCountry isoCountry = new ISOCountryImpl();
        isoCountry.setAlpha2("US");
        isoCountry.setName("UNITED STATES");

        isoService.save(isoCountry);

        State state = new StateImpl();
        state.setAbbreviation("TX");
        state.setName("Texas");
        state.setCountry(country);

        stateService.save(state);

        address.setState(state);
        address.setCountry(country);
        address.setIsoCountrySubdivision("US-TX");
        address.setIsoCountryAlpha2(isoCountry);

        group.setAddress(address);
        return group;
    }

    public FulfillmentGroup createFulfillmentGroup2(FulfillmentOption option, Double shippingPrice, Order order) {
        FulfillmentGroup group = new FulfillmentGroupImpl();
        group.setFulfillmentOption(option);
        group.setRetailShippingPrice(new Money(shippingPrice));
        group.setOrder(order);

        Address address = new AddressImpl();
        address.setAddressLine1("ABC Test Rd");
        address.setCity("Dallas");
        address.setFirstName("Joe");
        address.setLastName("Foster");
        address.setPostalCode("75240");
        address.setPrimaryPhone("972-978-9067");

        Country country = new CountryImpl();
        country.setAbbreviation("MX");
        country.setName("Mexico");
        countryService.save(country);

        ISOCountry isoCountry = new ISOCountryImpl();
        isoCountry.setAlpha2("MX");
        isoCountry.setName("MEXICO");

        isoService.save(isoCountry);

        State state = new StateImpl();
        state.setAbbreviation("MXC");
        state.setName("Mexico City");
        state.setCountry(country);

        stateService.save(state);

        address.setState(state);
        address.setCountry(country);
        address.setIsoCountrySubdivision("MX-MXC");
        address.setIsoCountryAlpha2(isoCountry);

        group.setAddress(address);
        return group;
    }

    public DiscreteOrderItem createDiscreteOrderItem(Long skuId, Double retailPrice, Double salePrice, boolean isDiscountable, int quantity, Order order) {
        DiscreteOrderItem item = new DiscreteOrderItemImpl();
        Sku sku = catalogService.findSkuById(skuId);
        sku.setRetailPrice(new Money(retailPrice));
        if (salePrice != null) {
            sku.setSalePrice(new Money(salePrice));
        } else {
            sku.setSalePrice(null);
        }
        sku.setDiscountable(isDiscountable);
        sku.setName("test");
        sku.setActiveStartDate(new Date());
        sku = catalogService.saveSku(sku);

        item.setSku(sku);
        item.setQuantity(quantity);
        Product product = new ProductImpl();
        product.setDefaultSku(sku);

        product = catalogService.saveProduct(product);

        item.setProduct(product);

        item.setOrder(order);

        item = (DiscreteOrderItem) orderItemService.saveOrderItem(item);

        return item;
    }
}
