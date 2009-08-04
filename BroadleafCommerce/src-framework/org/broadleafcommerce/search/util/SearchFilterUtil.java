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
package org.broadleafcommerce.search.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.lang.ArrayUtils;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.util.money.Money;

public class SearchFilterUtil {
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
