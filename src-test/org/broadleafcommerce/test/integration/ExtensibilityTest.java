package org.broadleafcommerce.test.integration;

import org.broadleafcommerce.extensibility.context.MergeClassPathXMLApplicationContext;
import org.broadleafcommerce.test.integration.extensibility.ExtensibilityTestBean;
import org.broadleafcommerce.test.integration.extensibility.ExtensibilityTestBean3;
import org.testng.annotations.Test;

public class ExtensibilityTest extends BaseTest {

	@Test
    public void test() {
        try {
        	/*
        	 * First we load the merged application context
        	 */
        	MergeClassPathXMLApplicationContext test = new MergeClassPathXMLApplicationContext(new String[]{"org/broadleafcommerce/test/integration/extensibility/base/applicationContext-src.xml"}, new String[]{"org/broadleafcommerce/test/integration/extensibility/override/applicationContext-patch1.xml","org/broadleafcommerce/test/integration/extensibility/override/applicationContext-patch2.xml"}, null);
        	/*
        	 * Test to make sure the first patch was applied. This patch involves merging in
        	 * new properties (and overriding a property and attribute) for a bean
        	 * with a given id.
        	 */
        	ExtensibilityTestBean bean1 = (ExtensibilityTestBean) test.getBean("test");
        	if (!bean1.getTestProperty().equals("test") || !bean1.getTestProperty2().equals("test2")) {
        		assert false;
        	}
        	/*
        	 * Test to make sure the second patch was applied. This patch involves adding
        	 * an entirely new bean to the source.
        	 */
        	ExtensibilityTestBean3 bean2 = (ExtensibilityTestBean3) test.getBean("test2");
        	if (!bean2.getTestProperty().equals("new") || !bean2.getTestProperty2().equals("none2") || !bean2.getTestProperty3().equals("none3")) {
        		assert false;
        	}
        } catch (Exception e) {
        	e.printStackTrace();
            assert false;
        }
    }
}
