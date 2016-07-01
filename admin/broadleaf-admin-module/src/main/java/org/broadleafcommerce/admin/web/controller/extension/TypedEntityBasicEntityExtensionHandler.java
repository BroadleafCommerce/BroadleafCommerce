package org.broadleafcommerce.admin.web.controller.extension;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.admin.domain.TypedEntity;
import org.broadleafcommerce.common.dao.GenericEntityDao;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.web.controller.AbstractAdminAbstractControllerExtensionHandler;
import org.broadleafcommerce.openadmin.web.controller.AdminAbstractControllerExtensionManager;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.springframework.stereotype.Component;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Adds special behavior specific for Typed Entities during any entity persistence flows.
 * @author Jon Fleschler (jfleschler)
 */
@Component("blTypedEntityBasicEntityExtensionHandler")
public class TypedEntityBasicEntityExtensionHandler extends AbstractAdminAbstractControllerExtensionHandler {
    protected static final Log LOG = LogFactory.getLog(TypedEntityBasicEntityExtensionHandler.class);

    @Resource(name = "blAdminAbstractControllerExtensionManager")
    protected AdminAbstractControllerExtensionManager extensionManager;

    @Resource(name = "blGenericEntityDao")
    protected GenericEntityDao genericEntityDao;

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }

    /**
     * This allows us to set the Type on the entityForm before the entity is first persisted.
     * @param entityForm
     * @param cmd
     * @param pathVars
     * @return
     */
    @Override
    public ExtensionResultStatusType modifyPreAddEntityForm(EntityForm entityForm, ClassMetadata cmd, Map<String, String> pathVars) {
        Class<?> implClass = genericEntityDao.getCeilingImplClass(cmd.getCeilingType());
        if (TypedEntity.class.isAssignableFrom(implClass)) {
            // Set the Type on the Add entity form
            String type = getDefaultType(implClass);

            String sectionKey = entityForm.getSectionKey();
            int typeIndex = sectionKey.indexOf(":");
            if (typeIndex > 0) {
                type = sectionKey.substring(typeIndex + 1).toUpperCase();
            }

            String typedFieldName = getTypeFieldName(implClass);
            if (typedFieldName != null) {
                entityForm.findField(typedFieldName).setValue(type);
            }
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    protected String getDefaultType(Class implClass) {
        try {
            return ((TypedEntity) implClass.newInstance()).getDefaultType();
        } catch (Exception e) {
            return null;
        }
    }

    protected String getTypeFieldName(Class implClass) {
        try {
            return ((TypedEntity) implClass.newInstance()).getTypeFieldName();
        } catch (Exception e) {
            return null;
        }
    }
}
