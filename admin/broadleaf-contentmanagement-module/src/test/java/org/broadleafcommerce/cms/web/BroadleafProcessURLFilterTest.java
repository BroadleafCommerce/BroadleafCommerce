/*-
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
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
package org.broadleafcommerce.cms.web;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;

/**
 * Created by bpolster.
 */
public class BroadleafProcessURLFilterTest {

    @Test
    public void testShouldProcessURL() throws Exception {
        BroadleafProcessURLFilter cf = new BroadleafProcessURLFilter();
        // Should fail
        AssertionErrors.assertFalse("Image resource should not be processed by content filter.", cf.shouldProcessURL(null, "/path/subpath/test.jpg"));
        AssertionErrors.assertFalse("URLs containing org.broadleafcommerce.admin should not be processed.", cf.shouldProcessURL(null, "/path/org.broadleafcommerce.admin/admintest"));
        AssertionErrors.assertTrue("/about_us should be processed by the content filter", cf.shouldProcessURL(null, "/about_us"));
        AssertionErrors.assertTrue("*.htm resources should be processed by the content filter", cf.shouldProcessURL(null, "/test.htm"));
    }

}
