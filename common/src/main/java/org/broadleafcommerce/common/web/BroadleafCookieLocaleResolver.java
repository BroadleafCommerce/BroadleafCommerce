package org.broadleafcommerce.common.web;

import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Specific Spring component to override the default behavior of {@link CookieLocaleResolver} so that the default Broadleaf
 * Locale looked up in the database is used. This should be hooked up in applicationContext-servlet.xml in place of Spring's
 * {@link CookieResolver}.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link BroadleafLocaleResolverImpl}
 */
public class BroadleafCookieLocaleResolver extends CookieLocaleResolver {

    @Resource(name = "blLocaleService")
    private LocaleService localeService;
    
    @Override
    protected java.util.Locale determineDefaultLocale(HttpServletRequest request) {
        java.util.Locale defaultLocale = getDefaultLocale();
        if (defaultLocale == null) {
            Locale defaultBroadleafLocale = localeService.findDefaultLocale();
            if (defaultBroadleafLocale == null) {
                return super.determineDefaultLocale(request);
            } else {
                return BroadleafRequestContext.convertLocaleToJavaLocale(defaultBroadleafLocale);
            }
        }
        return defaultLocale;
    }
    
}
