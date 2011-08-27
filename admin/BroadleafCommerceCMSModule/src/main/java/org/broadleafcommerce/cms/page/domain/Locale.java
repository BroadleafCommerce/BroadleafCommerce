package org.broadleafcommerce.cms.page.domain;

import java.io.Serializable;

/**
 * Created by jfischer
 */
public interface Locale extends Serializable {

    Long getId();

    void setId(Long id);

    String getLocaleCode();

    void setLocaleCode(String localeCode);

    String getLocaleName();

    void setLocaleName(String localeName);

    public String getFriendlyName();

    public void setFriendlyName(String friendlyName);

}
