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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import org.broadleafcommerce.common.config.EnableBroadleafSiteServletAutoConfiguration.BroadleafSiteServletAutoConfiguration;
import org.broadleafcommerce.common.config.EnableBroadleafSiteServletAutoConfiguration.BroadleafSiteServletAutoConfigurationOverrides;
import org.broadleafcommerce.common.extensibility.FrameworkXmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.servlet.ServletContainerInitializer;


/**
 * <p>
 * Bootstraps Broadleaf site configuration XML for <b>only</b> servlet beans in use with a traditional MVC application. If you are deploying Broadleaf in a
 * REST-API-only capacity or any other way then this annotation is probably not for you, and instead just {@link EnableBroadleafSiteRootAutoConfiguration}
 * is sufficient.If you have a customized {@link ServletContainerInitializer}
 * with a servlet-specific {@link ApplicationContext}, this annotation should only be placed on an {@literal @}Configuration class within
 * <b>that</b> servlet-specific {@lnk ApplicationContext}. If this is not the case and no servlet-specific {@link ApplicationContext} exists in your
 * project and you are using Spring Boot, this <b>must</b> be placed on an <b>inner static class</b> within the {@literal @}SpringBootApplication class. Example:
 * 
 * <pre>
 * {@literal @}SpringBootApplication
 * public class MyApplication extends SpringBootServletInitializer {
 * 
 *     {@literal @}Configuration
 *     {@literal @}EnableBroadleafSiteServletAutoConfiguration
 *     public static class BroadleafConfiguration { }
 *     
 *     public static void main(String[] args) {
 *         SpringApplication.run(ApiApplication.class, args);
 *     }
 *  
 *     {@literal @}Override
 *     protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
 *         return application.sources(ApiApplication.class);
 *     }
 * }
 *
 * </pre>
 * 
 * <p>
 * Since this annotation is a meta-annotation for {@literal @}Import, this <b>can</b> be placed on a {@literal @}Configuration class
 * that contains an {@literal @}Import annotation, <b>but</b> this {@literal @}Import's beans will take precedence over
 * any additional {@literal @}Import applied.
 *
 * <p>
 * This annotation assumes that you have activated the root configuration via {@link EnableBroadleafAdminRootAutoConfiguration} in a parent
 * context. However, rather than using this annotation in a parent-child configuration consider using {@link EnableBroadleafSiteAutoConfiguration} to
 * ensure that only a single {@link ApplicationContext} is present, or just use the {@link EnableBroadleafAutoConfiguration}
 * 
 * <p>
 * This import utilizes the {@link FrameworkXmlBeanDefinitionReader} so that framework XML bean definitions will not
 * overwrite beans defined in a project.
 *
 * @author Phillip Verheyden (phillipuniverse)
 * @author Nick Crum (ncrum)
 * @see EnableBroadleafSiteAutoConfiguration
 * @see EnableBroadleafAutoConfiguration
 * @since 5.2
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import({
    BroadleafSiteServletAutoConfiguration.class,
    BroadleafSiteServletAutoConfigurationOverrides.class
})
public @interface EnableBroadleafSiteServletAutoConfiguration {

    /**
     * We are deliberately leaving off the {@link org.springframework.context.annotation.Configuration} annotation since
     * this inner class is being included in the {@code Import} above, which interprets this as a
     * {@link org.springframework.context.annotation.Configuration}. We do this to avoid component scanning this inner class.
     */
    @Import(EnableBroadleafServletAutoConfiguration.BroadleafServletAutoConfiguration.class)
    @ImportResource(locations = {
            "classpath*:/blc-config/site/framework/bl-*-applicationContext-servlet.xml",
            "classpath*:/blc-config/site/early/bl-*-applicationContext-servlet.xml",
            "classpath*:/blc-config/site/bl-*-applicationContext-servlet.xml",
            "classpath*:/blc-config/site/late/bl-*-applicationContext-servlet.xml"
    }, reader = FrameworkXmlBeanDefinitionReader.class)
    class BroadleafSiteServletAutoConfiguration {}
    
    @ImportResource("classpath:/override-contexts/site-servlet-autoconfiguration-overrides.xml")
    class BroadleafSiteServletAutoConfigurationOverrides {}
}
