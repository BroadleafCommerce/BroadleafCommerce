package org.broadleafcommerce.common.money;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MoneyTest_IllegalStringAmount extends TestCase {
    public void testCreateMoney_FromNonNumericString(){
        try{
            new Money("Some Text!");
            Assert.fail("Expected exception not thrown");
        } catch(NumberFormatException e){
            Assert.assertTrue(true);
        }
    }
    
    public void testCreateMoney_FromNumericAndNonNumericString(){
        try{
            new Money("A few text and 1234");
            Assert.fail("Expected exception not thrown");
        } catch(NumberFormatException e){
            Assert.assertTrue(true);
        }
    }
    
    public void testCreateMoney_FromString_SpecialCharacters(){
        try{
            new Money("$%^&*");
            Assert.fail("Expected exception not thrown");
        } catch(NumberFormatException e){
            Assert.assertTrue(true);
        }
    }
}
