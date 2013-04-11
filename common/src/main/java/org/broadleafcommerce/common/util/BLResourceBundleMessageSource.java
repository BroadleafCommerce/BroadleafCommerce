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

package org.broadleafcommerce.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated use {@link BroadleafMergeResourceBundleMessageSource} instead
 */
@Deprecated
public class BLResourceBundleMessageSource extends ResourceBundleMessageSource implements InitializingBean {

    private static final Log LOG = LogFactory.getLog(BLResourceBundleMessageSource.class);

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

    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.fatal("***INCORRECT CONFIGURATION***\n" +
                "This class should no longer be used as it does not merge property files together. If this is being used\n" +
                "in the admin application then this configuration is definitely an error as no properties will be resolved.\n" +
                "It is possible that the frontend application is not seriously effected by using this class but you should\n" +
                "modify your configuration to instead use org.broadleafcommerce.common.util.BroadleafMergeResourceBundleMessageSource\n" +
                "instead as soon as possible.");
    }

}
