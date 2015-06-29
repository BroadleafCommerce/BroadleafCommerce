package org.broadleafcommerce.openadmin.web.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.config.domain.SystemProperty;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component("blSystemPropertyFormBuilderExtensionHandler")
public class SystemPropertyFormBuilderExtensionHandler extends AbstractFormBuilderExtensionHandler {
    
    protected static final Log LOG = LogFactory.getLog(SystemPropertyFormBuilderExtensionHandler.class);
    
    @Resource(name = "blFormBuilderExtensionManager")
    protected FormBuilderExtensionManager extensionManager;
    
    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }
    
    
    @Override
    public ExtensionResultStatusType modifyUnpopulatedEntityForm(EntityForm ef) {
        try {
            if (SystemProperty.class.isAssignableFrom(Class.forName(ef.getCeilingEntityClassname()))) {
                ef.findField("value").setFieldType("system_property_value");
            }
            
            return ExtensionResultStatusType.HANDLED;
        } catch (ClassNotFoundException e) {
            LOG.warn("No class found for the given entity form, not modifying grid");
        }
        return ExtensionResultStatusType.NOT_HANDLED;
    }
}
