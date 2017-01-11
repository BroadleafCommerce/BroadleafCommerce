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
 * Bootstraps Broadleaf configuration XML using a glob import.
 * <p>
 * This import utilizes the {@link FrameworkXmlBeanDefinitionReader} so that framework XML bean definitions will not
 * overwrite beans defined in a project.
 * <p>
 * Files ending with "-servlet" are included for backwards compatibility reasons. These are no longer necessary as of
 * Spring 4.0 since controllers can now be included in the regular application context. Eventually, when all modules
 * have been updated to not use "-servlet" files, the "-servlet" import location can be removed.
 *
 * @author Philip Baggett (pbaggett)
 * @author Brandon Hines (bhines)
 * @since 5.2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ImportResource(locations = {
        "classpath*:/blc-config/bl-*-applicationContext.xml",
        "classpath*:/blc-config/bl-*-applicationContext-servlet.xml"
}, reader = FrameworkXmlBeanDefinitionReader.class)
public @interface EnableBroadleafAutoConfiguration {
}
