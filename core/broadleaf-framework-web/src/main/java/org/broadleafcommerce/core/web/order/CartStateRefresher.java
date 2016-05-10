/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.order;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderPersistedEntityListener;
import org.broadleafcommerce.core.order.domain.OrderPersistedEvent;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;


/**
 * {@link ApplicationListener} responsible for updating {@link CartState} with a new version that was persisted.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * 
 * @see {@link OrderPersistedEntityListener}
 * @see {@link OrderPersistedEvent}
 */
@Component("blCartStateRefresher")
public class CartStateRefresher implements ApplicationListener<OrderPersistedEvent> {

    /**
     * <p>Resets {@link CartState} with the newly persisted Order. If {@link CartState} was empty, this will only update it if
     * the {@link Order} that has been persisted is the {@link OrderStatus#IN_PROCESS} {@link Order} for the active
     * {@link Customer} (as determined by {@link CustomerState#getCustomer()}. If {@link CartState} was <b>not</b> empty,
     * then it will be replaced only if this newly persisted {@link Order} has the same id.</p>
     * 
     * <p>This ensures that whatever is returned from {@link CartState#getCart()} will always be the most up-to-date
     * database version (meaning, safe to write to the DB).</p>
     */
    @Override
    public void onApplicationEvent(final OrderPersistedEvent event) {
        WebRequest request = BroadleafRequestContext.getBroadleafRequestContext().getWebRequest();
        if (request != null) {
             Order dbOrder = event.getOrder();
            //Update the cart state ONLY IF the IDs of the newly persisted order and whatever is already in CartState match
            boolean emptyCartState = CartState.getCart() == null || CartState.getCart() instanceof NullOrderImpl;
            if (emptyCartState) {
                //If cart state is empty, set it to this newly persisted order if it's the active Customer's cart
                if (CustomerState.getCustomer() != null && CustomerState.getCustomer().getId().equals(dbOrder.getCustomer().getId())
                        && OrderStatus.IN_PROCESS.equals(dbOrder.getStatus())) {
                    CartState.setCart(dbOrder);
                }
            } else if (CartState.getCart().getId().equals(dbOrder.getId())) {
                CartState.setCart(dbOrder);
            }
        }
    }

}
