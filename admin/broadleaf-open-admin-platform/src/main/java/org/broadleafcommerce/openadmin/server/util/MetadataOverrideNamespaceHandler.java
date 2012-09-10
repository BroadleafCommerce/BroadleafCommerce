package org.broadleafcommerce.openadmin.server.util;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author Jeff Fischer
 */
public class MetadataOverrideNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("mo", new MetadataOverrideBeanDefinitionParser());
    }

}
