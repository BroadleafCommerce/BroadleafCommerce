package org.broadleafcommerce.util;

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