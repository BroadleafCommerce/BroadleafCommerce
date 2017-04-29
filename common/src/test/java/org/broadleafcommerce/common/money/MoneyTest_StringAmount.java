package org.broadleafcommerce.common.money;

import java.math.BigDecimal;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MoneyTest_StringAmount extends TestCase {
    public void testCreateMoney_FromString_OneDecimalPrecision(){
        Money actual = new Money("1.5");
        BigDecimal expected = new BigDecimal(1.5).setScale(2); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromString_TwoDecimalsPrecision_Case1(){
        Money actual = new Money("1.50");
        BigDecimal expected = new BigDecimal(1.50).setScale(2); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromString_TwoDecimalsPrecision_Case2(){
        Money actual = new Money("1.55");
        BigDecimal expected = new BigDecimal(1.55).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromString_ThreeDecimalsPrecision_ExpectRoundUp(){
        Money actual = new Money("1.555"); // digit to the left is odd, hence ROUND_HALF_UP to 1.56
        BigDecimal expected = new BigDecimal(1.56).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromString_ThreeDecimalsPrecision_ExpectRoundDown(){
        Money actual = new Money("1.525"); // digit to the left is even, hence ROUND_HALF_DOWN to 1.52
        BigDecimal expected = new BigDecimal(1.52).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromString_ZeroDollar(){
        Money actual = new Money("0");
        BigDecimal expected = new BigDecimal(0).setScale(2); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromString_OneDollar(){
        Money actual = new Money("1");
        BigDecimal expected = new BigDecimal(1).setScale(2); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromString_TenDollar(){
        Money actual = new Money("10");
        BigDecimal expected = new BigDecimal(10).setScale(2); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromString_OneHundredDollar(){
        Money actual = new Money("100");
        BigDecimal expected = new BigDecimal(100).setScale(2); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromString_OneThousandDollar(){
        Money actual = new Money("1000");
        BigDecimal expected = new BigDecimal(1000).setScale(2); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromString_OneMillionDollar(){
        Money actual = new Money("1000000");
        BigDecimal expected = new BigDecimal(1000000).setScale(2); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_FromString_OneBillionDollar(){
        Money actual = new Money("1000000000");
        BigDecimal expected = new BigDecimal(1000000000).setScale(2); 
        Assert.assertEquals(expected, actual.getAmount());
    }
}
