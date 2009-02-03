package org.broadleafcommerce.test.integration;

import org.testng.annotations.Test;

/**
 * Simple test for mapping and query syntax.
 * @author jjacobs
 */
public class OrmTest extends BaseTest {

    @Test
    public void testMappings() {
        try {
            getEntityManager();
        } catch (Exception e) {
            assert false;
        }
    }
}