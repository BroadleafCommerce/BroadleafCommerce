package org.broadleafcommerce.common.context.override.config;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Nick Crum ncrum
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = XMLImportOverrideConfiguration2.class)
public class XMLImportOverrideTest2 {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Test
    public void testOverride2() {
        Assert.assertEquals(NoOpPasswordEncoder.class, passwordEncoder.getClass());
    }
}
