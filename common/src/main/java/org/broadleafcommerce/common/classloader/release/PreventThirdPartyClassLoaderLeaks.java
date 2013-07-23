package org.broadleafcommerce.common.classloader.release;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.LogManager;
import se.jiderhamn.classloader.leak.prevention.ClassLoaderLeakPreventor;

import javax.servlet.ServletContextEvent;
import java.lang.reflect.Method;

/**
 * @author Jeff Fischer
 */
public class PreventThirdPartyClassLoaderLeaks extends ClassLoaderLeakPreventor {

    private static Log LOG = LogFactory.getLog(PreventThirdPartyClassLoaderLeaks.class);

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        super.contextDestroyed(servletContextEvent);
        cleanMySql();
        ((Hierarchy) LogManager.getLoggerRepository()).clear();
    }

    protected Method getStaticMethod(String className, String methodName) {
        Method response = null;
        try {
            Class<?> clazz = Class.forName(className);
            response = clazz.getDeclaredMethod(methodName);
            response.setAccessible(true);
        } catch (ClassNotFoundException e) {
            //do nothing
        } catch (NoSuchMethodException e) {
            //do nothing
        }
        return response;
    }

    protected void cleanMySql() {
        try {
            Method method = getStaticMethod("com.mysql.jdbc.AbandonedConnectionCleanupThread", "shutdown");
            if (method != null) {
                method.invoke(null);
            }
        } catch (Exception e) {
            LOG.error("Unable to release the MySql AbandonedConnectionCleanupThread. May cause a classloader leak if re-deploying the app.", e);
        }
    }
}
