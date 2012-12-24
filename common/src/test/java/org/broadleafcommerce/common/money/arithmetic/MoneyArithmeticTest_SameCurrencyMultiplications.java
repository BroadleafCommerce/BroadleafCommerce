package org.broadleafcommerce.common.money.arithmetic;

import java.math.BigDecimal;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.broadleafcommerce.common.money.Money;

public class MoneyArithmeticTest_SameCurrencyMultiplications extends TestCase {
    public void testMoneyArithmetic_MultiplyZeroDollar(){
        BigDecimal increment = new BigDecimal(0).setScale(2);
        Money actual = new Money(10).multiply(increment);
        Money expected = new Money(0); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_MultiplyOneDollar(){
        BigDecimal increment = new BigDecimal(1.00).setScale(2);
        Money actual = new Money(10).multiply(increment);
        Money expected = new Money(10); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_MultiplyTenDollars(){
        BigDecimal increment = new BigDecimal(10).setScale(2);
        Money actual = new Money(10).multiply(increment);
        Money expected = new Money(100); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_MultiplyHundredDollars(){
        BigDecimal increment = new BigDecimal(100).setScale(2);
        Money actual = new Money(10).multiply(increment);
        Money expected = new Money(1000); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_MultiplyThousandDollars(){
        BigDecimal increment = new BigDecimal(1000).setScale(2);
        Money actual = new Money(10).multiply(increment);
        Money expected = new Money(10000); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_MultiplyPenny(){
        BigDecimal increment = new BigDecimal(0.01).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Money actual = new Money(10).multiply(increment);
        Money expected = new Money(0.1); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_MultiplyDime(){
        BigDecimal increment = new BigDecimal(0.1).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Money actual = new Money(10).multiply(increment);
        Money expected = new Money(1); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_MultiplyFiftyCents(){
        BigDecimal increment = new BigDecimal(0.5).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Money actual = new Money(10).multiply(increment);
        Money expected = new Money(5); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_MultiplyADollarAndFiftyCents(){
        BigDecimal increment = new BigDecimal(1.50).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Money actual = new Money(10).multiply(increment);
        Money expected = new Money(15); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_MultiplyNegativeADollarAndFiftyCents(){
        BigDecimal increment = new BigDecimal(-1.50).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Money actual = new Money(10).multiply(increment);
        Money expected = new Money(-15); 
        Assert.assertEquals(expected, actual);
    }
}
