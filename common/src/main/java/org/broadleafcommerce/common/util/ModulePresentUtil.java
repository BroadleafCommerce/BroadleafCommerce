package org.broadleafcommerce.common.util;

import org.broadleafcommerce.common.logging.ModuleLifecycleLoggingBean;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;

import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Conditional class that checks for the presence of the Enterprise module.
 *
 * @author Nathan Moore (nathanmoore).
 */
public class ModulePresentUtil {

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
