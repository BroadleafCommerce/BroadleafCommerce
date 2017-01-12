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
import org.springframework.context.annotation.ImportResource;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bootstraps Broadleaf admin configuration XML using a glob import.
 * <p>
 * This import utilizes the {@link FrameworkXmlBeanDefinitionReader} so that framework XML bean definitions will not
 * overwrite beans defined in a project.
 * <p>
 * This annotation is a temporary measure until all modules can be updated to not have admin and site specific
 * configuration files, but instead leverage {@link org.broadleafcommerce.common.admin.condition.ConditionalOnAdmin} and
 * use {@link EnableBroadleafAutoConfiguration}. This is documented further on {@link EnableBroadleafAutoConfiguration}.
 *
 * @author Philip Baggett (pbaggett)
 * @see EnableBroadleafAutoConfiguration
 * @since 5.2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ImportResource(locations = {
        "classpath*:/blc-config/bl-*-applicationContext.xml",
        "classpath*:/blc-config/bl-*-applicationContext-servlet.xml",
        "classpath*:/blc-config/admin/bl-*-applicationContext.xml",
        "classpath*:/blc-config/admin/bl-*-applicationContext-servlet.xml"
}, reader = FrameworkXmlBeanDefinitionReader.class)
public @interface EnableBroadleafAdminAutoConfiguration {
}
