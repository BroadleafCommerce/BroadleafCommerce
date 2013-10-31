/*
 * #%L
 * BroadleafCommerce Workflow
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
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
