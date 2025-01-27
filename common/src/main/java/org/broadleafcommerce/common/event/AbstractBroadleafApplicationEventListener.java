/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.common.event;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.service.BroadleafCurrencyService;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.service.SiteService;
import org.broadleafcommerce.common.util.tenant.IdentityExecutionUtils;
import org.broadleafcommerce.common.util.tenant.IdentityOperation;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.TimeZone;

/**
 * This abstract class contains the plumbing that sets up the context for handling a {@code BroadleafApplicationEvent}.
 *
 * @author Nick Crum ncrum
 */
public abstract class AbstractBroadleafApplicationEventListener<T extends BroadleafApplicationEvent>
        implements BroadleafApplicationListener<T> {

    @Autowired
    @Qualifier("blSiteService")
    protected SiteService siteService;

    @Autowired
    @Qualifier("blCurrencyService")
    protected BroadleafCurrencyService currencyService;

    @Autowired
    @Qualifier("blLocaleService")
    protected LocaleService localeService;

    protected abstract void handleApplicationEvent(T event);

    @Override
    public final void onApplicationEvent(final T event) {
        Site site = getSite(event);
        Catalog catalog = getCatalog(event);
        Site profile = getProfile(event);

        IdentityExecutionUtils.runOperationByIdentifier(new IdentityOperation<Void, RuntimeException>() {
            @Override
            public Void execute() throws RuntimeException {
                BroadleafRequestContext ctx = BroadleafRequestContext.getBroadleafRequestContext();
                TimeZone origTimeZone = ctx.getTimeZone();
                Locale origLocale = ctx.getLocale();
                BroadleafCurrency origCurrency = ctx.getBroadleafCurrency();
                try {
                    ctx.setTimeZone(getTimeZone(event));
                    ctx.setLocale(getLocale(event));
                    ctx.setBroadleafCurrency(getCurrency(event));
                    handleApplicationEvent(event);
                } finally {
                    ctx.setTimeZone(origTimeZone);
                    ctx.setLocale(origLocale);
                    ctx.setBroadleafCurrency(origCurrency);
                }
                return null;
            }
        }, site, profile, catalog);
    }

    protected Site getSite(T event) {
        if (event.getSiteId() != null) {
            return siteService.retrieveNonPersistentSiteById(event.getSiteId());
        }
        return null;
    }

    protected Catalog getCatalog(T event) {
        if (event.getCatalogId() != null) {
            return siteService.findCatalogById(event.getCatalogId());
        }
        return null;
    }

    protected Site getProfile(T event) {
        if (event.getProfileId() != null) {
            return siteService.retrieveNonPersistentSiteById(event.getProfileId());
        }
        return null;
    }

    protected BroadleafCurrency getCurrency(T event) {
        if (event.getCurrencyCode() != null) {
            return currencyService.findCurrencyByCode(event.getCurrencyCode());
        }
        return null;
    }

    protected Locale getLocale(T event) {
        if (event.getLocaleCode() != null) {
            return localeService.findLocaleByCode(event.getLocaleCode());
        }
        return null;
    }

    protected TimeZone getTimeZone(T event) {
        if (event.getTimeZoneId() != null) {
            return TimeZone.getTimeZone(event.getTimeZoneId());
        }
        return null;
    }

}
