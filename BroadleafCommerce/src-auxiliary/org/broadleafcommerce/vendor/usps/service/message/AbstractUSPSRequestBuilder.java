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
package org.broadleafcommerce.vendor.usps.service.message;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.broadleafcommerce.util.DimensionUnitOfMeasureType;
import org.broadleafcommerce.util.UnitOfMeasureUtil;
import org.broadleafcommerce.util.WeightUnitOfMeasureType;

public abstract class AbstractUSPSRequestBuilder implements org.broadleafcommerce.vendor.usps.service.message.USPSRequestBuilder {

    public String findPounds(BigDecimal weight, WeightUnitOfMeasureType type) {
        int pounds = UnitOfMeasureUtil.findWholePounds(weight, type);
        return String.valueOf(pounds);
    }

    public String findOunces(BigDecimal weight, WeightUnitOfMeasureType type) {
        BigDecimal ounces = UnitOfMeasureUtil.findRemainingOunces(weight, type);
        DecimalFormat format = new DecimalFormat("0.#");
        return format.format(ounces.doubleValue());
    }

    public String findInches(BigDecimal dimension, DimensionUnitOfMeasureType type) {
        dimension = UnitOfMeasureUtil.findInches(dimension, type);
        DecimalFormat format = new DecimalFormat("0.#");
        return format.format(dimension.doubleValue());
    }

}
