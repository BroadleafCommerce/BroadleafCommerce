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
package org.broadleafcommerce.common.locale.dao;

import org.broadleafcommerce.common.locale.domain.Locale;

import java.util.List;

/**
 * Created by bpolster.
 */
public interface LocaleDao {

    /**
     * @return The locale for the passed in code
     */
    public Locale findLocaleByCode(String localeCode);

    /**
     * Returns the page template with the passed in id.
     *
     * @return The default locale
     */
    public Locale findDefaultLocale();

    /**
     * Returns all supported BLC locales.
     * @return
     */
    public List<Locale> findAllLocales();
    
    public Locale save(Locale locale);

}
