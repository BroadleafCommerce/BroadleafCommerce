package org.broadleafcommerce.common.money;

import java.math.BigDecimal;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MoneyTest_DoubleAmountPrecisionWithRounding extends TestCase {
    public void testCreateMoney_OneDecimalPrecision(){
        Money money = new Money(10.5);
        BigDecimal expected = new BigDecimal(10.5).setScale(2); 
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_TwoDecimalsPrecision(){
        Money money = new Money(10.50);
        BigDecimal expected = new BigDecimal(10.50).setScale(2); 
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_ThreeDecimalsPrecision(){
        Money money = new Money(10.505);
        BigDecimal expected = new BigDecimal(10.50).setScale(2); 
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_ThreeDecimalsPrecision_RoundDown(){
        Money money = new Money(10.501);
        BigDecimal expected = new BigDecimal(10.50).setScale(2); 
        Assert.assertEquals(expected, money.getAmount());
    }

    public void testCreateMoney_ThreeDecimalsPrecision_RoundUp(){
        Money money = new Money(10.506);
        BigDecimal expected = new BigDecimal(10.51).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_ThreeDecimalsPrecision_RoundDownEvenDigit_Case1(){
        Money money = new Money(10.505); // digit to the left is even, hence ROUND_HALF_DOWN to 10.50
        BigDecimal expected = new BigDecimal(10.50).setScale(2); 
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_ThreeDecimalsPrecision_RoundDownEvenDigit_Case2(){
        Money money = new Money(10.525); // digit to the left is even, hence ROUND_HALF_DOWN to 10.51
        BigDecimal expected = new BigDecimal(10.52).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_ThreeDecimalsPrecision_RoundDownEvenDigit_Case3(){
        Money money = new Money(10.565); // digit to the left is even, hence ROUND_HALF_DOWN to 10.56
        BigDecimal expected = new BigDecimal(10.56).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_ThreeDecimalsPrecision_RoundUpOddDigit_Case1(){
        Money money = new Money(10.515); // digit to the left is odd, hence ROUND_HALF_UP to 10.52
        BigDecimal expected = new BigDecimal(10.52).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_ThreeDecimalsPrecision_RoundUpOddDigit_Case2(){
        Money money = new Money(10.535); // digit to the left is odd, hence ROUND_HALF_UP to 10.54
        BigDecimal expected = new BigDecimal(10.54).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, money.getAmount());
    }
    
    public void testCreateMoney_ThreeDecimalsPrecision_RoundUpOddDigit_Case3(){
        Money money = new Money(10.555); // digit to the left is odd, hence ROUND_HALF_UP to 10.56
        BigDecimal expected = new BigDecimal(10.56).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, money.getAmount());
    }
}
