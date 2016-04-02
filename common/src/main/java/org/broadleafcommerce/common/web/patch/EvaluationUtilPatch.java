/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.common.web.patch;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Source for this patch is taken from https://github.com/thymeleaf/thymeleaf/commit/fc360d0d50df5090f93f6922012463055f28896d.
 * This is part of https://github.com/thymeleaf/thymeleaf/issues/449, which will be resolved in 2.1.5-RELEASE (not released yet).
 * This fix benefits the performance of many Broadleaf templates that utilize string concatenation in expressions.
 *
 * When Thymeleaf 2.1.5 is released, the Broadleaf dependency should be updated and this patch should be removed.
 *
 * @author Jeff Fischer
 */
public class EvaluationUtilPatch {

    public static BigDecimal evaluateAsNumber(final Object object) {

        if (object == null) {
            return null;
        }

        if (object instanceof Number) {
            if (object instanceof BigDecimal) {
                return (BigDecimal)object;
            } else if (object instanceof BigInteger) {
                return new BigDecimal((BigInteger)object);
            } else if (object instanceof Short) {
                return new BigDecimal(((Short)object).intValue());
            } else if (object instanceof Integer) {
                return new BigDecimal(((Integer)object).intValue());
            } else if (object instanceof Long) {
                return new BigDecimal(((Long)object).longValue());
            } else if (object instanceof Float) {
                //noinspection UnpredictableBigDecimalConstructorCall
                return new BigDecimal(((Float)object).doubleValue());
            } else if (object instanceof Double) {
                //noinspection UnpredictableBigDecimalConstructorCall
                return new BigDecimal(((Double)object).doubleValue());
            }
        } else if (object instanceof String && ((String)object).length() > 0) {
            final char c0 = ((String)object).charAt(0);
            // This test will avoid trying to create the BigDecimal most of the times, which
            // will improve performance by avoiding lots of NumberFormatExceptions
            if ((c0 >= '0' && c0 <= '9') || c0 == '+' || c0 == '-') {
                try {
                    return new BigDecimal(((String)object).trim());
                } catch (final NumberFormatException ignored) {
                    return null;
                }
            }
        }

        return null;

    }

}
