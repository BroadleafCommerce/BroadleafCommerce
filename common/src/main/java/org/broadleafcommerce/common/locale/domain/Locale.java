/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.locale.domain;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;

import java.io.Serializable;

/**
 * Created by jfischer
 */
public interface Locale extends Serializable {

    String getLocaleCode();

    void setLocaleCode(String localeCode);
    
    public java.util.Locale getJavaLocale();

    public String getFriendlyName();

    public void setFriendlyName(String friendlyName);

    public void setDefaultFlag(Boolean defaultFlag);

    public Boolean getDefaultFlag();

    public BroadleafCurrency getDefaultCurrency();

    public void setDefaultCurrency(BroadleafCurrency currency);

    /**
     * If true then the country portion of the locale will be used when building the search index.
     * If null or false then only the language will be used.
     * 
     * For example, if false, a locale of en_US will only index the results based
     * on the root of "en".
     * 
     * @return
     */
    public Boolean getUseCountryInSearchIndex();
    
    /**
     * Sets whether or not to use the country portion of the locale in the search index.
     * @param useInSearchIndex
     */
    public void setUseCountryInSearchIndex(Boolean useInSearchIndex);

}
