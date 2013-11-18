/*
 * #%L
 * BroadleafCommerce Workflow
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
package org.broadleafcommerce.common.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Jeff Fischer
 */
public class ExceptionHelper {

    private static final Log LOG = LogFactory.getLog(ExceptionHelper.class);

    public static <G extends Throwable, J extends RuntimeException> RuntimeException refineException(Class<G> refineType, Class<J> wrapType, Throwable e) {
        if (refineType.isAssignableFrom(e.getClass())) {
            return wrapException(e, wrapType);
        }
        Throwable rootCause = e;
        boolean eof = false;
        while (!eof) {
            if (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
                if (refineType.isAssignableFrom(rootCause.getClass())) {
                    return wrapException(e, wrapType);
                }
            } else {
                eof = true;
            }
        }
        return wrapException(e, wrapType);
    }

    public static <G extends Throwable, J extends RuntimeException> void processException(Class<G> refineType, Class<J> wrapType, Throwable e) throws G {
        if (refineType.isAssignableFrom(e.getClass())) {
            throw (G) e;
        }
        Throwable rootCause = e;
        boolean eof = false;
        while (!eof) {
            if (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
                if (refineType.isAssignableFrom(rootCause.getClass())) {
                    throw (G) rootCause;
                }
            } else {
                eof = true;
            }
        }
        throw wrapException(e, wrapType);
    }

    private static <J extends RuntimeException> RuntimeException wrapException(Throwable e, Class<J> wrapType) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        try {
            return wrapType.getConstructor(Throwable.class).newInstance(e);
        } catch (Exception e1) {
            LOG.error("Could not wrap exception", e1);
            throw new RuntimeException(e);
        }
    }

}
