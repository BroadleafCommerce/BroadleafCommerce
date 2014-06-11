/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.config.service.MessageSourceService;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ResourceLoader;
import java.text.MessageFormat;
import java.util.Locale;
import javax.annotation.Resource;


/**
 * <p>Replaces the deprecated {@link BLResourceBundleMessageSource} by using a {@link ReloadableResourceBundleMessageSource}
 * instead. The main advantage of using this is the out-of-the-box ability to merge multiple property files together. There
 * is one important difference: When there is a conflict for a property (declared in multiple files) this implementation
 * assumes that the <i>later</i> one in the list takes precedence. This follows with normal Broadleaf assumptions that bean
 * definitions declared later in the merge process win.</p>
 * 
 * <p>While this theoretically supports caching via the features provided in ReloadableResourceBundleMessageSource, this should
 * not be used and instead should have cacheMillis always set to -1 (which is the default implementation). This ensures
 * that codes are always obtained from a merged property list.</p>
 * 
 * <p>The basenames in this implementation are Spring path resources so if you need to refer to a resource on the classpath,
 * these should be prefixed with classpath:. This is slightly different from the {@link ResourceBundleMessageSource}; see
 * {@link ReloadableResourceBundleMessageSource#setBasenames(String...)} for more information.</p>
 *
 * <p>The default logic will look in the DB (cache) first and if it's not found will delegate to
 * super.resolveCode() to resolve the message.
 * </p>
 * 
 * @author Phillip Verheyden
 * @see {@link ReloadableResourceBundleMessageSource}
 * @see {@link ResourceLoader#getResource(String)}
 * @see {@link #setBasenames(String...)}
 */
public class BroadleafMergeResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {

    @Resource(name = "blMessageSourceService")
    protected MessageSourceService messageSourceService;

    @Resource(name = "blLocaleService")
    protected LocaleService localeService;

    /**
     * The super implementation ensures the basenames defined at the beginning take precedence. We require the opposite in
     * order to be in line with previous assumptions about the applicationContext merge process (meaning, beans defined in
     * later applicationContexts take precedence). Thus, this reverses <b>basenames</b> before passing it up to the super
     * implementation.
     * 
     * @param basenames
     * @param resourceBundleExtensionPoint
     * @see {@link ReloadableResourceBundleMessageSource#setBasenames(String...)}
     */
    @Override
    public void setBasenames(String... basenames) {
        CollectionUtils.reverseArray(basenames);
        super.setBasenames(basenames);
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        org.broadleafcommerce.common.locale.domain.Locale blcLocale = localeService.findLocaleByCode(locale.toString());
        if (blcLocale != null) {
            String message = messageSourceService.resolveMessageSource(code, blcLocale);
            if (StringUtils.isNotBlank(message)) {
                return createMessageFormat(message, locale);
            }
        }

        return super.resolveCode(code, locale);
    }

    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        org.broadleafcommerce.common.locale.domain.Locale blcLocale = localeService.findLocaleByCode(locale.toString());
        if (blcLocale != null) {
            String message = messageSourceService.resolveMessageSource(code, blcLocale);
            if (StringUtils.isNotBlank(message)) {
                return message;
            }
        }

        return super.resolveCodeWithoutArguments(code, locale);
    }
}
