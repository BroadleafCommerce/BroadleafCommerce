package org.broadleafcommerce.common.money;

import java.math.BigDecimal;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MoneyTest_LongAmountPrecision extends TestCase {
    public void testCreateMoney_FromLong_ZeroDollar(){
        Money money = new Money(0L);
        BigDecimal expected = new BigDecimal(0L).setScale(2); 
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_FromLong_OneDollar(){
        Money money = new Money(1L);
        BigDecimal expected = new BigDecimal(1L).setScale(2);
        Assert.assertEquals(expected, money.getAmount());
    }
  
    public void testCreateMoney_FromLong_TenDollar(){
        Money money = new Money(10L);
        BigDecimal expected = new BigDecimal(10L).setScale(2);
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_FromLong_HundredDollar(){
        Money money = new Money(100L);
        BigDecimal expected = new BigDecimal(100L).setScale(2);
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_FromLong_TenThousandsDollar(){
        Money money = new Money(10000L);
        BigDecimal expected = new BigDecimal(10000L).setScale(2);
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_FromLong_TenMillionsDollar(){
        Money money = new Money(10000000L);
        BigDecimal expected = new BigDecimal(10000000L).setScale(2);
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_FromLong_TenBillionsDollar(){
        Money money = new Money(10000000000L);
        BigDecimal expected = new BigDecimal(10000000000L).setScale(2);
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_FromLong_OneHundredBillionsDollar(){
        Money money = new Money(1000000000000L);
        BigDecimal expected = new BigDecimal(1000000000000L).setScale(2);
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_FromLong_OneThousandBillionsDollar(){
        Money money = new Money(1000000000000000L);
        BigDecimal expected = new BigDecimal(1000000000000000L).setScale(2);
        Assert.assertEquals(expected, money.getAmount());
    }
}
