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

import org.broadleafcommerce.common.config.EnableBroadleafServletAutoConfiguration.BroadleafServletAutoConfiguration;
import org.broadleafcommerce.common.config.EnableBroadleafServletAutoConfiguration.BroadleafServletAutoConfigurationOverrides;
import org.broadleafcommerce.common.extensibility.FrameworkXmlBeanDefinitionReader;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <b>STOP. This is probably not the annotation you want currently.</b>
 * <p>
 * The same rules apply here as with {@link EnableBroadleafAutoConfiguration} but this is for only the servlet-level Broadleaf beans
 * @author Philip Baggett (pbaggett)
 * @author Brandon Hines (bhines)
 * @author Nick Crum (ncrum)
 * @see EnableBroadleafSiteServletAutoConfiguration
 * @see EnableBroadleafAdminRootAutoConfiguration
 * @see EnableBroadleafRootAutoConfiguration
 * @see EnableBroadleafAutoConfiguration
 * @since 5.2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
    BroadleafServletAutoConfiguration.class,
    BroadleafServletAutoConfigurationOverrides.class
})
public @interface EnableBroadleafServletAutoConfiguration {

    /**
     * We are deliberately leaving off the {@link org.springframework.context.annotation.Configuration} annotation since
     * this inner class is being included in the {@code Import} above, which interprets this as a
     * {@link org.springframework.context.annotation.Configuration}. We do this to avoid component scanning this inner class.
     */
    @ImportResource(locations = {
            "classpath*:/blc-config/framework/bl-*-applicationContext-servlet.xml",
            "classpath*:/blc-config/early/bl-*-applicationContext-servlet.xml",
            "classpath*:/blc-config/bl-*-applicationContext-servlet.xml",
            "classpath*:/blc-config/late/bl-*-applicationContext-servlet.xml"
    }, reader = FrameworkXmlBeanDefinitionReader.class)
    class BroadleafServletAutoConfiguration {}
    
    @ImportResource("classpath:/override-contexts/autoconfiguration-servlet-overrides.xml")
    class BroadleafServletAutoConfigurationOverrides { }
}
