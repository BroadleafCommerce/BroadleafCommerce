package org.broadleafcommerce.common.money;

import java.math.BigDecimal;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MoneyTest_DoubleAmountPrecisionWithRounding extends TestCase {
    public void testCreateMoney_OneDecimalPrecision(){
        Money actual = new Money(10.5);
        BigDecimal expected = new BigDecimal(10.5).setScale(2); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_TwoDecimalsPrecision_Case1(){
        Money actual = new Money(10.50);
        BigDecimal expected = new BigDecimal(10.50).setScale(2); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_TwoDecimalsPrecision_Case2(){
        Money actual = new Money(100.58);
        BigDecimal expected = new BigDecimal(100.58).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_TwoDecimalsPrecision_Case3(){
        Money actual = new Money(567.25);
        BigDecimal expected = new BigDecimal(567.25).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_TwoDecimalsPrecision_Case4(){
        Money actual = new Money(1200.88);
        BigDecimal expected = new BigDecimal(1200.88).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_TwoDecimalsPrecision_Case5(){
        Money actual = new Money(9980.28);
        BigDecimal expected = new BigDecimal(9980.28).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_TwoDecimalsPrecision_Case6(){
        Money actual = new Money(29980.22);
        BigDecimal expected = new BigDecimal(29980.22).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_ThreeDecimalsPrecision(){
        Money actual = new Money(10.505);
        BigDecimal expected = new BigDecimal(10.50).setScale(2); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_ThreeDecimalsPrecision_RoundDown(){
        Money actual = new Money(10.501);
        BigDecimal expected = new BigDecimal(10.50).setScale(2); 
        Assert.assertEquals(expected, actual.getAmount());
    }

    public void testCreateMoney_ThreeDecimalsPrecision_RoundUp(){
        Money actual = new Money(10.506);
        BigDecimal expected = new BigDecimal(10.51).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_ThreeDecimalsPrecision_RoundDownEvenDigit_Case1(){
        Money actual = new Money(10.505); // digit to the left is even, hence ROUND_HALF_DOWN to 10.50
        BigDecimal expected = new BigDecimal(10.50).setScale(2); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_ThreeDecimalsPrecision_RoundDownEvenDigit_Case2(){
        Money actual = new Money(10.525); // digit to the left is even, hence ROUND_HALF_DOWN to 10.51
        BigDecimal expected = new BigDecimal(10.52).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_ThreeDecimalsPrecision_RoundDownEvenDigit_Case3(){
        Money actual = new Money(10.565); // digit to the left is even, hence ROUND_HALF_DOWN to 10.56
        BigDecimal expected = new BigDecimal(10.56).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_ThreeDecimalsPrecision_RoundUpOddDigit_Case1(){
        Money actual = new Money(10.515); // digit to the left is odd, hence ROUND_HALF_UP to 10.52
        BigDecimal expected = new BigDecimal(10.52).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_ThreeDecimalsPrecision_RoundUpOddDigit_Case2(){
        Money actual = new Money(10.535); // digit to the left is odd, hence ROUND_HALF_UP to 10.54
        BigDecimal expected = new BigDecimal(10.54).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, actual.getAmount());
    }
    
    public void testCreateMoney_ThreeDecimalsPrecision_RoundUpOddDigit_Case3(){
        Money actual = new Money(10.555); // digit to the left is odd, hence ROUND_HALF_UP to 10.56
        BigDecimal expected = new BigDecimal(10.56).setScale(2, BigDecimal.ROUND_HALF_DOWN); 
        Assert.assertEquals(expected, actual.getAmount());
    }
}
