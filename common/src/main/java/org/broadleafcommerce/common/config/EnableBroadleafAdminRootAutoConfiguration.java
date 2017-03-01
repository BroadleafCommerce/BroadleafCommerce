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
package org.broadleafcommerce.common.config;

import org.broadleafcommerce.common.extensibility.FrameworkXmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Bootstraps the Broadleaf <b>root</b> admin configuration XML for only non-servlet beans. This can be placed on any {@literal @}Configuration
 * class (except ones with additional {@literal @}ImportResource) to make the root Broadleaf beans apart of the {@link ApplicationContext}
 * 
 * <p>
 * Since this annotation is a meta-annotation for {@literal @}ImportResource, this <b>cannot</b> be placed on a {@literal @}Configuration class
 * that contains an {@literal @}ImportResource annotation directly or on a meta-annotation.
 *  
 * <p>
 * Since this does not include any of the servlet-specific Broadleaf beans, this is generally only used when you are not running in a 
 * servlet environment at all or there is a parent-child relationship between a root {@link ApplicationContext} and you want to
 * configure multiple servlets that share much of the same beans. In general, rather than create multiple servlets with shared
 * configuration you should instead create separate deployments and utilize {@link EnableBroadleafAdminAutoConfiguration} in a single place.
 * 
 * <p>
 * This import utilizes the {@link FrameworkXmlBeanDefinitionReader} so that framework XML bean definitions will not
 * overwrite beans defined in a project.
 *
 * @author Philip Baggett (pbaggett)
 * @author Phillip Verheyden (phillipuniverse)
 * @see EnableBroadleafAdminAutoConfiguration
 * @see EnableBroadleafAdminServletAutoConfiguration
 * @see EnableBroadleafAutoConfiguration
 * @since 5.2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ImportResource(locations = {
        "classpath*:/blc-config/bl-*-applicationContext.xml",
        "classpath*:/blc-config/admin/bl-*-applicationContext.xml"
}, reader = FrameworkXmlBeanDefinitionReader.class)
public @interface EnableBroadleafAdminRootAutoConfiguration {
}
