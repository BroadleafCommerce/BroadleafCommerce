package org.broadleafcommerce.cms.web;

import junit.framework.TestCase;

/**
 * Created by bpolster.
 */
public class BroadleafProcessURLFilterTest extends TestCase {
    public void testShouldProcessURL() throws Exception {
        BroadleafProcessURLFilter cf = new BroadleafProcessURLFilter();
        // Should fail
        assertFalse("Image resource should not be processed by content filter.", cf.shouldProcessURL(null, "/path/subpath/test.jpg"));
        assertFalse("URLs containing org.broadleafcommerce.admin should not be processed.", cf.shouldProcessURL(null, "/path/org.broadleafcommerce.admin/admintest"));
        assertTrue("/about_us should be processed by the content filter", cf.shouldProcessURL(null, "/about_us"));
        assertTrue("*.htm resources should be processed by the content filter", cf.shouldProcessURL(null, "/test.htm"));
    }
}
