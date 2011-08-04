package org.broadleafcommerce.openadmin.server.service.persistence.entitymanager;

import org.broadleafcommerce.openadmin.server.service.SandBoxContext;
import org.broadleafcommerce.openadmin.server.service.SandBoxMode;
import org.broadleafcommerce.openadmin.server.service.exception.SandBoxException;
import org.hibernate.ejb.HibernateEntityManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

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
    protected final PlatformTransactionManager standardTransactionManager;
    protected final PlatformTransactionManager sandboxTransactionManager;
    protected final HibernateCleaner cleaner;

	public BroadleafEntityManagerInvocationHandler(HibernateEntityManager standardManager, HibernateEntityManager sandboxManager, HibernateCleaner cleaner) {
		this.standardManager = standardManager;
		this.sandboxManager = sandboxManager;
        standardTransactionManager = new org.springframework.orm.jpa.JpaTransactionManager(standardManager.getEntityManagerFactory());
        sandboxTransactionManager = new org.springframework.orm.jpa.JpaTransactionManager(sandboxManager.getEntityManagerFactory());
        this.cleaner = cleaner;
	}

    protected Object executeInTransaction(Executable executable, PlatformTransactionManager txManager) throws Throwable {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("SandBoxTx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        Object response;
        TransactionStatus status = txManager.getTransaction(def);
        try {
          response = executable.execute();
        } catch (Throwable ex) {
          txManager.rollback(status);
          throw ex;
        }
        txManager.commit(status);

        return response;
    }

    @Override
    public Object invoke(Object o, final Method method, final Object[] objects) throws Throwable {
        SandBoxContext context = SandBoxContext.getSandBoxContext();
        boolean isSandBox = context != null && context.getSandBoxMode() == SandBoxMode.SANDBOX_COMMIT;
        if (
            method.getName().equals("merge") ||
            method.getName().equals("persist")
        ) {
            if (isSandBox) {
                Object[] converted = new Object[]{cleaner.convertBean(objects[0], method, sandboxManager, sandboxTransactionManager)};
                return converted[0];
            } else {
                return executeInTransaction(new Executable() {
                    @Override
                    public Object execute() throws Throwable {
                        return method.invoke(standardManager, objects);
                    }
                }, standardTransactionManager);
            }
        }
        if (
            method.getName().equals("remove") ||
            method.getName().equals("flush") ||
            method.getName().equals("lock")
        ) {
            if (isSandBox) {
                return executeInTransaction(new Executable() {
                    @Override
                    public Object execute() throws Throwable {
                        return method.invoke(sandboxManager, objects);
                    }
                }, sandboxTransactionManager);
            } else {
                return executeInTransaction(new Executable() {
                    @Override
                    public Object execute() throws Throwable {
                        return method.invoke(standardManager, objects);
                    }
                }, standardTransactionManager);
            }
        }
        if (
            method.getName().equals("setFlushMode") ||
            method.getName().equals("getFlushMode") ||
            method.getName().equals("getLockMode") ||
            method.getName().equals("joinTransaction") ||
            method.getName().equals("getTransaction")
        ) {
            if (isSandBox) {
                return method.invoke(sandboxManager, objects);
            } else {
                return method.invoke(standardManager, objects);
            }
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

    public interface Executable {

        public Object execute() throws Throwable;

    }
}
