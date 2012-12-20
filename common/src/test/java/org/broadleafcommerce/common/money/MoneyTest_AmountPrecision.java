package org.broadleafcommerce.common.money;

import java.math.BigDecimal;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MoneyTest_AmountPrecision extends TestCase {
    public void testCreateMoney_FromInteger_ZeroDollar(){
        Money money = new Money(0);
        BigDecimal expected = new BigDecimal(0).setScale(2); 
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_FromInteger_NegativeDollar(){
        Money money = new Money(-1);
        BigDecimal expected = new BigDecimal(-1).setScale(2);
        Assert.assertEquals(expected.doubleValue(), money.getAmount().doubleValue());
    }
  
    public void testCreateMoney_FromInteger_OneDollar(){
        Money money = new Money(1);
        BigDecimal expected = new BigDecimal(1).setScale(2);
        Assert.assertEquals(expected, money.getAmount());
    }
  
    public void testCreateMoney_FromInteger_TenDollar(){
        Money money = new Money(10);
        BigDecimal expected = new BigDecimal(10).setScale(2);
        Assert.assertEquals(expected.doubleValue(), money.getAmount().doubleValue());
    }
}
