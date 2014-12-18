package org.broadleafcommerce.core.web.controller.catalog;

import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.order.service.ProductOptionValueService;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BroadleafProductOptionController extends BroadleafAbstractController {

    @Resource(name = "blProductOptionValueService")
    protected ProductOptionValueService productOptionValueService;

    public String getPricingForProductOptions(HttpServletRequest request, HttpServletResponse response, Model model,
                                              Map<String, String> pricingData) throws IOException {
        Long productId = Long.parseLong(pricingData.get("productId"));
        pricingData.remove("productId");

        for(String key : pricingData.keySet()) {
            if(pricingData.get(key) == null) {
                pricingData.remove(key);
            }
        }

        Sku sku = productOptionValueService.findSkuForProductOptionsAndValues(productId, pricingData);
        if (sku != null) {
            pricingData.put("skuPrice", formatPrice(sku.getPrice()).toString());
        } else {
            pricingData.put("skuPrice", "N/A");
            pricingData.put("error", "Product not available");
        }

        return jsonResponse(response, pricingData);
    }


    private String formatPrice(Money price){
        if (price == null){
            return null;
        }
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc.getJavaLocale() != null) {
            return BroadleafCurrencyUtils.getNumberFormatFromCache(brc.getJavaLocale(), price.getCurrency()).format
                    (price.getAmount());
        } else {
            // Setup your BLC_CURRENCY and BLC_LOCALE to display a diff default.
            return "$ " + price.getAmount().toString();
        }
    }

    public String getSelectableProductOptions(HttpServletRequest request, HttpServletResponse response, Model model,
                                      Long productId, String attributeName, String attributeValue) throws IOException {

        Map<Long, List<Long>> selectableProductOptions = new HashMap<Long, List<Long>>();
        List<ProductOptionValue> povList = productOptionValueService.findMatchingProductOptionsForValues(productId, attributeName, attributeValue);

        for(ProductOptionValue pov : povList) {
            ArrayList<Long> poList;
            if(selectableProductOptions.get(pov.getProductOption().getId()) == null) {
                poList = new ArrayList<Long>();
            } else {
                poList = (ArrayList<Long>) selectableProductOptions.get(pov.getProductOption().getId());
            }

            poList.add(pov.getId());
            selectableProductOptions.put(pov.getProductOption().getId(), poList);
        }

        return jsonResponse(response, selectableProductOptions);
    }
}
