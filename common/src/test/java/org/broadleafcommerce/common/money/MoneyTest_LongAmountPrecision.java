package org.broadleafcommerce.common.money;

import java.math.BigDecimal;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MoneyTest_LongAmountPrecision extends TestCase {
    public void testCreateMoney_FromLong_ZeroDollar(){
        Money actual = new Money(0L);
        BigDecimal expected = new BigDecimal(0L).setScale(2); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromLong_OneDollar(){
        Money actual = new Money(1L);
        BigDecimal expected = new BigDecimal(1L).setScale(2);
        Assert.assertEquals(expected, actual.getAmount());
    }
  
    public void testCreateMoney_FromLong_TenDollar(){
        Money actual = new Money(10L);
        BigDecimal expected = new BigDecimal(10L).setScale(2);
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromLong_HundredDollar(){
        Money actual = new Money(100L);
        BigDecimal expected = new BigDecimal(100L).setScale(2);
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromLong_TenThousandsDollar(){
        Money actual = new Money(10000L);
        BigDecimal expected = new BigDecimal(10000L).setScale(2);
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromLong_TenMillionsDollar(){
        Money actual = new Money(10000000L);
        BigDecimal expected = new BigDecimal(10000000L).setScale(2);
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromLong_TenBillionsDollar(){
        Money actual = new Money(10000000000L);
        BigDecimal expected = new BigDecimal(10000000000L).setScale(2);
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromLong_OneHundredBillionsDollar(){
        Money actual = new Money(1000000000000L);
        BigDecimal expected = new BigDecimal(1000000000000L).setScale(2);
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromLong_OneThousandBillionsDollar(){
        Money actual = new Money(1000000000000000L);
        BigDecimal expected = new BigDecimal(1000000000000000L).setScale(2);
        Assert.assertEquals(expected, actual.getAmount());
    }
}
