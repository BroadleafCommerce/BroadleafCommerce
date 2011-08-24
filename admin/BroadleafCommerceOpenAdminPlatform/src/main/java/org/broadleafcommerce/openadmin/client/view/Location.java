package org.broadleafcommerce.openadmin.client.view;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/24/11
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class Location {

    protected String localeCode;
    protected String localeName;

    public Location() {
        //do nothing
    }

    public Location(String localeCode, String localeName) {
        this.localeCode = localeCode;
        this.localeName = localeName;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public String getLocaleName() {
        return localeName;
    }

    public void setLocaleName(String localeName) {
        this.localeName = localeName;
    }
}
