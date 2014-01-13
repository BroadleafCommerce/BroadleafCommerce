package org.broadleafcommerce.common.extensibility.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.Resource;


/**
 * Provides common functionality to all Broadleaf merge application contexts
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public abstract class AbstractMergeXMLApplicationContext extends AbstractXmlApplicationContext {

    protected Resource[] configResources;
    
    protected Resource[] getConfigResources() {
        return this.configResources;
    }
    
    public AbstractMergeXMLApplicationContext(ApplicationContext parent) {
        super(parent);
    }

}
