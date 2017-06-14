/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
/**
 * 
 */
package org.broadleafcommerce.common.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Loads the given set of classes after Spring's {@link EnableAutoConfiguration}. This must be added
 * to an {@code @Configuration} class that is within a normal component-scan, but the given class to
 * be imported <b>must not</b> be component-scanned.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @see PostAutoConfigurationDefferedImportSelector
 * @see PostAutoConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(PostAutoConfigurationDefferedImportSelector.class)
public @interface PostAutoConfigurationImport {

    /**
     * The classes that should be imported after Spring's {@link EnableAutoConfiguration} classes.
     * Classes passed in here <b>must</b> be <i>excluded</i> from any component scans or other {@code Import}s, and
     * should be annotated with {@literal @}PostAutoConfiguration
     */
    Class<?>[] value();
}
