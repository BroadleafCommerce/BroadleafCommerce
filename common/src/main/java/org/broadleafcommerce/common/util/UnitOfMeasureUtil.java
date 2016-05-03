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
package org.broadleafcommerce.common.util;

import java.math.BigDecimal;


public class UnitOfMeasureUtil {

    public static BigDecimal convertKilogramsToPounds(BigDecimal kilograms) {
        return kilograms.multiply(BigDecimal.valueOf(0.45359237));
    }

    public static BigDecimal convertPoundsToKilograms(BigDecimal pounds) {
        return pounds.multiply(BigDecimal.valueOf(2.20462262185));
    }

    public static BigDecimal convertPoundsToOunces(BigDecimal pounds) {
        return pounds.multiply(BigDecimal.valueOf(16));
    }

    public static BigDecimal convertOuncesToPounds(BigDecimal ounces) {
        return ounces.multiply(BigDecimal.valueOf(0.0625));
    }

    public static BigDecimal convertFeetToMeters(BigDecimal feet) {
        return feet.multiply(BigDecimal.valueOf(0.3048));
    }

    public static BigDecimal convertMetersToFeet(BigDecimal meters) {
        return meters.multiply(BigDecimal.valueOf(3.28084));
    }

    public static BigDecimal convertInchesToFeet(BigDecimal inches) {
        return inches.multiply(BigDecimal.valueOf(0.083333));
    }

    public static BigDecimal convertFeetToInches(BigDecimal feet) {
        return feet.multiply(BigDecimal.valueOf(12));
    }

    public static int findWholePounds(BigDecimal weight, WeightUnitOfMeasureType type) {
        weight = findPounds(weight, type);
        int pounds = Double.valueOf(Math.floor(weight.doubleValue())).intValue();
        return pounds;
    }

    public static BigDecimal findPounds(BigDecimal weight, WeightUnitOfMeasureType type) {
        if (type.equals(WeightUnitOfMeasureType.KILOGRAMS)) {
            weight = UnitOfMeasureUtil.convertKilogramsToPounds(weight);
        }
        return weight;
    }

    public static BigDecimal findRemainingOunces(BigDecimal weight, WeightUnitOfMeasureType type) {
        if (type.equals(WeightUnitOfMeasureType.KILOGRAMS)) {
            weight = UnitOfMeasureUtil.convertKilogramsToPounds(weight);
        }
        double fractionalPounds = weight.doubleValue() - Math.floor(weight.doubleValue());
        BigDecimal ounces = UnitOfMeasureUtil.convertPoundsToOunces(BigDecimal.valueOf(fractionalPounds));
        return ounces;
    }

    public static BigDecimal findOunces(BigDecimal weight, WeightUnitOfMeasureType type) {
        if (type.equals(WeightUnitOfMeasureType.KILOGRAMS)) {
            weight = UnitOfMeasureUtil.convertKilogramsToPounds(weight);
        }
        BigDecimal ounces = UnitOfMeasureUtil.convertPoundsToOunces(weight);
        return ounces;
    }

    public static BigDecimal findInches(BigDecimal dimension, DimensionUnitOfMeasureType type) {
        if (type.equals(DimensionUnitOfMeasureType.CENTIMETERS)) {
            dimension = UnitOfMeasureUtil.convertFeetToInches(UnitOfMeasureUtil.convertMetersToFeet(dimension.multiply(BigDecimal.valueOf(0.01))));
        }
        if (type.equals(DimensionUnitOfMeasureType.METERS)) {
            dimension = UnitOfMeasureUtil.convertFeetToInches(UnitOfMeasureUtil.convertMetersToFeet(dimension));
        }
        if (type.equals(DimensionUnitOfMeasureType.FEET)) {
            dimension = UnitOfMeasureUtil.convertFeetToInches(dimension);
        }
        return dimension;
    }
}
