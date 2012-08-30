package org.broadleafcommerce.openadmin.client.view.dynamic.form;

/**
 * @author Jeff Fischer
 */
public class FormManager {

    private static FormManager formManager = null;

    public static FormManager getInstance() {
        if (formManager == null) {
            formManager = new FormManager();
        }

        return formManager;
    }
}
