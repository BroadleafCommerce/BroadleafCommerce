package org.broadleafcommerce.common.util;

import org.broadleafcommerce.common.logging.ModuleLifecycleLoggingBean;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;

import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Conditional class that checks for the presence of a specified module.
 *
 * @author Nathan Moore (nathanmoore).
 */
public class ModulePresentUtil {

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
    public static boolean isPresent(@Nonnull final String moduleInQuestion) {
        ListableBeanFactory factory = ApplicationContextHolder.getApplicationContext();
        Map<String, ModuleLifecycleLoggingBean> beanMap = BeanFactoryUtils.beansOfTypeIncludingAncestors(factory, ModuleLifecycleLoggingBean.class, false, false);

        for (ModuleLifecycleLoggingBean module : beanMap.values()) {
            String moduleName = module.getModuleName();

            if (moduleName.equals(moduleInQuestion)) {
                return true;
            }
        }

        return false;
    }
}
