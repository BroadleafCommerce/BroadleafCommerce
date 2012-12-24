package org.broadleafcommerce.common.money.arithmetic;

import org.broadleafcommerce.common.money.Money;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MoneyArithmeticTest_SameCurrencySubstractions extends TestCase {
    public void testMoneyArithmetic_SubtractZeroDollar(){
        Money increment = new Money(0);
        Money actual = new Money(1000).subtract(increment);
        Money expected = new Money(1000); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_SubtractOneDollar(){
        Money increment = new Money(1);
        Money actual = new Money(1000).subtract(increment);
        Money expected = new Money(999); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_SubtractFiveDollars(){
        Money increment = new Money(5);
        Money actual = new Money(1000).subtract(increment);
        Money expected = new Money(995); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_SubtractTenDollars(){
        Money increment = new Money(10);
        Money actual = new Money(1000).subtract(increment);
        Money expected = new Money(990); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_SubtractOneHundredDollars(){
        Money increment = new Money(100);
        Money actual = new Money(1000).subtract(increment);
        Money expected = new Money(900); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_SubtractOneThousandDollars(){
        Money increment = new Money(1000);
        Money actual = new Money(1000).subtract(increment);
        Money expected = new Money(0); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_SubtractTwoThousandDollars(){
        Money increment = new Money(2000);
        Money actual = new Money(1000).subtract(increment);
        Money expected = new Money(-1000); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_SubtractNegativeOneThousandDollars(){
        Money increment = new Money(-1000);
        Money actual = new Money(1000).subtract(increment);
        Money expected = new Money(2000); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_SubtractPenny(){
        Money increment = new Money(0.01);
        Money actual = new Money(1000).subtract(increment);
        Money expected = new Money(999.99); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_SubtractDime(){
        Money increment = new Money(0.10);
        Money actual = new Money(1000).subtract(increment);
        Money expected = new Money(999.90); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_SubtractOneHundredAndTwentyFiveCents(){
        Money increment = new Money(100.25);
        Money actual = new Money(1000).subtract(increment);
        Money expected = new Money(899.75); 
        Assert.assertEquals(expected, actual);
    }
}
