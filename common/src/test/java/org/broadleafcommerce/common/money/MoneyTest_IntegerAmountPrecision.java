package org.broadleafcommerce.common.money;

import java.math.BigDecimal;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MoneyTest_IntegerAmountPrecision extends TestCase {
    public void testCreateMoney_FromInteger_ZeroDollar(){
        Money money = new Money(0);
        BigDecimal expected = new BigDecimal(0).setScale(2); 
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_FromInteger_OneDollar(){
        Money money = new Money(1);
        BigDecimal expected = new BigDecimal(1).setScale(2);
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_FromNegativeInteger_OneDollar(){
        Money money = new Money(-1);
        BigDecimal expected = new BigDecimal(-1).setScale(2);
        Assert.assertEquals(expected, money.getAmount());
    }
  
    public void testCreateMoney_FromInteger_TenDollar(){
        Money money = new Money(10);
        BigDecimal expected = new BigDecimal(10).setScale(2);
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_FromInteger_HundredDollar(){
        Money money = new Money(100);
        BigDecimal expected = new BigDecimal(100).setScale(2);
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_FromInteger_TenThousandsDollar(){
        Money money = new Money(10000);
        BigDecimal expected = new BigDecimal(10000).setScale(2);
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_FromInteger_TenMillionsDollar(){
        Money money = new Money(10000000);
        BigDecimal expected = new BigDecimal(10000000).setScale(2);
        Assert.assertEquals(expected, money.getAmount());
    }
}
