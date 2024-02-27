/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.web;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.i18n.service.TranslationService;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.util.Validate;

import java.util.Locale;

import javax.annotation.Resource;

/**
 * This implementation will check to see if the key matches the known i18n value key. If that is the case, we will attempt 
 * to translate the requested field value for the entity/key. If not, we will delegate to other message resolvers.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class BroadleafThymeleafMessageResolver extends AbstractMessageResolver {
    protected static final Log LOG = LogFactory.getLog(BroadleafThymeleafMessageResolver.class);
    protected static final String I18N_VALUE_KEY = "translate";
    
    @Resource(name = "blTranslationService")
    protected TranslationService translationService;

    @Override
    public String resolveMessage(ITemplateContext context, Class<?> origin, String key, Object[] messageParams) {

        Validate.notNull(context.getLocale(), "Locale in context cannot be null");
        if (I18N_VALUE_KEY.equals(key)) {
            Object entity = messageParams[0];
            String property = (String) messageParams[1];
            Locale locale = context.getLocale();

            if (LOG.isTraceEnabled()) {
                LOG.trace(String.format("Attempting to resolve translated value for object %s, property %s, locale %s",
                        entity, property, locale));
            }

            String resolvedMessage = translationService.getTranslatedValue(entity, property, locale);

            if (StringUtils.isNotBlank(resolvedMessage)) {
                return resolvedMessage;
            }
        }
        return null;
    }

    @Override
    public String createAbsentMessageRepresentation(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
        return null;
    }
}