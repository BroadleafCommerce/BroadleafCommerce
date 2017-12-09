package org.broadleafcommerce.core.web.linkeddata.generator;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author Nathan Moore (nathanmoore).
 */
@Service("blDefaultLinkedDataGeneratorExtensionHandler")
public class DefaultLinkedDataGeneratorExtensionHandler extends AbstractLinkedDataGeneratorExtensionHandler 
        implements LinkedDataGeneratorExtensionHandler {
    @Resource(name = "blLinkedDataGeneratorExtensionManager")
    protected LinkedDataGeneratorExtensionManager extensionManager;
    
    @PostConstruct
    public void init() {
        extensionManager.registerHandler(this);
    }
    
    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }
}
