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
package org.broadleafcommerce.common.condition;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.logging.ModuleLifecycleLoggingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Allows for conditional registration of beans depending on if a particular Broadleaf module is present, which
 * by default checks if they have been registered via a {@link ModuleLifecycleLoggingBean#getModuleName()}, which every module should
 * have.
 * 
 * <p>
 * There are 2 options for checking these registrations:
 * <ol>
 *  <li>The type-safe {@link #value()} which assumes that this class is kept up to date with different modules that are added</li>
 *  <li>The {@link #moduleName()} which maps directly to the {@link ModuleLifecycleLoggingBean#getModuleName()}</li>
 * </ol>
 * 
 * <p>
 * Generally you should use the {@link #value()} attribute to give you a type-safe way to reference the registrations but it is possible
 * that you need to reference a module that has not yet been added to this class. In that case, use the {@link #moduleName()} parameter
 * which alows
 * 
 * <p>
 * If the module has not registered itself with the {@link ModuleLifecycleLoggingBean} then this annotation will do nothing. In this case,
 * utilize one of the Spring {@code ConditionalOn...} annotations like {@link ConditionalOnClass} and consider making your own composed
 * annotation that utilizes it. For instance:
 * 
 * <pre>
 * {@literal @}Target({ ElementType.TYPE, ElementType.METHOD })
 * {@literal @}Retention(RetentionPolicy.RUNTIME)
 * {@literal @}Documented
 * {@literal @}ConditionalOnClass("com.broadleafcommerce.somemodule.ModuleClass")
 * public {@literal @}interface ConditionalOnSomeModule {
 * 
 * }
 * </pre>
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @since 5.2
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(BroadleafModuleCondition.class)
public @interface ConditionalOnBroadleafModule {
    
    /**
     * Which module to check for the presence of. This should generally be preferred to using {@link #moduleName()}
     * but can be used as as stop-gap measurement if the module is not explicitly defined in {@link BroadleafModuleEnum}.
     */
    public BroadleafModuleEnum value() default BroadleafModuleEnum.IGNORED;
    
    /**
     * This should only be used if the module you are checking for has not yet been added to the {@link BroadleafModuleEnum} as that.
     * Generally you should seek to use the {@link #value()} parameter and add additional modules as needed.
     */
    public String moduleName() default "";
    
    /**
     * List of modules that are known to have declared {@link ModuleLifecycleLoggingBean} in their applicationContext.xml
     */
    public enum BroadleafModuleEnum {
        ACCOUNT ("Account"),
        ADVANCED_CMS ("AdvancedCMS"),
        ADVANCED_INVENTORY ("broadleaf-advanced-inventory"),
        ADVANCED_OFFER ("AdvancedOffer"),
        AFFILIATE ("Affilliate"),
        CART_RULES ("broadleaf-cart-rules"),
        CATALOG_ACCESS_POLICY ("CatalogAccessPolicy"),
        CUSTOMER_SEGMENT ("CustomerSegment"),
        CUSTOM_FIELD ("CustomField"),
        GIFT_CARD_AND_CUSTOMER_CREDIT ("GiftCardAndCustomerCredit (AccountCredit)"),
        ENTERPRISE ("Enterprise"),
        ENTERPRISE_SEARCH ("Enterprise Search"),
        EXPORT ("Export"),
        I18N_ENTERPRISE ("i18n Enterprise"),
        IMPORT ("Import"),
        JOBS_AND_EVENTS ("Jobs and Events"),
        MENU ("Menu"),
        MARKETPLACE ("Marketplace"),
        MERCHANDISING_GROUP ("MerchandisingGroup"),
        MULTI_TENANT_SINGLE_SCHEMA ("MultiTenant-SingeSchema"),
        OMS ("broadleaf-oms"),
        PRICE_LIST ("PriceList"),
        PROCESS ("Process"),
        PRODUCT_TYPE ("broadleaf-product-type"),
        QUOTE ("Quote"),
        REST_API ("Broadleaf REST APIs"),
        SUBSCRIPTION ("Subscription"),
        THEME ("Theme"),
        THYMELEAF2 ("Broadleaf Thymeleaf 2 Support"),
        THYMELEAF3 ("Broadleaf Thymeleaf 3 Support"),
        
        /**
         * Added in order to provide an optional default value to {@link ConditionalOnBroadleafModule}
         */
        IGNORED ("IGNORED");

        private final String name;

        BroadleafModuleEnum(String name) {
            this.name = name;
        }

        public boolean equalsModuleName(String name) {
            return StringUtils.equals(name, this.name);
        }
        
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
