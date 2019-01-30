/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.test.common.context.override.entityconfig;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Resource;

/**
 * @author Nick Crum ncrum
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EntityConfigurationOverrideTestConfiguration.class)
public class EntityConfigurationOverrideTest {

    @Resource(name="blMergedEntityContexts")
    protected Set<String> mergedEntityContexts;

    @Test
    public void testMergedEntityContextsOrder() {
        Iterator<String> iterator = mergedEntityContexts.iterator();
        Assert.assertEquals("common", iterator.next());
        Assert.assertEquals("framework", iterator.next());
        Assert.assertEquals("local", iterator.next());
    }
}
