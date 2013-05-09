/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.web.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.annotation.Scope;

import javax.ws.rs.WebApplicationException;
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
@Scope("singleton")
@Provider
public class BroadleafRestExceptionMapper implements ExceptionMapper<Throwable>, MessageSourceAware {

    private static final Log LOG = LogFactory.getLog(BroadleafRestExceptionMapper.class);

    protected MessageSource messageSource;

    @Override
    public Response toResponse(Throwable t) {

        Response response = null;

        if (t instanceof WebApplicationException) {
            response = ((WebApplicationException) t).getResponse();
            if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                LOG.error("An exception was caught by the JAX-RS framework: Status: " + response.getStatus() + " Message: " + response.getEntity(), t);
            } else if (response.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
                LOG.warn("Someone tried to access a resource that was forbidden: Status: " + response.getStatus() + " Message: " + response.getEntity(), t);
            } else if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode() && LOG.isDebugEnabled()) {
                LOG.debug("Bad Request: Status: " + response.getStatus() + " Message: " + response.getEntity(), t);
            } else if (response.getStatus() == Response.Status.NOT_ACCEPTABLE.getStatusCode() && LOG.isDebugEnabled()) {
                LOG.debug("Not acceptable: Status: " + response.getStatus() + " Message: " + response.getEntity(), t);
            } else {
                LOG.error("An exception was caught by the JAX-RS framework: Status: " + response.getStatus() + " Message: " + response.getEntity(), t);
            }
        } else {
            LOG.error("An exception was caught by the JAX-RS framework: ", t);
        }

        if (response != null) {
            Object msg = response.getEntity();
            if (msg == null) {
                msg = "An error occurred";
            }
            return Response.status(response.getStatus()).type(MediaType.TEXT_PLAIN).entity(msg).build();
        }

        return Response.status(500).type(MediaType.TEXT_PLAIN).entity("An unknown or unreported error has occured. If the problem persists, please contact the administrator.").build();
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
