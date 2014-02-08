/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.order.service;

import org.broadleafcommerce.core.order.dao.OrderMultishipOptionDao;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderMultishipOption;
import org.broadleafcommerce.core.order.domain.OrderMultishipOptionImpl;
import org.broadleafcommerce.core.order.service.call.OrderMultishipOptionDTO;
import org.broadleafcommerce.profile.core.service.AddressService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

/**
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Service("blOrderMultishipOptionService")
public class OrderMultishipOptionServiceImpl implements OrderMultishipOptionService {

    @Resource(name = "blOrderMultishipOptionDao")
    OrderMultishipOptionDao orderMultishipOptionDao;
    
    @Resource(name = "blAddressService")
    protected AddressService addressService;
    
    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;
    
    @Resource(name = "blFulfillmentOptionService")
    protected FulfillmentOptionService fulfillmentOptionService;
    
    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;

    @Override
    public OrderMultishipOption save(OrderMultishipOption orderMultishipOption) {
        return orderMultishipOptionDao.save(orderMultishipOption);
    }

    @Override
    public List<OrderMultishipOption> findOrderMultishipOptions(Long orderId) {
        return orderMultishipOptionDao.readOrderMultishipOptions(orderId);
    }
    
    @Override
    public List<OrderMultishipOption> findOrderItemOrderMultishipOptions(Long orderItemId) {
        return orderMultishipOptionDao.readOrderItemOrderMultishipOptions(orderItemId);
    }
    
    @Override
    public OrderMultishipOption create() {
        return orderMultishipOptionDao.create();
    }
    
    @Override
    public void deleteOrderItemOrderMultishipOptions(Long orderItemId) {
        List<OrderMultishipOption> options = findOrderItemOrderMultishipOptions(orderItemId);
        orderMultishipOptionDao.deleteAll(options);
    }
    
    @Override
    public void deleteOrderItemOrderMultishipOptions(Long orderItemId, int numToDelete) {
        List<OrderMultishipOption> options = findOrderItemOrderMultishipOptions(orderItemId);
        numToDelete = (numToDelete > options.size()) ? options.size() : numToDelete;
        options = options.subList(0, numToDelete);
        orderMultishipOptionDao.deleteAll(options);
    }
    
    @Override
    public void deleteAllOrderMultishipOptions(Order order) {
        List<OrderMultishipOption> options = findOrderMultishipOptions(order.getId());
        orderMultishipOptionDao.deleteAll(options);
    }
    
    @Override
    public void saveOrderMultishipOptions(Order order, List<OrderMultishipOptionDTO> optionDTOs) {
        Map<Long, OrderMultishipOption> currentOptions = new HashMap<Long, OrderMultishipOption>();
        for (OrderMultishipOption option : findOrderMultishipOptions(order.getId())) {
            currentOptions.put(option.getId(), option);
        }
        
        List<OrderMultishipOption> orderMultishipOptions = new ArrayList<OrderMultishipOption>();
        for (OrderMultishipOptionDTO dto: optionDTOs) {
            OrderMultishipOption option = currentOptions.get(dto.getId());
            if (option == null) {
                option = orderMultishipOptionDao.create();
            }
            
            option.setOrder(order);
            option.setOrderItem(orderItemService.readOrderItemById(dto.getOrderItemId()));
            
            if (dto.getAddressId() != null) {
                option.setAddress(addressService.readAddressById(dto.getAddressId()));
            } else {
                option.setAddress(null);
            }
            
            if (dto.getFulfillmentOptionId() != null) {
                option.setFulfillmentOption(fulfillmentOptionService.readFulfillmentOptionById(dto.getFulfillmentOptionId()));
            } else {
                option.setFulfillmentOption(null);
            }
            
            orderMultishipOptions.add(option);
        }
        
        for (OrderMultishipOption option : orderMultishipOptions) {
            save(option);
        }
    }
    
    @Override
    public List<OrderMultishipOption> getOrGenerateOrderMultishipOptions(Order order) {
        List<OrderMultishipOption> orderMultishipOptions = findOrderMultishipOptions(order.getId());
        if (orderMultishipOptions == null || orderMultishipOptions.size() == 0) {
            orderMultishipOptions = generateOrderMultishipOptions(order);
        }
        
        // Create a map representing the current discrete order item counts for the order
        Map<Long, Integer> orderDiscreteOrderItemCounts = new HashMap<Long, Integer>();
        for (DiscreteOrderItem item : order.getDiscreteOrderItems()) {
            orderDiscreteOrderItemCounts.put(item.getId(), item.getQuantity());
        }
        
        List<OrderMultishipOption> optionsToRemove = new ArrayList<OrderMultishipOption>();
        for (OrderMultishipOption option : orderMultishipOptions) {
            Integer count = orderDiscreteOrderItemCounts.get(option.getOrderItem().getId());
            if (count == null || count == 0) {
                optionsToRemove.add(option);
            } else {
                count--;
                orderDiscreteOrderItemCounts.put(option.getOrderItem().getId(), count);
            }
        }
        
        for (Entry<Long, Integer> entry : orderDiscreteOrderItemCounts.entrySet()) {
            DiscreteOrderItem item = (DiscreteOrderItem) orderItemService.readOrderItemById(entry.getKey());
            orderMultishipOptions.addAll(createPopulatedOrderMultishipOption(order, item, entry.getValue()));
        }
        
        orderMultishipOptions.removeAll(optionsToRemove);
        orderMultishipOptionDao.deleteAll(optionsToRemove);
        
        return orderMultishipOptions;
    }
    
    @Override
    public List<OrderMultishipOption> getOrderMultishipOptionsFromDTOs(Order order, List<OrderMultishipOptionDTO> optionDtos) {
        List<OrderMultishipOption> orderMultishipOptions = new ArrayList<OrderMultishipOption>();
        for (OrderMultishipOptionDTO optionDto : optionDtos) {
            OrderMultishipOption option = new OrderMultishipOptionImpl();
            if (optionDto.getAddressId() != null) {
                option.setAddress(addressService.readAddressById(optionDto.getAddressId()));
            }   
            if (optionDto.getFulfillmentOptionId() != null) {
                option.setFulfillmentOption(fulfillmentOptionService.readFulfillmentOptionById(optionDto.getFulfillmentOptionId()));
            }
            option.setId(optionDto.getId());
            option.setOrder(order);
            option.setOrderItem(orderItemService.readOrderItemById(optionDto.getOrderItemId()));
            orderMultishipOptions.add(option);
        }
        return orderMultishipOptions;
    }
    
    @Override
    public List<OrderMultishipOption> generateOrderMultishipOptions(Order order) {
        List<OrderMultishipOption> orderMultishipOptions = new ArrayList<OrderMultishipOption>();
        for (DiscreteOrderItem discreteOrderItem : order.getDiscreteOrderItems()) {
            orderMultishipOptions.addAll(createPopulatedOrderMultishipOption(order, discreteOrderItem, discreteOrderItem.getQuantity()));
        }
        
        return orderMultishipOptions;
    }
    
    protected List<OrderMultishipOption> createPopulatedOrderMultishipOption(Order order, DiscreteOrderItem item, Integer quantity) {
        List<OrderMultishipOption> orderMultishipOptions = new ArrayList<OrderMultishipOption>();
        if (!fulfillmentGroupService.isShippable(item.getSku().getFulfillmentType())) {
            return orderMultishipOptions;
        }
        for (int i = 0; i < quantity; i++) {
            OrderMultishipOption orderMultishipOption = new OrderMultishipOptionImpl();
            orderMultishipOption.setOrder(order);
            orderMultishipOption.setOrderItem(item);
            orderMultishipOptions.add(orderMultishipOption);
        }
        return orderMultishipOptions;
    }
}
