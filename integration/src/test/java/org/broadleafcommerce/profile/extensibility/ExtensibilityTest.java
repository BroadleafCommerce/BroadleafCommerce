/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.profile.extensibility;

import org.broadleafcommerce.common.extensibility.context.MergeClassPathXMLApplicationContext;
import org.broadleafcommerce.test.BaseTest;
import org.testng.annotations.Test;

/**
 *
 * @author jfischer
 *
 */
public class ExtensibilityTest extends BaseTest {

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
