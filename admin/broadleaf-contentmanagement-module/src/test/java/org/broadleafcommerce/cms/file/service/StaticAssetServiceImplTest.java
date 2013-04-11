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

package org.broadleafcommerce.cms.file.service;

import junit.framework.TestCase;

/**
 * Created by bpolster.
 */
public class StaticAssetServiceImplTest extends TestCase {
    
    public void testConvertURLProperties() throws Exception {
        StaticAssetServiceImpl staticAssetService = new StaticAssetServiceImpl();
        staticAssetService.setStaticAssetUrlPrefix("cmsstatic");
        staticAssetService.setStaticAssetEnvironmentUrlPrefix("http://images.mysite.com/myapp/cmsstatic");
        
        String url = staticAssetService.convertAssetPath("/cmsstatic/product.jpg","myapp", false);
        assertTrue(url.equals("http://images.mysite.com/myapp/cmsstatic/product.jpg"));

        staticAssetService.setStaticAssetEnvironmentUrlPrefix("http://images.mysite.com");
        url = staticAssetService.convertAssetPath("/cmsstatic/product.jpg","myapp", false);
        assertTrue(url.equals("http://images.mysite.com/product.jpg"));

        url = staticAssetService.convertAssetPath("/cmsstatic/product.jpg","myapp", true);
        assertTrue(url.equals("https://images.mysite.com/product.jpg"));


        staticAssetService.setStaticAssetEnvironmentUrlPrefix(null);
        url = staticAssetService.convertAssetPath("/cmsstatic/product.jpg","myapp", true);
        assertTrue(url.equals("/myapp/cmsstatic/product.jpg"));

        url = staticAssetService.convertAssetPath("cmsstatic/product.jpg","myapp", true);
        assertTrue(url.equals("/myapp/cmsstatic/product.jpg"));

    }
}
