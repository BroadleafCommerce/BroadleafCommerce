package org.broadleafcommerce.common.money;

import java.math.BigDecimal;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MoneyTest_NegativeAmount extends TestCase {
    public void testCreateMoney_FromInteger_NegativeInteger(){
        Money actual = new Money(-1);
        BigDecimal expected = new BigDecimal(-1).setScale(2); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromInteger_NegativeLong(){
        Money actual = new Money(-1L);
        BigDecimal expected = new BigDecimal(-1L).setScale(2); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromInteger_NegativeDouble_Case1(){
        Money actual = new Money(-1.00d);
        BigDecimal expected = new BigDecimal(-1.00d).setScale(2); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromInteger_NegativeDouble_Case2(){
        Money actual = new Money(-1.55d);
        BigDecimal expected = new BigDecimal(-1.55d).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromInteger_NegativeDouble_ExpectRoundingUp(){
        Money actual = new Money(-1.555d); // digit to the left is odd, hence expect ROUND_HALF_UP to -1.54. But no rounding happens.
        BigDecimal expected = new BigDecimal(-1.555d).setScale(3, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromInteger_NegativeDouble_ExpectRoundingDown(){
        Money actual = new Money(-1.585d); // digit to the left is odd, hence expect ROUND_HALF_DOWN to -1.58. But no rounding happens.
        BigDecimal expected = new BigDecimal(-1.585d).setScale(3, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, actual.getAmount());
    }
}
