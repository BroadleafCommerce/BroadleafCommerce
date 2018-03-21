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
package org.broadleafcommerce.core.order.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuBundleItem;
import org.broadleafcommerce.core.catalog.domain.pricing.SkuPriceWrapper;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuPrices;
import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuPricingService;
import org.broadleafcommerce.core.order.dao.OrderItemDao;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemFeePrice;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemAttribute;
import org.broadleafcommerce.core.order.domain.OrderItemAttributeImpl;
import org.broadleafcommerce.core.order.domain.PersonalMessage;
import org.broadleafcommerce.core.order.service.call.AbstractOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.BundleOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.ConfigurableOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.DiscreteOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.GiftWrapOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.NonDiscreteOrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.call.OrderItemRequest;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.call.ProductBundleOrderItemRequest;
import org.broadleafcommerce.core.order.service.extension.OrderItemServiceExtensionManager;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

@Service("blOrderItemService")
public class OrderItemServiceImpl implements OrderItemService {

    @Resource(name="blOrderItemDao")
    protected OrderItemDao orderItemDao;

    @Resource(name="blDynamicSkuPricingService" )
    protected DynamicSkuPricingService dynamicSkuPricingService;

    @Resource(name="blOrderItemServiceExtensionManager")
    protected OrderItemServiceExtensionManager extensionManager;

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Override
    public OrderItem readOrderItemById(final Long orderItemId) {
        return orderItemDao.readOrderItemById(orderItemId);
    }

    @Override
    public OrderItem saveOrderItem(final OrderItem orderItem) {
        return orderItemDao.saveOrderItem(orderItem);
    }

    @Override
    public void delete(final OrderItem item) {
        orderItemDao.delete(item);
    }

    @Override
    public PersonalMessage createPersonalMessage() {
        return orderItemDao.createPersonalMessage();
    }

    protected void populateDiscreteOrderItem(DiscreteOrderItem item, AbstractOrderItemRequest itemRequest) {
        item.setSku(itemRequest.getSku());
        item.setQuantity(itemRequest.getQuantity());
        item.setCategory(itemRequest.getCategory());
        item.setProduct(itemRequest.getProduct());
        item.setOrder(itemRequest.getOrder());
        Map<String, String> attributes = itemRequest.getItemAttributes();
        populateProductOptionAttributes(item, attributes);
        applyAdditionalOrderItemProperties(item);
    }

    protected void populateProductOptionAttributes(OrderItem item, Map<String, String> attributes) {
        if (attributes != null && attributes.size() > 0) {
            Map<String, OrderItemAttribute> orderItemAttributes = item.getOrderItemAttributes();
            if (item.getOrderItemAttributes() == null) {
                orderItemAttributes = new HashMap<String, OrderItemAttribute>();
                item.setOrderItemAttributes(orderItemAttributes);
            }
            for (String key : attributes.keySet()) {
                String value = attributes.get(key);
                OrderItemAttribute attribute = new OrderItemAttributeImpl();
                attribute.setName(key);
                attribute.setValue(value);
                attribute.setOrderItem(item);
                orderItemAttributes.put(key, attribute);
            }
        }
    }

    @Override
    public OrderItem createOrderItem(final OrderItemRequest itemRequest) {
        final OrderItem item = orderItemDao.create(OrderItemType.BASIC);
        item.setName(itemRequest.getItemName());
        item.setQuantity(itemRequest.getQuantity());
        item.setOrder(itemRequest.getOrder());

        if (itemRequest.getSalePriceOverride() != null) {
            item.setSalePriceOverride(Boolean.TRUE);
            item.setSalePrice(itemRequest.getSalePriceOverride());
        }

        if (itemRequest.getRetailPriceOverride() != null) {
            item.setRetailPriceOverride(Boolean.TRUE);
            item.setRetailPrice(itemRequest.getRetailPriceOverride());
        }

        if (MapUtils.isNotEmpty(itemRequest.getItemAttributes())) {
            Map<String, OrderItemAttribute> attributeMap = item.getOrderItemAttributes();
            if (attributeMap == null) {
                attributeMap = new HashMap<String, OrderItemAttribute>();
                item.setOrderItemAttributes(attributeMap);
            }

            for (Entry<String, String> entry : itemRequest.getItemAttributes().entrySet()) {
                OrderItemAttribute orderItemAttribute = new OrderItemAttributeImpl();

                orderItemAttribute.setName(entry.getKey());
                orderItemAttribute.setValue(entry.getValue());
                orderItemAttribute.setOrderItem(item);

                attributeMap.put(entry.getKey(), orderItemAttribute);
            }
        }

        applyAdditionalOrderItemProperties(item);

        return item;
    }

    @Override
    public OrderItem updateDiscreteOrderItem(OrderItem item, final DiscreteOrderItemRequest itemRequest) {
        List<ProductOption> productOptions = null;
        if (item instanceof DiscreteOrderItem) {
            productOptions = ((DiscreteOrderItem) item).getProduct().getProductOptions();
        } else if (item instanceof BundleOrderItem) {
            productOptions = ((BundleOrderItem) item).getProduct().getProductOptions();
        }
        List<String> removeKeys = new ArrayList<String>();
        if (productOptions != null && itemRequest.getItemAttributes() != null) {
            for (String name : itemRequest.getItemAttributes().keySet()) {
                //we do not let them update all product options.
                //Only allow them to update those options that can have validation to take place at later time
                //if  option.getProductOptionValidationType()  is null then it might change the sku, so we dont allow those
                for (ProductOption option : productOptions) {
                    if (option.getAttributeName().equals(name) && option.getProductOptionValidationStrategyType() == null) {

                        removeKeys.add(name);
                        break;
                    }
                }
            }
        }
        for (String name : removeKeys) {
            itemRequest.getItemAttributes().remove(name);
        }
        populateProductOptionAttributes(item, itemRequest.getItemAttributes());
        applyAdditionalOrderItemProperties(item);
        return item;
    }

    @Override
    public DiscreteOrderItem createDiscreteOrderItem(final DiscreteOrderItemRequest itemRequest) {
        final DiscreteOrderItem item = (DiscreteOrderItem) orderItemDao.create(OrderItemType.DISCRETE);
        populateDiscreteOrderItem(item, itemRequest);

        item.setBundleOrderItem(itemRequest.getBundleOrderItem());
        if (itemRequest.getSalePriceOverride() != null) {
            item.setBaseSalePrice(itemRequest.getSalePriceOverride());
        }
        item.setDiscreteOrderItemFeePrices(itemRequest.getDiscreteOrderItemFeePrices());

        if (itemRequest.getSalePriceOverride() != null) {
            item.setSalePriceOverride(Boolean.TRUE);
            item.setSalePrice(itemRequest.getSalePriceOverride());
            item.setBaseSalePrice(itemRequest.getSalePriceOverride());
        }

        if (itemRequest.getRetailPriceOverride() != null) {
            item.setRetailPriceOverride(Boolean.TRUE);
            item.setRetailPrice(itemRequest.getRetailPriceOverride());
            item.setBaseRetailPrice(itemRequest.getRetailPriceOverride());
        }

        for (DiscreteOrderItemFeePrice feePrice : item.getDiscreteOrderItemFeePrices()) {
            feePrice.setDiscreteOrderItem(item);
        }

        if (MapUtils.isNotEmpty(itemRequest.getAdditionalAttributes())) {
            item.setAdditionalAttributes(itemRequest.getAdditionalAttributes());
        }

        item.setPersonalMessage(itemRequest.getPersonalMessage());

        applyAdditionalOrderItemProperties(item);

        return item;
    }

    public DiscreteOrderItem createDiscreteOrderItem(final AbstractOrderItemRequest itemRequest) {
        final DiscreteOrderItem item = (DiscreteOrderItem) orderItemDao.create(OrderItemType.DISCRETE);
        populateDiscreteOrderItem(item, itemRequest);
        item.setBaseSalePrice(itemRequest.getSku().getSalePrice());
        item.setBaseRetailPrice(itemRequest.getSku().getRetailPrice());
        // item.updatePrices();
        item.updateSaleAndRetailPrices();

        item.assignFinalPrice();
        item.setPersonalMessage(itemRequest.getPersonalMessage());

        applyAdditionalOrderItemProperties(item);

        return item;
    }

    @Override
    public DiscreteOrderItem createDynamicPriceDiscreteOrderItem(final DiscreteOrderItemRequest itemRequest, @SuppressWarnings("rawtypes") HashMap skuPricingConsiderations) {
        final DiscreteOrderItem item = (DiscreteOrderItem) orderItemDao.create(OrderItemType.EXTERNALLY_PRICED);
        populateDiscreteOrderItem(item, itemRequest);

        SkuPriceWrapper wrapper = new SkuPriceWrapper(itemRequest.getSku());
        DynamicSkuPrices prices = dynamicSkuPricingService.getSkuPrices(wrapper, skuPricingConsiderations);
        item.setBundleOrderItem(itemRequest.getBundleOrderItem());
        item.setBaseRetailPrice(prices.getRetailPrice());
        item.setBaseSalePrice(prices.getSalePrice());
        item.setSalePrice(prices.getSalePrice());
        item.setRetailPrice(prices.getRetailPrice());

        if (itemRequest.getSalePriceOverride() != null) {
            item.setSalePriceOverride(Boolean.TRUE);
            item.setSalePrice(itemRequest.getSalePriceOverride());
            item.setBaseSalePrice(itemRequest.getSalePriceOverride());
        }

        if (itemRequest.getRetailPriceOverride() != null) {
            item.setRetailPriceOverride(Boolean.TRUE);
            item.setRetailPrice(itemRequest.getRetailPriceOverride());
            item.setBaseRetailPrice(itemRequest.getRetailPriceOverride());
        }

        item.setDiscreteOrderItemFeePrices(itemRequest.getDiscreteOrderItemFeePrices());
        for (DiscreteOrderItemFeePrice fee : itemRequest.getDiscreteOrderItemFeePrices()) {
            item.setSalePrice(item.getSalePrice().add(fee.getAmount()));
            item.setRetailPrice(item.getRetailPrice().add(fee.getAmount()));
        }

        item.setPersonalMessage(itemRequest.getPersonalMessage());

        applyAdditionalOrderItemProperties(item);

        return item;
    }

    @Override
    public GiftWrapOrderItem createGiftWrapOrderItem(final GiftWrapOrderItemRequest itemRequest) {
        final GiftWrapOrderItem item = (GiftWrapOrderItem) orderItemDao.create(OrderItemType.GIFTWRAP);
        item.setSku(itemRequest.getSku());
        item.setOrder(itemRequest.getOrder());
        item.setBundleOrderItem(itemRequest.getBundleOrderItem());
        item.setQuantity(itemRequest.getQuantity());
        item.setCategory(itemRequest.getCategory());
        item.setProduct(itemRequest.getProduct());
        item.setBaseSalePrice(itemRequest.getSku().getSalePrice());
        item.setBaseRetailPrice(itemRequest.getSku().getRetailPrice());
        item.setDiscreteOrderItemFeePrices(itemRequest.getDiscreteOrderItemFeePrices());

        if (itemRequest.getSalePriceOverride() != null) {
            item.setSalePriceOverride(Boolean.TRUE);
            item.setSalePrice(itemRequest.getSalePriceOverride());
            item.setBaseSalePrice(itemRequest.getSalePriceOverride());
        }

        if (itemRequest.getRetailPriceOverride() != null) {
            item.setRetailPriceOverride(Boolean.TRUE);
            item.setRetailPrice(itemRequest.getRetailPriceOverride());
            item.setBaseRetailPrice(itemRequest.getRetailPriceOverride());
        }

        //item.updatePrices();
        item.updateSaleAndRetailPrices();
        item.assignFinalPrice();
        item.getWrappedItems().addAll(itemRequest.getWrappedItems());
        for (OrderItem orderItem : item.getWrappedItems()) {
            orderItem.setGiftWrapOrderItem(item);
        }

        applyAdditionalOrderItemProperties(item);

        return item;
    }

    @Override
    public BundleOrderItem createBundleOrderItem(final BundleOrderItemRequest itemRequest) {
        final BundleOrderItem item = (BundleOrderItem) orderItemDao.create(OrderItemType.BUNDLE);
        item.setQuantity(itemRequest.getQuantity());
        item.setCategory(itemRequest.getCategory());
        item.setName(itemRequest.getName());
        item.setBundleOrderItemFeePrices(itemRequest.getBundleOrderItemFeePrices());
        item.setOrder(itemRequest.getOrder());

        if (itemRequest.getSalePriceOverride() != null) {
            item.setSalePriceOverride(Boolean.TRUE);
            item.setSalePrice(itemRequest.getSalePriceOverride());
            item.setBaseSalePrice(itemRequest.getSalePriceOverride());
        }

        if (itemRequest.getRetailPriceOverride() != null) {
            item.setRetailPriceOverride(Boolean.TRUE);
            item.setRetailPrice(itemRequest.getRetailPriceOverride());
            item.setBaseRetailPrice(itemRequest.getRetailPriceOverride());
        }

        for (DiscreteOrderItemRequest discreteItemRequest : itemRequest.getDiscreteOrderItems()) {
            discreteItemRequest.setBundleOrderItem(item);
            DiscreteOrderItem discreteOrderItem;
            if (discreteItemRequest instanceof GiftWrapOrderItemRequest) {
                discreteOrderItem = createGiftWrapOrderItem((GiftWrapOrderItemRequest) discreteItemRequest);
            } else {
                discreteOrderItem = createDiscreteOrderItem(discreteItemRequest);
            }
            item.getDiscreteOrderItems().add(discreteOrderItem);
        }

        applyAdditionalOrderItemProperties(item);

        return item;
    }

    @Override
    public BundleOrderItem createBundleOrderItem(final ProductBundleOrderItemRequest itemRequest, boolean saveItem) {
        ProductBundle productBundle = itemRequest.getProductBundle();
        BundleOrderItem bundleOrderItem = (BundleOrderItem) orderItemDao.create(OrderItemType.BUNDLE);
        bundleOrderItem.setQuantity(itemRequest.getQuantity());
        bundleOrderItem.setCategory(itemRequest.getCategory());
        bundleOrderItem.setSku(itemRequest.getSku());
        bundleOrderItem.setName(itemRequest.getName());
        bundleOrderItem.setProductBundle(productBundle);
        bundleOrderItem.setOrder(itemRequest.getOrder());

        if (itemRequest.getSalePriceOverride() != null) {
            bundleOrderItem.setSalePriceOverride(Boolean.TRUE);
            bundleOrderItem.setSalePrice(itemRequest.getSalePriceOverride());
            bundleOrderItem.setBaseSalePrice(itemRequest.getSalePriceOverride());
        }

        if (itemRequest.getRetailPriceOverride() != null) {
            bundleOrderItem.setRetailPriceOverride(Boolean.TRUE);
            bundleOrderItem.setRetailPrice(itemRequest.getRetailPriceOverride());
            bundleOrderItem.setBaseRetailPrice(itemRequest.getRetailPriceOverride());
        }

        for (SkuBundleItem skuBundleItem : productBundle.getSkuBundleItems()) {
            Product bundleProduct = skuBundleItem.getBundle();
            Sku bundleSku = skuBundleItem.getSku();

            Category bundleCategory = null;
            if (itemRequest.getCategory() != null) {
                bundleCategory = itemRequest.getCategory();
            }

            if (bundleCategory == null && bundleProduct != null) {
                bundleCategory = bundleProduct.getDefaultCategory();
            }

            DiscreteOrderItemRequest bundleItemRequest = new DiscreteOrderItemRequest();
            bundleItemRequest.setCategory(bundleCategory);
            bundleItemRequest.setProduct(bundleProduct);
            bundleItemRequest.setQuantity(skuBundleItem.getQuantity());
            bundleItemRequest.setSku(bundleSku);
            bundleItemRequest.setItemAttributes(itemRequest.getItemAttributes());
            bundleItemRequest.setSalePriceOverride(skuBundleItem.getSalePrice());
            bundleItemRequest.setBundleOrderItem(bundleOrderItem);

            DiscreteOrderItem bundleDiscreteItem = createDiscreteOrderItem(bundleItemRequest);
            bundleDiscreteItem.setSkuBundleItem(skuBundleItem);
            bundleOrderItem.getDiscreteOrderItems().add(bundleDiscreteItem);
        }

        if (saveItem) {
            bundleOrderItem = (BundleOrderItem) saveOrderItem(bundleOrderItem);
        }

        applyAdditionalOrderItemProperties(bundleOrderItem);

        return bundleOrderItem;
    }

    @Override
    public BundleOrderItem createBundleOrderItem(final ProductBundleOrderItemRequest itemRequest) {
        return createBundleOrderItem(itemRequest, true);
    }

    @Override
    public OrderItemRequestDTO buildOrderItemRequestDTOFromOrderItem(OrderItem item) {
        OrderItemRequestDTO orderItemRequest;
        if (item instanceof DiscreteOrderItem) {
            DiscreteOrderItem doi = (DiscreteOrderItem) item;
            orderItemRequest = new OrderItemRequestDTO();

            if (doi.getCategory() != null) {
                orderItemRequest.setCategoryId(doi.getCategory().getId());
            }

            if (doi.getProduct() != null) {
                orderItemRequest.setProductId(doi.getProduct().getId());
            }

            if (doi.getSku() != null) {
                orderItemRequest.setSkuId(doi.getSku().getId());
            }

            if (doi.getAdditionalAttributes() != null) {
                for (Entry<String, String> entry : doi.getAdditionalAttributes().entrySet()) {
                    orderItemRequest.getAdditionalAttributes().put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            orderItemRequest = new NonDiscreteOrderItemRequestDTO();
            NonDiscreteOrderItemRequestDTO ndr = (NonDiscreteOrderItemRequestDTO) orderItemRequest;

            ndr.setItemName(item.getName());
            ndr.setOverrideRetailPrice(item.getRetailPrice());
            ndr.setOverrideSalePrice(item.getSalePrice());
        }

        orderItemRequest.setQuantity(item.getQuantity());
        if (item.getOrderItemAttributes() != null) {
            for (Entry<String, OrderItemAttribute> entry : item.getOrderItemAttributes().entrySet()) {
                orderItemRequest.getItemAttributes().put(entry.getKey(), entry.getValue().getValue());
            }
        }

        if (CollectionUtils.isNotEmpty(item.getChildOrderItems())) {
            for (OrderItem childItem : item.getChildOrderItems()) {
                orderItemRequest.getChildOrderItems().add(buildOrderItemRequestDTOFromOrderItem(childItem));
            }
        }

        return orderItemRequest;
    }

    @Override
    public OrderItem buildOrderItemFromDTO(Order order, OrderItemRequestDTO orderItemRequestDTO) {
        Sku sku = null;
        if (orderItemRequestDTO.getSkuId() != null) {
            sku = catalogService.findSkuById(orderItemRequestDTO.getSkuId());
        }

        Product product = null;
        if (orderItemRequestDTO.getProductId() != null) {
            product = catalogService.findProductById(orderItemRequestDTO.getProductId());
        }

        Category category = null;
        if (orderItemRequestDTO.getCategoryId() != null) {
            category = catalogService.findCategoryById(orderItemRequestDTO.getCategoryId());
        }

        if (category == null && product != null) {
            category = product.getDefaultCategory();
        }

        OrderItem item;
        if (orderItemRequestDTO instanceof NonDiscreteOrderItemRequestDTO) {
            NonDiscreteOrderItemRequestDTO ndr = (NonDiscreteOrderItemRequestDTO) orderItemRequestDTO;
            OrderItemRequest itemRequest = new OrderItemRequest();
            itemRequest.setQuantity(ndr.getQuantity());
            itemRequest.setRetailPriceOverride(ndr.getOverrideRetailPrice());
            itemRequest.setSalePriceOverride(ndr.getOverrideSalePrice());
            itemRequest.setItemAttributes(orderItemRequestDTO.getItemAttributes());
            itemRequest.setAdditionalAttributes(orderItemRequestDTO.getAdditionalAttributes());
            itemRequest.setItemName(ndr.getItemName());
            itemRequest.setOrder(order);
            item = createOrderItem(itemRequest);
        } else if (product == null || !(product instanceof ProductBundle)) {
            DiscreteOrderItemRequest itemRequest = new DiscreteOrderItemRequest();
            itemRequest.setCategory(category);
            itemRequest.setProduct(product);
            itemRequest.setSku(sku);
            itemRequest.setQuantity(orderItemRequestDTO.getQuantity());
            itemRequest.setItemAttributes(orderItemRequestDTO.getItemAttributes());
            itemRequest.setAdditionalAttributes(orderItemRequestDTO.getAdditionalAttributes());
            itemRequest.setOrder(order);
            itemRequest.setSalePriceOverride(orderItemRequestDTO.getOverrideSalePrice());
            itemRequest.setRetailPriceOverride(orderItemRequestDTO.getOverrideRetailPrice());
            item = createDiscreteOrderItem(itemRequest);
        } else {
            ProductBundleOrderItemRequest bundleItemRequest = new ProductBundleOrderItemRequest();
            bundleItemRequest.setCategory(category);
            bundleItemRequest.setProductBundle((ProductBundle) product);
            bundleItemRequest.setSku(sku);
            bundleItemRequest.setQuantity(orderItemRequestDTO.getQuantity());
            bundleItemRequest.setItemAttributes(orderItemRequestDTO.getItemAttributes());
            bundleItemRequest.setName(product.getName());
            bundleItemRequest.setOrder(order);
            bundleItemRequest.setSalePriceOverride(orderItemRequestDTO.getOverrideSalePrice());
            bundleItemRequest.setRetailPriceOverride(orderItemRequestDTO.getOverrideRetailPrice());
            item = createBundleOrderItem(bundleItemRequest, false);
        }

        if (orderItemRequestDTO.getParentOrderItemId() != null) {
            OrderItem parent = readOrderItemById(orderItemRequestDTO.getParentOrderItemId());
            item.setParentOrderItem(parent);
        }

        if (orderItemRequestDTO.getHasConfigurationError()) {
            item.setHasValidationError(true);
        }

        if (orderItemRequestDTO instanceof ConfigurableOrderItemRequest) {
            ConfigurableOrderItemRequest configRequest = (ConfigurableOrderItemRequest) orderItemRequestDTO;
            if (configRequest.getHasConfigurationError()) {
                item.setHasValidationError(true);
            }
            if (!configRequest.getDiscountsAllowed()) {
                item.setDiscountingAllowed(false);
            }
        }

        applyAdditionalOrderItemProperties(item);

        return item;
    }

    @Override
    public void priceOrderItem(OrderItem item) {
        extensionManager.getProxy().modifyOrderItemPrices(item);
    }

    @Override
    public Set<Product> findAllProductsInRequest(ConfigurableOrderItemRequest itemRequest) {
        Set<Product> allProductsSet = findAllChildProductsInRequest(itemRequest.getChildOrderItems());
        allProductsSet.add(itemRequest.getProduct());
        return allProductsSet;
    }

    protected Set<Product> findAllChildProductsInRequest(List<OrderItemRequestDTO> childItems) {
        Set<Product> allProductsSet = new HashSet<Product>();
        for (OrderItemRequestDTO child : childItems) {
            ConfigurableOrderItemRequest configChild = (ConfigurableOrderItemRequest) child;
            Product childProduct = configChild.getProduct();
            if (childProduct != null) {
                allProductsSet.add(childProduct);
            } else {
                List<OrderItemRequestDTO> productChoices = new ArrayList<OrderItemRequestDTO>(configChild.getProductChoices());
                allProductsSet.addAll(findAllChildProductsInRequest(productChoices));
            }
        }
        return allProductsSet;
    }

    @Override
    public void applyAdditionalOrderItemProperties(OrderItem orderItem) {
        extensionManager.getProxy().applyAdditionalOrderItemProperties(orderItem);
    }

    @Override
    public ConfigurableOrderItemRequest createConfigurableOrderItemRequestFromProduct(Product product) {
        ConfigurableOrderItemRequest itemRequest = new ConfigurableOrderItemRequest();
        itemRequest.setProduct(product);
        itemRequest.setSkuId(product.getDefaultSku().getId());
        itemRequest.setQuantity(1);
        itemRequest.setDisplayPrice(product.getDefaultSku().getPrice());
        itemRequest.setDiscountsAllowed(product.getDefaultSku().isDiscountable());
        return itemRequest;
    }

    @Override
    public void modifyOrderItemRequest(ConfigurableOrderItemRequest itemRequest) {
        extensionManager.getProxy().modifyOrderItemRequest(itemRequest);
    }

    @Override
    public void mergeOrderItemRequest(ConfigurableOrderItemRequest itemRequest, OrderItem orderItem) {
        extensionManager.getProxy().mergeOrderItemRequest(itemRequest, orderItem);
    }

    @Override
    public List<OrderItem> findOrderItemsForCustomersInDateRange(List<Long> customerIds, Date startDate, Date endDate) {
        return orderItemDao.readOrderItemsForCustomersInDateRange(customerIds, startDate, endDate);
    }

    @Override
    public List<OrderItem> readBatchOrderItems(int start, int count, List<OrderStatus> orderStatusList) {
        return orderItemDao.readBatchOrderItems(start, count, orderStatusList);
    }

    @Override
    public Long readNumberOfOrderItems() {
        return orderItemDao.readNumberOfOrderItems();
    }
}
