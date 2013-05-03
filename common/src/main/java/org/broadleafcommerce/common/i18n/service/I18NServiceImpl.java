package org.broadleafcommerce.common.i18n.service;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.Locale;


@Service("blI18NService")
public class I18NServiceImpl implements I18NService {
    protected static final Log LOG = LogFactory.getLog(I18NServiceImpl.class);
    
    @Override
    public String getTranslatedValue(Object entity, String property, Locale locale) {
        // Get the value of this property straight from the entity. We will fall back on this if we do not
        // find a translation for the property
        String entityPropertyValue = null;
        try {
            Object rawPropertyValue = PropertyUtils.getSimpleProperty(entity, property);
            if (rawPropertyValue != null) {
                entityPropertyValue = String.valueOf(rawPropertyValue);
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            LOG.warn(String.format("Unable to find a value for property %s in object %s, %s", property, entity, msg));
        }
        
        // TODO: Attempt to translate the property if we're not in the default locale
        
        return entityPropertyValue;
    }

}