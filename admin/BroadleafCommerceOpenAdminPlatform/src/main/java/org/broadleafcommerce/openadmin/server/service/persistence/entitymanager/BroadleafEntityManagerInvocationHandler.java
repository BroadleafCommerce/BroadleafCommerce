package org.broadleafcommerce.openadmin.server.service.persistence.entitymanager;

import org.broadleafcommerce.openadmin.server.service.SandBoxContext;
import org.broadleafcommerce.openadmin.server.service.SandBoxMode;
import org.broadleafcommerce.openadmin.server.service.exception.SandBoxException;
import org.hibernate.ejb.HibernateEntityManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/2/11
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class BroadleafEntityManagerInvocationHandler implements InvocationHandler {

    protected final HibernateEntityManager standardManager;
	protected final HibernateEntityManager sandboxManager;

	public BroadleafEntityManagerInvocationHandler(HibernateEntityManager standardManager, HibernateEntityManager sandboxManager) {
		this.standardManager = standardManager;
		this.sandboxManager = sandboxManager;
	}

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        SandBoxContext context = SandBoxContext.getSandBoxContext();
        boolean isSandBox = context != null && context.getSandBoxMode() == SandBoxMode.SANDBOX_COMMIT;
        HibernateEntityManager managerToUse = isSandBox?sandboxManager:standardManager;
        if (
            method.getName().equals("persist") ||
            method.getName().equals("remove") ||
            method.getName().equals("merge") ||
            method.getName().equals("flush") ||
            method.getName().equals("setFlushMode") ||
            method.getName().equals("getFlushMode") ||
            method.getName().equals("lock") ||
            method.getName().equals("getLockMode") ||
            method.getName().equals("joinTransaction") ||
            method.getName().equals("getTransaction")
        ) {
            return method.invoke(managerToUse, objects);
        }
        if (
            method.getName().equals("find") ||
            method.getName().equals("getReference") ||
            method.getName().equals("unwrap")
        ) {
            if (isSandBox) {
                Object response = method.invoke(sandboxManager, objects);
                if (response != null) {
                    return response;
                }
            }
            return method.invoke(standardManager, objects);
        }
        if (
            method.getName().equals("refresh")
        ) {
            if (isSandBox) {
                try {
                    return method.invoke(sandboxManager, objects);
                } catch (Exception e) {
                    //TODO remove this stack trace later
                    e.printStackTrace();
                }
            }
            return method.invoke(standardManager, objects);
        }
        if (
            method.getName().equals("clear") ||
            method.getName().equals("getProperties") ||
            method.getName().equals("setProperty") ||
            method.getName().equals("close")
        ) {
            method.invoke(sandboxManager, objects);
            return method.invoke(standardManager, objects);
        }
        if (
            method.getName().equals("detach")
        ) {
            if (sandboxManager.contains(objects[0])) {
                sandboxManager.detach(objects[0]);
            }
            if (standardManager.contains(objects[0])) {
                standardManager.detach(objects[0]);
            }
            return null;
        }
        if (
            method.getName().equals("contains")
        ) {
            if (isSandBox) {
                boolean contains = sandboxManager.contains(objects[0]);
                if (contains) {
                    return true;
                }
            }
            return standardManager.contains(objects[0]);
        }
        if (
            method.getName().equals("isOpen")
        ) {
            if (isSandBox) {
                boolean isOpen = sandboxManager.isOpen();
                if (!isOpen) {
                    return false;
                }
            }
            return standardManager.isOpen();
        }
        if (
            method.getName().equals("getDelegate") ||
            method.getName().equals("getEntityManagerFactory") ||
            method.getName().equals("getMetamodel") ||
            method.getName().equals("getSession")
        ) {
            return method.invoke(standardManager, objects);
        }
        if (
            method.getName().equals("createQuery") ||
            method.getName().equals("createNamedQuery") ||
            method.getName().equals("createNativeQuery") ||
            method.getName().equals("getCriteriaBuilder")
        ) {
            //TODO need to return a proxied version of query that is intelligent enough to return a mixed list
            return method.invoke(standardManager, objects);
        }
        if (method.getName().equals("getStandardManager")) {
            return standardManager;
        }
        if (method.getName().equals("getSandboxManager")) {
            return sandboxManager;
        }
        throw new SandBoxException("Unrecognized EntityManager method sent to proxy: " + method.getName());
    }
}
