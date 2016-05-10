/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web;

import junit.framework.TestCase;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;

/**
 * 
 * @author jfischer
 *
 */
public class SpringTemporaryRedirectOverrideFilterTest extends TestCase {
    
    public void testFilter() throws Exception {
        SpringTemporaryRedirectOverrideFilter filter = new SpringTemporaryRedirectOverrideFilter();
        FilterConfig config = new FilterConfig() {
            
            public String getFilterName() {
                return null;
            }
            
            public String getInitParameter(String param) {
                return "category/temp.*\n stellar/test/tester another/small/test";
            }
            
            public Enumeration<String> getInitParameterNames() {
                return null;
            }

            public ServletContext getServletContext() {
                return null;
            }
        };
        filter.init(config);
        assertFalse(filter.isUrlMatch("nonsense/category/temp/mytest"));
        assertTrue(filter.isUrlMatch("category/temp/mytest"));
        assertTrue(filter.isUrlMatch("stellar/test/tester"));
    }

}
