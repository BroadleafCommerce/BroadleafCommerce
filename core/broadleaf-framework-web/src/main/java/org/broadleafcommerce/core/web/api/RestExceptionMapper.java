/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.api;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.web.api.wrapper.ErrorMessageWrapper;
import org.broadleafcommerce.core.web.api.wrapper.ErrorWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * This is a generic JAX-RS ExceptionMapper.  This can be registered as a Spring Bean to catch exceptions, log them, 
 * and return reasonable responses to the client.  Alternatively, you can extend this or implement your own, more granular, mapper(s). 
 * This class does not return internationalized messages. But for convenience, this class provides a protected 
 * Spring MessageSource to allow for internationalization if one chose to go that route.
 * 
 * @author Kelly Tisdell
 *
 */
//This class MUST be a singleton Spring Bean
@Provider
@Component("blRestExceptionMapper")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RestExceptionMapper implements ExceptionMapper<Throwable>, MessageSourceAware, ApplicationContextAware {

    private static final Log LOG = LogFactory.getLog(RestExceptionMapper.class);

    protected String messageKeyPrefix = BroadleafWebServicesException.class.getName() + '.';

    @Context
    protected HttpHeaders headers;

    protected MessageSource messageSource;

    protected ApplicationContext context;

    @Override
    public Response toResponse(Throwable t) {
        MediaType mediaType = resolveResponseMediaType(t);
        ErrorWrapper errorWrapper = (ErrorWrapper) context.getBean(ErrorWrapper.class.getName());
        Locale locale = null;
        BroadleafRequestContext requestContext = BroadleafRequestContext.getBroadleafRequestContext();
        if (requestContext != null) {
            locale = requestContext.getJavaLocale();
        }

        if (t instanceof BroadleafWebServicesException) {
            //If this is a BroadleafWebServicesException, then we will build the components of the response from that.
            BroadleafWebServicesException blcException = (BroadleafWebServicesException) t;
            if (t.getCause() != null) {
                LOG.error("An error occured invoking a REST service.", t.getCause());
            }
            errorWrapper.setHttpStatusCode(blcException.getHttpStatusCode());
            if (blcException.getLocale() != null) {
                locale = blcException.getLocale();
            }
            if (locale == null) {
                locale = Locale.getDefault();
            }

            if (blcException.getMessages() != null && !blcException.getMessages().isEmpty()) {
                Set<String> keys = blcException.getMessages().keySet();
                for (String key : keys) {
                    ErrorMessageWrapper errorMessageWrapper = (ErrorMessageWrapper) context.getBean(ErrorMessageWrapper.class.getName());
                    errorMessageWrapper.setMessageKey(resolveClientMessageKey(key));
                    errorMessageWrapper.setMessage(messageSource.getMessage(key, blcException.getMessages().get(key), key, locale));
                    errorWrapper.getMessages().add(errorMessageWrapper);
                }
            } else {
                ErrorMessageWrapper errorMessageWrapper = (ErrorMessageWrapper) context.getBean(ErrorMessageWrapper.class.getName());
                errorMessageWrapper.setMessageKey(resolveClientMessageKey(BroadleafWebServicesException.UNKNOWN_ERROR));
                errorMessageWrapper.setMessage(messageSource.getMessage(BroadleafWebServicesException.UNKNOWN_ERROR, null,
                        BroadleafWebServicesException.UNKNOWN_ERROR, locale));
                errorWrapper.getMessages().add(errorMessageWrapper);
            }

        } else if (t instanceof WebApplicationException) {
            //We will trust that if someone through a WebApplicationException, then they already created the 
            //response properly.
            if (t.getCause() != null) {
                LOG.error("An error occured invoking a REST service.", t.getCause());
            }
            WebApplicationException webAppException = (WebApplicationException) t;
            return webAppException.getResponse();
        } else {
            LOG.error("An error occured invoking a REST service", t);
            if (locale == null) {
                locale = Locale.getDefault();
            }
            errorWrapper.setHttpStatusCode(500);
            ErrorMessageWrapper errorMessageWrapper = (ErrorMessageWrapper) context.getBean(ErrorMessageWrapper.class.getName());
            errorMessageWrapper.setMessageKey(resolveClientMessageKey(BroadleafWebServicesException.UNKNOWN_ERROR));
            errorMessageWrapper.setMessage(messageSource.getMessage(BroadleafWebServicesException.UNKNOWN_ERROR, null,
                    BroadleafWebServicesException.UNKNOWN_ERROR, locale));
            errorWrapper.getMessages().add(errorMessageWrapper);
        }

        return Response.status(resolveResponseStatusCode(t, errorWrapper)).type(mediaType).entity(errorWrapper).build();
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    /**
     * This key is the prefix that will be stripped off of all message keys that are returned to a client.
     * The default is "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.". So, if a message key contained 
     * in a BroadleafWebServicesException is org.broadleafcommerce.core.web.api.BroadleafWebServicesException.unknownError, 
     * just "unknownError" will be returned to the client. This behavior can be changed by overriding the 
     * <code>resolveClientMessageKey</code> method. 
     * @param prefix
     */
    public void setMessageKeyPrefix(String prefix) {
        this.messageKeyPrefix = prefix;
    }

    /*
     * This allows you to return a different HTTP response code in the HTTP response than what is in the response wrapper.
     * For example, some clients may wish to always return a 200 (SUCCESS), even when there is an error.
     * The default behavior is to return the same status code associated with the error wrapper, or 500 if it is not known.
     */
    protected int resolveResponseStatusCode(Throwable t, ErrorWrapper error) {
        if (error.getHttpStatusCode() == null) {
            return 500;
        }
        return error.getHttpStatusCode();
    }

    protected MediaType resolveResponseMediaType(Throwable t) {
        if (headers.getAcceptableMediaTypes() != null && !headers.getAcceptableMediaTypes().isEmpty()) {
            List<MediaType> types = headers.getAcceptableMediaTypes();
            for (MediaType type : types) {
                if (MediaType.APPLICATION_XML_TYPE.equals(type) || MediaType.APPLICATION_JSON_TYPE.equals(type)) {
                    return type;
                }
            }
        }

        if (MediaType.APPLICATION_XML_TYPE.equals(headers.getMediaType())) {
            return MediaType.APPLICATION_XML_TYPE;
        }

        return MediaType.APPLICATION_JSON_TYPE;
    }

    protected String resolveClientMessageKey(String key) {
        if (messageKeyPrefix != null) {
            return StringUtils.remove(key, messageKeyPrefix);
        }
        return key;
    }
}
