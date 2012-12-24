package org.broadleafcommerce.common.money.arithmetic;

import java.math.BigDecimal;

import org.broadleafcommerce.common.money.Money;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MoneyArithmeticTest_SameCurrencyDivisions extends TestCase {
    public void testMoneyArithmetic_DivideZeroDollar(){
        try{
            BigDecimal increment = new BigDecimal(0).setScale(2);
            new Money(10).divide(increment);
            Assert.fail("Expected divide-by-zero arithmetic exception didn't occur");
        } catch(ArithmeticException e){
            Assert.assertTrue(true);
        }
    }
    
    public void testMoneyArithmetic_DivideOneDollar(){
        BigDecimal increment = new BigDecimal(1.00).setScale(2);
        Money actual = new Money(10).divide(increment);
        Money expected = new Money(10); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_DivideFiveDollars(){
        BigDecimal increment = new BigDecimal(5.00).setScale(2);
        Money actual = new Money(10).divide(increment);
        Money expected = new Money(2); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_DivideTenDollars(){
        BigDecimal increment = new BigDecimal(10.00).setScale(2);
        Money actual = new Money(10).divide(increment);
        Money expected = new Money(1); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_DivideOneHundredDollars(){
        BigDecimal increment = new BigDecimal(100).setScale(2);
        Money actual = new Money(10).divide(increment);
        Money expected = new Money(0.1); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_DivideOneThousandDollars(){
        BigDecimal increment = new BigDecimal(1000).setScale(2);
        Money actual = new Money(10).divide(increment);
        Money expected = new Money(0.01); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_DivideNegativeOneThousandDollars(){
        BigDecimal increment = new BigDecimal(-1000).setScale(2);
        Money actual = new Money(10).divide(increment);
        Money expected = new Money(-0.01); 
        Assert.assertEquals(expected, actual);
    }
}
