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
package org.broadleafcommerce.common.locale.service;

import org.broadleafcommerce.common.locale.domain.Locale;

import java.util.List;

/**
 * Created by bpolster.
 */
public interface LocaleService {

    /**
     * @return the locale for the passed in code
     */
    public Locale findLocaleByCode(String localeCode);

    /**
     * @return the default locale
     */
    public Locale findDefaultLocale();

    /**
     * @return a list of all known locales
     */
    public List<Locale> findAllLocales();
    
    /**
     * Persists the given locale
     * 
     * @param locale
     * @return the persisted locale
     */
    public Locale save(Locale locale);
    
}
