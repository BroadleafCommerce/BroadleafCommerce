/*
 * #%L
 * BroadleafCommerce Common Libraries
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
