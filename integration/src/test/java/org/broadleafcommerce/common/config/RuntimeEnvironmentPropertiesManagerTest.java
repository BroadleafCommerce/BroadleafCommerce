/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.config;

import org.broadleafcommerce.test.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.annotation.Resource;

public class RuntimeEnvironmentPropertiesManagerTest extends BaseTest {

    @Resource(name = "blConfigurationManager")
    RuntimeEnvironmentPropertiesManager configurationManager;

    @Test
    public void testPropertyOnly() throws Exception {
        String s = configurationManager.getProperty("detect.sequence.generator.inconsistencies");
        if(s.indexOf("$")>=0) {
            Assert.fail("RuntimeEnvironmentPropertiesManager bean not defined");
        }
        
    }
    @Test(dependsOnMethods={"testPropertyOnly"})
    public void testPrefix() throws Exception {
        configurationManager.setPrefix("detect");
        String s = configurationManager.getProperty("sequence.generator.inconsistencies");
        if(s.indexOf("$")>=0) {
            Assert.fail("RuntimeEnvironmentPropertiesManager bean not defined");
        }
    }
    @Test(dependsOnMethods={"testPrefix"})
    public void testSuffix() throws Exception {
        
        String s = configurationManager.getProperty("sequence.generator","inconsistencies");
        if(s.indexOf("$")>=0) {
            Assert.fail("RuntimeEnvironmentPropertiesManager bean not defined");
        }
    }
    @Test(dependsOnMethods={"testSuffix"})
    public void testNullSuffix() throws Exception {
        configurationManager.setPrefix("detect");
        String s = configurationManager.getProperty("sequence.generator.inconsistencies", "SOMETHING");
        Assert.assertNotNull(s);
    }
    @Test
    public void testNULL() throws Exception {

        String s = configurationManager.getProperty(null, "SOMETHING");
  
        Assert.assertEquals(s, null);
    }
}
