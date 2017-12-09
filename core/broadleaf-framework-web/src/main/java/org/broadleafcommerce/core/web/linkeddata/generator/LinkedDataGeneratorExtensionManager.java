package org.broadleafcommerce.core.web.linkeddata.generator;

import org.broadleafcommerce.common.extension.ExtensionManager;
import org.springframework.stereotype.Service;

/**
 * Manage extension points for {@link org.broadleafcommerce.core.web.linkeddata.generator.LinkedDataGenerator}s.
 * 
 * @author Nathan Moore (nathanmoore).
 */
@Service("blLinkedDataGeneratorExtensionManager")
public class LinkedDataGeneratorExtensionManager extends ExtensionManager<LinkedDataGeneratorExtensionHandler> {
    public LinkedDataGeneratorExtensionManager() {
        super(LinkedDataGeneratorExtensionHandler.class);
    }
}
