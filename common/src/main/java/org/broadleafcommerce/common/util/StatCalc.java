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

public class StatCalc {

    private int count;   // Number of numbers that have been entered.
    private double sum;  // The sum of all the items that have been entered.
    private double squareSum;  // The sum of the squares of all the items.
    private double max = Double.NEGATIVE_INFINITY;  // Largest item seen.
    private double min = Double.POSITIVE_INFINITY;  // Smallest item seen.

    public void enter(double num) {
        // Add the number to the dataset.
        count++;
        sum += num;
        squareSum += num*num;
        if (num > max)
            max = num;
        if (num < min)
            min = num;
    }

    public int getCount() {
        // Return number of items that have been entered.
        return count;
    }

    public double getSum() {
        // Return the sum of all the items that have been entered.
        return sum;
    }

    public double getMean() {
        // Return average of all the items that have been entered.
        // Value is Double.NaN if count == 0.
        return sum / count;
    }

    public double getStandardDeviation() {
        // Return standard deviation of all the items that have been entered.
        // Value will be Double.NaN if count == 0.
        double mean = getMean();
        return Math.sqrt( squareSum/count - mean*mean );
    }

    public double getMin() {
        // Return the smallest item that has been entered.
        // Value will be infinity if no items have been entered.
        return min;
    }

    public double getMax() {
        // Return the largest item that has been entered.
        // Value will be -infinity if no items have been entered.
        return max;
    }

}
