/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2019 Broadleaf Commerce
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
package org.broadleafcommerce.url.handler.ehcache;

import java.net.URLStreamHandler;

public abstract class AbstractURLStreamHandler extends URLStreamHandler {

    protected static void addMyPackage(Class<? extends URLStreamHandler> handlerClass) {
        // Ensure that we are registered as a url protocol handler for JavaFxCss:/path css files.
        String was = System.getProperty("java.protocol.handler.pkgs", "");
        String pkg = handlerClass.getPackage().getName();
        int ind = pkg.lastIndexOf('.');
        assert ind != -1 : "You can't add url handlers in the base package";
        assert "Handler".equals(handlerClass.getSimpleName()) : "A URLStreamHandler must be in a class named Handler; not " + handlerClass.getSimpleName();

        System.setProperty("java.protocol.handler.pkgs", handlerClass.getPackage().getName().substring(0, ind) +
                (was.isEmpty() ? "" : "|" + was ));
    }


}