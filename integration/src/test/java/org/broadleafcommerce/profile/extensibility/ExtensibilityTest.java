/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.profile.extensibility;

import org.broadleafcommerce.common.extensibility.context.MergeClassPathXMLApplicationContext;
import org.broadleafcommerce.test.TestNGSiteIntegrationSetup;
import org.testng.annotations.Test;

/**
 *
 * @author jfischer
 *
 */
public class ExtensibilityTest extends TestNGSiteIntegrationSetup {

    @Test
    public void test() {
        try {
            /*
             * First we load the merged application context
             */
            MergeClassPathXMLApplicationContext test = new MergeClassPathXMLApplicationContext(new String[]{"org/broadleafcommerce/extensibility/base/applicationContext-src.xml","org/broadleafcommerce/extensibility/base/applicationContext-src2.xml"}, new String[]{"org/broadleafcommerce/extensibility/override/applicationContext-patch1.xml","org/broadleafcommerce/extensibility/override/applicationContext-patch2.xml"}, null);

            ExtensibilityTestBean srcBean = (ExtensibilityTestBean) test.getBean("test3");
            if (!srcBean.getTestProperty().equals("test1")) {
                assert false;
            }
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
            logger.error(e);
            assert false;
        }
    }
}
