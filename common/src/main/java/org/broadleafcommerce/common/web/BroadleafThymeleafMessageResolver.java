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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.messageresolver.MessageResolution;
import org.thymeleaf.spring3.messageresolver.SpringMessageResolver;
import org.thymeleaf.util.Validate;

import java.util.Locale;

public class BroadleafThymeleafMessageResolver extends AbstractMessageResolver implements MessageSourceAware {
    
    protected static final Log LOG = LogFactory.getLog(BroadleafThymeleafMessageResolver.class);
    
    protected MessageSource messageSource;
    
    public BroadleafThymeleafMessageResolver() {
        super();
    }
    
    public MessageResolution resolveMessage(final Arguments args, final String key, final Object[] messageParams) {
        Validate.notNull(args, "args cannot be null");
        Validate.notNull(args.getContext().getLocale(), "Locale in context cannot be null");
        Validate.notNull(key, "Message key cannot be null");
        
        if (LOG.isTraceEnabled()) {
            LOG.trace(String.format("Thread %s resolving message with key \"%s\" for template \"%s\" and " +
            		"locale \"%s\". Messages will be retrieved from Spring's MessageSource infrastructure.", 
            		TemplateEngine.threadIndex(), key, args.getTemplateName(), args.getContext().getLocale()));
        }
        
        try {
            Locale locale = args.getContext().getLocale();
            final String resolvedMessage = this.messageSource.getMessage(key, messageParams, locale);
            return new MessageResolution(resolvedMessage);
        } catch (NoSuchMessageException e) {
            return null;
        }
    }
    
    protected boolean isI18NFieldValueKey(final Arguments args, final String key, final Object[] messageParams) {
        
        return false;
    }
    
    protected MessageResolution getI18NFieldTranslation(String key) {
        
        return null;
    }
    
    public final MessageSource getMessageSource() {
        checkInitialized();
        return this.messageSource;
    }
    
    protected final MessageSource unsafeGetMessageSource() {
        return this.messageSource;
    }
    
    public void setMessageSource(final MessageSource messageSource) {
        checkNotInitialized();
        this.messageSource = messageSource;
    }
    
    @Override
    protected final void initializeSpecific() {
        if (this.messageSource == null) {
            throw new ConfigurationException(
                    "Cannot initialize " + SpringMessageResolver.class.getSimpleName() + 
                    ": MessageSource has not been set. Either define this object as " +
                    "a Spring bean (which will automatically set the MessageSource) or, " +
                    "if you instance it directly, set the MessageSource manually using its "+
                    "corresponding setter method.");
        }
    }
    
}