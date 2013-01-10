/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.order.web;

import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.order.web.model.AddToCartItem;
import org.broadleafcommerce.order.web.model.CartSummary;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;


@Controller("blCartControllerREST")
@SessionAttributes("cartSummary")
public class CartControllerREST extends CartController {

    @RequestMapping(value = "currentCart", method = RequestMethod.GET)
    public String viewCart(ModelMap model, HttpServletRequest request) throws PricingException {
        return super.viewCart(model, request);
    }

    @RequestMapping(value = "skus/sku/{skuId}", method = {RequestMethod.POST})
    public String addItem(@PathVariable Long skuId, @ModelAttribute AddToCartItem addToCartItem, BindingResult errors, ModelMap model, HttpServletRequest request) {
        addToCartItem.setSkuId(skuId);
        return super.addItem(false, addToCartItem, errors, model, request);
    }

    @RequestMapping(value = "orderItems/orderItem/{orderItemId}", method = {RequestMethod.DELETE})
    public String removeItem(@PathVariable Long orderItemId, @ModelAttribute CartSummary cartSummary, ModelMap model, HttpServletRequest request) {
        return super.removeItem(orderItemId, cartSummary, model, request);
    }
    
    @RequestMapping(value = "orderItems", method = RequestMethod.PUT)
    public String updateItemQuantity(@ModelAttribute(value="cartSummary") CartSummary cartSummary, Errors errors, ModelMap model, HttpServletRequest request) throws PricingException {
        return super.updateItemQuantity(cartSummary, errors, model, request);
    }
    
    @RequestMapping(value = "promos/promo/{promoCode}", method = RequestMethod.POST)
    public String updatePromoCode (@PathVariable String promoCode, @ModelAttribute(value="cartSummary") CartSummary cartSummary, ModelMap model, HttpServletRequest request) throws PricingException {
        cartSummary.setPromoCode(promoCode);
        return super.updatePromoCode(cartSummary, model, request);
    }
}