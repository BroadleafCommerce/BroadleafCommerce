package org.broadleafcommerce.common.security.service;

import org.broadleafcommerce.common.exception.ServiceException;

import junit.framework.TestCase;

public class ExpolitProtectionServiceTest extends TestCase {
    public void test(){
        ExploitProtectionServiceImpl service = new ExploitProtectionServiceImpl();
        service.setXssProtectionEnabled(true);
        try {
            String s = service.cleanStringWithResults("\"javascript:alert(0)");
        } catch (ServiceException e) {
            assertTrue(e instanceof CleanStringException);
        }
        try {
            String testString = "aaa";
            String s = service.cleanStringWithResults(testString);
            assertEquals(s, testString);
        } catch (ServiceException e) {
            fail("Exception is not expected");
        }
    }
}
