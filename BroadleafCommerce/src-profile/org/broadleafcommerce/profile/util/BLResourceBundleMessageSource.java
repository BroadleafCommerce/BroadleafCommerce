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
