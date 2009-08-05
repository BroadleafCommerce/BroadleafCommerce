/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.profile.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.support.ResourceBundleMessageSource;

public class BLResourceBundleMessageSource extends ResourceBundleMessageSource {

    public BLResourceBundleMessageSource(String[] basenames, ResourceBundleExtensionPoint resourceBundleExtensionPoint) {
        super();
        List<String> bundles = new ArrayList<String>();
        if (resourceBundleExtensionPoint != null) {
            String[] bundleNames = resourceBundleExtensionPoint.getBasenameExtensions();
            if (bundleNames != null) {
                for (int i = 0; i < bundleNames.length; i++) {
                    bundles.add(bundleNames[i]);
                }
            }
            if (basenames != null) {
                for (int i = 0; i < basenames.length; i++) {
                    bundles.add(basenames[i]);
                }
            }
        }
        setBasenames(bundles.toArray(new String[0]));
    }
}
