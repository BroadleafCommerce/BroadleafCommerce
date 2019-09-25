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
package org.broadleafcommerce.common.io;

import org.springframework.util.Assert;

import java.net.URLStreamHandler;
import java.util.HashMap;

/**
 * Abstract class to allow a protocol handler to be registered with the JVM.  
 * Subclasses MUST be named 'Handler' and they MUST NOT be in a base (default) package.
 * 
 * Also, subclasses must register themselves by calling 
 * {@link AbstractRegisteringURLStreamHandler#registerHandler(Class)} in a static block.  For example:
 * 
 * static {
 *     registerHandler(Handler.class);
 * }
 * 
 * This will register the Handler class in a particular package.  There can be multiple Handler 
 * classes for multiple protocols, but each must be explicitly called "Handler" and each one must be in a different package.
 * 
 * The protocol, by convention, will be the last part (or folder) of the package in which the Handler class is located.
 * 
 * For more information, see https://docs.oracle.com/javase/8/docs/api/java/net/URL.html#URL-java.lang.String-java.lang.String-int-java.lang.String- and 
 * the 'java.protocol.handler.pkgs' system property.
 * 
 * 
 * @author Kelly Tisdell
 *
 */
public abstract class AbstractRegisteringURLStreamHandler extends URLStreamHandler {
    
    public static final String PROTOCOL_HANDLER_PKGS_PROPERTY_NAME = "java.protocol.handler.pkgs";
    
    private static final HashMap<String, Class<? extends URLStreamHandler>> PROTOCOL_REGISTRY = new HashMap<>();
    
    private String protocol;
    
    /**
     * Method that allows a Handler that extends this class to register itself with the JVM.
     * 
     * @param handlerClass
     */
    public static synchronized void registerHandler(Class<? extends URLStreamHandler> handlerClass) {
        Assert.notNull(handlerClass, "The handlerClass argument cannot be null.");
        Assert.isTrue("Handler".equals(handlerClass.getSimpleName()), URLStreamHandler.class.getName() 
                + " class implementations must be explicitly named 'Handler', and not " + handlerClass.getSimpleName());
        
        final String was = System.getProperty(PROTOCOL_HANDLER_PKGS_PROPERTY_NAME, "");
        final String pkg = handlerClass.getPackage().getName();
        final int packageDelimIdx = pkg.lastIndexOf('.');
        
        //By default if you register two different Handlers in different packages that have the same last folder name, then the last one wins.  That can cause confusion and some 
        //difficulty troubleshooting.  As a convenience, we'll throw an exception if two different Handlers are registered with the same implicit protocol.
        //For example, if there is a Handler called com.mycompany.foo.Handler and another called com.mycompany.handlers.foo.Handler, then one of them will win and the other will be ignored. 
        //This will assist in preventing that by ensuring that registry of URLStreamHandler implementations are each have a unique protocol.
        //One caveat is that if someone registered the package names of Handlers outside of this class using a -D system argument (e.g. -Djava.protocol.handler.pkgs=com.mycompany.foo|com.mycompany.handlers.foo), 
        //and the Handler(s) do not invoke this method then there is still a possibility that one URLStreamHandler overrides another for the same protocol name.
        final String protocol = getProtocol(handlerClass);
        
        if (PROTOCOL_REGISTRY.containsKey(protocol)) {
            throw new IllegalStateException("The protocol, '" + protocol + "', was already registered by the class " + PROTOCOL_REGISTRY.get(protocol).getName());
        } else {
            PROTOCOL_REGISTRY.put(protocol, handlerClass);
        }
        
        //Ensure that this package hasn't already been added.
        String[] tokens = was.split("\\|");
        for (String token : tokens) {
            if (token.equals(pkg)) {
                //This package name has already been added to the system property, so return
                return;
            }
        }
        
        //Append the new package to the system property.
        System.setProperty(PROTOCOL_HANDLER_PKGS_PROPERTY_NAME, pkg.substring(0, packageDelimIdx) +
                (was.isEmpty() ? "" : "|" + was));
        
    }
    
    /**
     * Convenience method to get the protocol for this Handler instance.  
     * By convention, the protocol name is the last part of the package name containing the concrete Handler implementation.
     * @return
     */
    public String getProtocol() {
        if (protocol == null) {
            synchronized (this) {
                if (protocol == null) {
                    protocol = getProtocol(this.getClass());
                }
            }
        }
        return protocol;
    }
    
    /**
     * Convenience method to get the protocol for any Handler class.
     * @param handlerClass
     * @return
     */
    public static String getProtocol(Class<? extends URLStreamHandler> handlerClass) {
        final String fullPackage = handlerClass.getPackage().getName();
        final int packageDelimIdx = fullPackage.lastIndexOf('.');
        Assert.isTrue(packageDelimIdx != -1, URLStreamHandler.class.getName() + " implementations cannot be registered from the base (default) package.");
        final String[] tokens = fullPackage.split("\\.");
        return tokens[tokens.length - 1];
    }
}