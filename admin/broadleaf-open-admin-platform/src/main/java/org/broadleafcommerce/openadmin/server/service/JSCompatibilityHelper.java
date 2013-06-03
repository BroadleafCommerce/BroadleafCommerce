package org.broadleafcommerce.openadmin.server.service;

/**
 * @author Jeff Fischer
 */
public class JSCompatibilityHelper {

    public static String encodeFieldName(String name) {
        return name.replace(".", "__");
    }

    public static String unEncodeFieldname(String name) {
        return name.replace("__", ".");
    }

}
