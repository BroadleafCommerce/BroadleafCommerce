package org.broadleafcommerce.common.money.arithmetic;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.broadleafcommerce.common.money.Money;

public class MoneyArithmeticTest_SameCurrencyAdditions extends TestCase {
    public void testMoneyArithmetic_AddZeroDollar(){
        Money increment = new Money(0);
        Money actual = new Money(10).add(increment);
        Money expected = new Money(10); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_AddOneDollar(){
        Money increment = new Money(1);
        Money actual = new Money(10).add(increment);
        Money expected = new Money(11); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_AddTenDollar(){
        Money increment = new Money(10);
        Money actual = new Money(10).add(increment);
        Money expected = new Money(20); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_AddOneHundredDollar(){
        Money increment = new Money(100);
        Money actual = new Money(10).add(increment);
        Money expected = new Money(110); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_AddOneThousandDollar(){
        Money increment = new Money(1000);
        Money actual = new Money(10).add(increment);
        Money expected = new Money(1010); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_AddPenny(){
        Money increment = new Money(0.01);
        Money actual = new Money(10).add(increment);
        Money expected = new Money(10.01); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_AddDime(){
        Money increment = new Money(0.10);
        Money actual = new Money(10).add(increment);
        Money expected = new Money(10.10); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_AddADollarAndFiveCents(){
        Money increment = new Money(1.05);
        Money actual = new Money(10).add(increment);
        Money expected = new Money(11.05); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_AddNegativeDollar(){
        Money increment = new Money(-1.00);
        Money actual = new Money(10).add(increment);
        Money expected = new Money(9.00); 
        Assert.assertEquals(expected, actual);
    }
    
    public void testMoneyArithmetic_AddNegativeDollarAndFiftyCents(){
        Money increment = new Money(-1.50);
        Money actual = new Money(10).add(increment);
        Money expected = new Money(8.5); 
        Assert.assertEquals(expected, actual);
    }
}
