package org.broadleafcommerce.cms.locale.domain;

import java.io.Serializable;

/**
 * Created by jfischer
 */
public interface Locale extends Serializable {

    Long getId();

    void setId(Long id);

    String getLocaleCode();

    void setLocaleCode(String localeCode);

    public String getFriendlyName();

    public void setFriendlyName(String friendlyName);

    public void setDefaultFlag(Boolean defaultFlag);

    public Boolean getDefaultFlag();

}
