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
package org.broadleafcommerce.core.web.search;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.lang.ArrayUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Product;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * SearchFilterUtil exposes a simple static method for filtering out products that do not match the
 * criteria POSTed to a controller by a {@link SearchFilterTag}.
 */
public class SearchFilterUtil {
    /**
     * filterProducts iterates over the products for each allowed parameter, filtering out products that do
     * not match the values passed in via the parameters argument. There are 2 ways that a product can be
     * filtered out, corresponding to the multiSelect and sliderRange displayTypes on {@link SearchFilterItemTag}.
     * For multiSelect items, the method will remove the product if the property specified in allowedParameters's
     * toString() method returns a String equal to one of the Strings in the corresponding String[] in parameters.
     * For sliderRange items, the property on the product must be of type {@link Money}. The product will be filtered
     * out if it's property is greater than the Money value parsed out of max-(property name) or smaller than the Money
     * value parsed from min-(property name)
     * @param products the list of products to filter
     * @param parameters the parameters passed to the controller. Generally request.getParameterMap()
     * @param allowedParameters an array of the allowed parameters to filter on
     */
    public static void filterProducts(List<Product> products,  Map<String, String[]>parameters, String[] allowedParameters) {
        for (String parameter : allowedParameters) {
            BeanToPropertyValueTransformer reader = new BeanToPropertyValueTransformer(parameter, true);
            if (parameters.containsKey(parameter)) { // we're doing a multi-select
                for (Iterator<Product> itr = products.iterator(); itr.hasNext(); ) {
                    Product product = itr.next();
                    if (!ArrayUtils.contains(parameters.get(parameter), reader.transform(product).toString())) {
                        itr.remove();
                    }
                }
            } else if (parameters.containsKey("min-"+parameter)) {
                String minMoney = parameters.get("min-" + parameter)[0];
                String maxMoney = parameters.get("max-" + parameter)[0];
                Money minimumMoney = new Money(minMoney.replaceAll("[^0-9.]", ""));
                Money maximumMoney = new Money(maxMoney.replaceAll("[^0-9.]", ""));
                for (Iterator<Product> itr = products.iterator(); itr.hasNext();) {
                    Product product = itr.next();
                    Money objectValue = (Money) reader.transform(product);
                    if (objectValue.lessThan(minimumMoney) || objectValue.greaterThan(maximumMoney)) {
                        itr.remove();
                    }
                }
            }
        }
    }
}
