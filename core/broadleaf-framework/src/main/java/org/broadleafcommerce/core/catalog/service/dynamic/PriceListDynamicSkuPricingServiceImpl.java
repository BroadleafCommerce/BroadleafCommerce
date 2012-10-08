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
import org.broadleafcommerce.core.catalog.domain.ProductOptionValueImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuBundleItem;
import org.broadleafcommerce.core.pricing.domain.PriceAdjustment;
import org.broadleafcommerce.core.pricing.domain.PriceData;
import org.broadleafcommerce.core.pricing.domain.SkuBundleItemPriceData;
import org.springframework.stereotype.Service;

/**
 * 
 * @author jfischer
 * 
 */
@Service("blPriceListDynamicSkuPricingService")
public class PriceListDynamicSkuPricingServiceImpl implements DynamicSkuPricingService {

    @Override
    public DynamicSkuPrices getSkuPrices(Sku sku,
            @SuppressWarnings("rawtypes") HashMap skuPricingConsiderations) {
        BroadleafRequestContext brc = BroadleafRequestContext
                .getBroadleafRequestContext();
        // the default behavior is to ignore the pricing considerations and
        // return the retail and sale price from the sku

        DynamicSkuPrices prices = new DynamicSkuPrices();;

        PriceList priceList = brc.getPriceList();
  
        
        if (priceList != null) {
            if (sku.getPriceDataMap() != null) {               
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
            
            Money adjustments = null;
            if (sku.getProductOptionValueAdjustments() != null) {
                for(ProductOptionValue optionValue : sku.getProductOptionValues()) {
                    if (optionValue.getPriceAdjustmentMap() != null) {                        
                        PriceAdjustment adjustment = optionValue.getPriceAdjustmentMap().get(priceList.getPriceKey());
                        if (adjustment != null && adjustment.getPriceAdjustment() != null) {
                            Money adjustmentAsMoney = BroadleafCurrencyImpl.getMoney(adjustment.getPriceAdjustment(), priceList.getCurrencyCode());
                            if (adjustments == null) {
                                adjustments = adjustmentAsMoney;
                            } else {
                                adjustments = adjustments.add(adjustmentAsMoney);
                            }
                        }                        
                    }
                }
                prices.setPriceAdjustment(adjustments);
            }           
        } else {
            prices.setRetailPrice(sku.getRetailPrice());
            prices.setSalePrice(sku.getSalePrice());
            prices.setPriceAdjustment(sku.getProductOptionValueAdjustments());
        }
        
        return prices;
    }

    
   // TODO: BCP -- Need to apply the same approach (e.g. proxy to this method to prevent recursion)
    @Override
    public DynamicSkuPrices getSkuBundleItemPrice(
            SkuBundleItem skuBundleItem,
            @SuppressWarnings("rawtypes") HashMap skuPricingConsiderations) {
        BroadleafRequestContext brc = BroadleafRequestContext
                .getBroadleafRequestContext();
  
        
        // the default behavior is to ignore the pricing considerations and
        // return the retail and sale price from the sku

        DynamicSkuPrices prices = new DynamicSkuPrices();;

        PriceList priceList = brc.getPriceList();
  
        
        if ((priceList != null)) {
            if( skuBundleItem.getPriceDataMap() != null )
              {
               
                 SkuBundleItemPriceData priceData = skuBundleItem
                         .getPriceDataMap().get(priceList.getPriceKey());
                if (priceData != null) {
                    prices = new DynamicSkuPrices();
                    prices.setSalePrice(BroadleafCurrencyImpl.getMoney(
                    priceData.getSalePrice(), priceList.getCurrencyCode()));
                  
                }
            
    
            
        }
        }else {
          
            prices.setSalePrice(skuBundleItem.getSalePrice());
          
        }
   
        return prices;     
    }


    @Override
    public DynamicSkuPrices getPriceAdjustment(
            ProductOptionValueImpl skuBundleItem,
            Money priceAdjustment, HashMap skuPricingConsiderationContext) {
        
        BroadleafRequestContext brc = BroadleafRequestContext
                .getBroadleafRequestContext();
  
        
        // the default behavior is to ignore the pricing considerations and
        // return the retail and sale price from the sku

        DynamicSkuPrices prices = new DynamicSkuPrices();;

        PriceList priceList = brc.getPriceList();
  
        
        if ((priceList != null)) {
            if( skuBundleItem.getPriceAdjustmentMap() != null) {
               
            PriceAdjustment priceData = skuBundleItem
                        .getPriceAdjustmentMap().get(priceList.getPriceKey());
                if (priceData != null) {
                    prices = new DynamicSkuPrices();
                    prices.setPriceAdjustment(BroadleafCurrencyImpl.getMoney(
                            priceData.getPriceAdjustment(), priceList.getCurrencyCode()));
                  
                }
            
        }
        } else {
          
            prices.setPriceAdjustment(priceAdjustment);
          
        }
        
        
        return prices;
        
        
    }

    
 
}
