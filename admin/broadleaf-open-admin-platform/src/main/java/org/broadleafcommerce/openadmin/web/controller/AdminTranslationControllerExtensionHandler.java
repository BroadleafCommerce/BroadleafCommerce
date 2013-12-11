package org.broadleafcommerce.openadmin.web.controller;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.web.form.TranslationForm;

/**
 * @author Jeff Fischer
 */
public interface AdminTranslationControllerExtensionHandler extends ExtensionHandler {

    /**
     * Applies any necessary transformations to the given form. For example, some entity fields might need to be
     * mapped in a different way.
     *
     * @param form
     */
    public ExtensionResultStatusType applyTransformation(TranslationForm form);

}
