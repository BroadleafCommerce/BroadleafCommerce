package org.broadleafcommerce.common.util;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.logging.ModuleLifecycleLoggingBean;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;

import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Conditional class that checks for the presence of a specified module.
 * <p>
 * Will only find beans in modules that have declared {@link ModuleLifecycleLoggingBean}
 * in their applicationContext.xml
 *
 * @author Nathan Moore (nathanmoore).
 */
public class ModulePresentUtil {

    /**
     * List of modules that are known to have declared {@link ModuleLifecycleLoggingBean}
     * in their applicationContext.xml
     */
    public enum BroadleafModuleEnum {
        ACCOUNT ("Account"),
        ADVANCED_CMS ("AdvancedCMS"),
        ADVANCED_INVENTORY ("broadleaf-advanced-inventory"),
        ADVANCED_OFFER ("AdvancedOffer"),
        CUSTOMER_SEGMENT ("CustomerSegment"),
        GIFT_CARD_AND_CUSTOMER_CREDIT ("GiftCardAndCustomerCredit (AccountCredit)"),
        ENTERPRISE ("Enterprise"),
        ENTERPRISE_SEARCH ("Enterprise Search"),
        I18N_ENTERPRISE ("i18n Enterprise"),
        IMPORT ("ImportExport"),
        MENU ("Menu"),
        MERCHANDISING_GROUP ("MerchandisingGroup"),
        MULTI_TENANT_SINGLE_SCHEMA ("MultiTenant-SingeSchema"),
        OMS ("broadleaf-oms"),
        PRICE_LIST ("PriceList"),
        QUOTE ("Quote"),
        SUBSCRIPTION ("Subscription"),
        THEME ("Theme");

        private final String name;

        BroadleafModuleEnum(String name) {
            this.name = name;
        }

        public boolean equalsModuleName(String name) {
            return StringUtils.equals(name, this.name);
        }

        public String toString() {
            return this.name;
        }
    }

    /**
     * Treats the {@link org.springframework.context.ApplicationContext} as a
     * {@link ListableBeanFactory} to produce a {@link Map} of
     * {@link org.springframework.context.annotation.Bean}. When it finds a
     * Bean in the module specified, it returns true. Otherwise, it returns false.
     *
     * @param moduleInQuestion Name of the module being looked for.
     *
     * @return whether the module in question is present in the project
     */
    public static boolean isPresent(@Nonnull final BroadleafModuleEnum moduleInQuestion) {
        ListableBeanFactory factory = ApplicationContextHolder.getApplicationContext();
        Map<String, ModuleLifecycleLoggingBean> beanMap = BeanFactoryUtils.beansOfTypeIncludingAncestors(factory, ModuleLifecycleLoggingBean.class, false, false);

        for (ModuleLifecycleLoggingBean module : beanMap.values()) {
            String moduleName = module.getModuleName();

            if (moduleInQuestion.equalsModuleName(moduleName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * This version takes a String instead of a {@link BroadleafModuleEnum}
     *
     * @param moduleInQuestion
     * @return
     */
    public static boolean isPresent(@Nonnull final String moduleInQuestion) {
        ListableBeanFactory factory = ApplicationContextHolder.getApplicationContext();
        Map<String, ModuleLifecycleLoggingBean> beanMap = BeanFactoryUtils.beansOfTypeIncludingAncestors(factory, ModuleLifecycleLoggingBean.class, false, false);

        for (ModuleLifecycleLoggingBean module : beanMap.values()) {
            String moduleName = module.getModuleName();

            if (moduleInQuestion.equals(moduleName)) {
                return true;
            }
        }

        return false;
    }
}
