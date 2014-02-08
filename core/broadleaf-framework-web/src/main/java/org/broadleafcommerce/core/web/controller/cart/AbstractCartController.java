/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.controller.cart;

import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.payment.service.OrderToPaymentRequestDTOService;
import org.broadleafcommerce.core.web.service.UpdateCartService;

import javax.annotation.Resource;

/**
 * An abstract controller that provides convenience methods and resource declarations for its
 * children. Operations that are shared between controllers that deal with the catalog belong here.
 * 
 * @author apazzolini
 */
public abstract class AbstractCartController extends BroadleafAbstractController {
    
    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;
    
    @Resource(name = "blOrderService")
    protected OrderService orderService;
    
    @Resource(name = "blOfferService")
    protected OfferService offerService;

    @Resource(name="blUpdateCartService")
    protected UpdateCartService updateCartService;

    @Resource(name = "blOrderToPaymentRequestDTOService")
    protected OrderToPaymentRequestDTOService dtoTranslationService;

}
