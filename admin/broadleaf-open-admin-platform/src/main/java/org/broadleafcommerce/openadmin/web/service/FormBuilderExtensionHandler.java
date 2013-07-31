package org.broadleafcommerce.openadmin.web.service;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;

public interface FormBuilderExtensionHandler extends ExtensionHandler {

    public ExtensionResultStatusType addFormExtensions(EntityForm ef);

}
