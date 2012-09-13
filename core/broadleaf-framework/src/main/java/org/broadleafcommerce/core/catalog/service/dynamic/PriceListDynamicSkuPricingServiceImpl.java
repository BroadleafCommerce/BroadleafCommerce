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

package org.broadleafcommerce.core.catalog.service.dynamic;

import java.util.HashMap;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.pricelist.domain.PriceList;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.pricing.domain.PriceAdjustment;
import org.broadleafcommerce.core.pricing.domain.PriceData;
import org.springframework.stereotype.Service;

/**
 * 
 * @author jfischer
 * 
 */
@Service("blPriceListDynamicSkuPricingService")
public class PriceListDynamicSkuPricingServiceImpl extends
        DefaultDynamicSkuPricingServiceImpl {

    @Override
    public DynamicSkuPrices getSkuPrices(Sku sku,
            @SuppressWarnings("rawtypes") HashMap skuPricingConsiderations) {
        BroadleafRequestContext brc = BroadleafRequestContext
                .getBroadleafRequestContext();
        // the default behavior is to ignore the pricing considerations and
        // return the retail and sale price from the sku

        DynamicSkuPrices prices = null;

        PriceList priceList = brc.getPriceList();
  
        
        if (priceList != null) {
            if (sku.getPriceDataMap() == null
                    || sku.getPriceDataMap().get(
                            priceList.getPriceKey()) == null) {
                if (!priceList.getDefaultFlag()) {
                    /**
                     *  if there is a pricelist , but the useDefaultIfNotFound is false, then return null.
                     *  This is a way to ensure that if there is no pricedata for the item , then don't return the default currency value.
                     */
                    return null;
                }
            } else {
                PriceData priceData = sku
                        .getPriceDataMap().get(priceList.getPriceKey());
                if (priceData != null) {
                    prices = new DynamicSkuPrices();
                    prices.setRetailPrice(BroadleafCurrencyImpl.getMoney(
                            priceData.getRetailPrice(), priceList.getCurrencyCode()));
                    prices.setSalePrice(BroadleafCurrencyImpl.getMoney(
                            priceData.getSalePrice(), priceList.getCurrencyCode()));
                }
            }
        }
      
        return prices;
    }

    @Override
    public DynamicSkuPrices getPriceAdjustment(
            ProductOptionValue productOptionValue,
            @SuppressWarnings("rawtypes") HashMap skuPricingConsiderations) {
        BroadleafRequestContext brc = BroadleafRequestContext
                .getBroadleafRequestContext();
        // the default behavior is to ignore the pricing considerations and
        // return the price Adjustment from the productOptionValue
        DynamicSkuPrices prices = new DynamicSkuPrices();

        PriceList priceList = brc.getPriceList();
        if (priceList != null
                && productOptionValue.getPriceAdjustmentMap().get(
                        priceList.getPriceKey()) != null) {
            PriceAdjustment priceData = productOptionValue
                    .getPriceAdjustmentMap().get(priceList.getPriceKey());
            prices.setPriceAdjustment(BroadleafCurrencyImpl.getMoney(
                    priceData.getPriceAdjustment(), priceList.getCurrencyCode()));
        } else {
            prices.setPriceAdjustment(null);
        }

        return prices;
    }

    @Override
    public Money getPriceAdjustmentForProductOptionValue(
            ProductOptionValue productOptionValue,
            @SuppressWarnings("rawtypes") HashMap skuPricingConsiderations) {
        BroadleafRequestContext brc = BroadleafRequestContext
                .getBroadleafRequestContext();
        // the default behavior is to ignore the pricing considerations and
        // return the price Adjustment from the productOptionValue
        Money adjustment = null;

        PriceList priceList = brc.getPriceList();
        if (priceList != null) {
            if (productOptionValue.getPriceAdjustmentMap() == null
                    || productOptionValue.getPriceAdjustmentMap().get(
                            priceList.getPriceKey()) == null) {
                if (!priceList.getDefaultFlag()) {
                    /**
                     *  if there is a pricelist , but the useDefaultIfNotFound is false, then return null.
                     *  This is a way to ensure that if there is no pricedata for the item , then don't return the default currency value.
                     */
                    return null;
                }
            } else {
                PriceAdjustment priceData = productOptionValue
                        .getPriceAdjustmentMap().get(priceList.getPriceKey());
                if (priceData != null) {
                    adjustment = BroadleafCurrencyImpl.getMoney(
                            priceData.getPriceAdjustment(),
                            priceList.getCurrencyCode());
                }
            }
        }
      

        return adjustment;
    }
}
