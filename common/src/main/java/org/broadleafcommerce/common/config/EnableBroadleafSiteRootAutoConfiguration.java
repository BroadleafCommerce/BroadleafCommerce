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

import org.broadleafcommerce.common.config.EnableBroadleafSiteRootAutoConfiguration.BroadleafSiteRootAutoConfiguration;
import org.broadleafcommerce.common.config.EnableBroadleafSiteRootAutoConfiguration.BroadleafSiteRootAutoConfigurationOverrides;
import org.broadleafcommerce.common.extensibility.FrameworkXmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Bootstraps Broadleaf <b>root</b> site configuration XML for only non-servlet beans. This can be placed on any {@literal @}Configuration
 * class to make the core Broadleaf beans apart of the {@link ApplicationContext}. If you are using Spring boot,
 * this <b>must</b> be placed on an <b>inner static class</b> within the {@literal @}SpringBootApplication class. Example:
 * 
 * <pre>
 * {@literal @}SpringBootApplication
 * public class MyApplication extends SpringBootServletInitializer {
 * 
 *     {@literal @}Configuration
 *     {@literal @}EnableBroadleafSiteRootAutoConfiguration
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
 * Since this does not include any of the servlet-specific Broadleaf beans, this is generally only used when you are not running in a 
 * servlet environment at all or there is a parent-child relationship between a root {@link ApplicationContext} and you want to
 * configure multiple servlets that share much of the same beans. In general, rather than create multiple servlets with shared
 * configuration you should instead create separate deployments and utilize {@link EnableBroadleafSiteAutoConfiguration} in a single place.
 * 
 * <p>
 * This import utilizes the {@link FrameworkXmlBeanDefinitionReader} so that framework XML bean definitions will not
 * overwrite beans defined in a project.
 *
 * @author Philip Bagget (pbaggett)
 * @author Phillip Verheyden (phillipuniverse)
 * @author Nick Crum (ncrum)
 * @see EnableBroadleafSiteAutoConfiguration
 * @see EnableBroadleafAutoConfiguration
 * @since 5.2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
    BroadleafSiteRootAutoConfiguration.class,
    BroadleafSiteRootAutoConfigurationOverrides.class
})
public @interface EnableBroadleafSiteRootAutoConfiguration {

    /**
     * We are deliberately leaving off the {@link org.springframework.context.annotation.Configuration} annotation since
     * this inner class is being included in the {@code Import} above, which interprets this as a
     * {@link org.springframework.context.annotation.Configuration}. We do this to avoid component scanning this inner class.
     */
    @Import(EnableBroadleafRootAutoConfiguration.BroadleafRootAutoConfiguration.class)
    @ImportResource(locations = {
            "classpath*:/blc-config/site/framework/bl-*-applicationContext.xml",
            "classpath*:/blc-config/site/early/bl-*-applicationContext.xml",
            "classpath*:/blc-config/site/bl-*-applicationContext.xml",
            "classpath*:/blc-config/site/late/bl-*-applicationContext.xml"
    }, reader = FrameworkXmlBeanDefinitionReader.class)
    class BroadleafSiteRootAutoConfiguration {}
    
    @ImportResource("classpath:/override-contexts/site-root-autoconfiguration-overrides.xml")
    class BroadleafSiteRootAutoConfigurationOverrides {}
}
