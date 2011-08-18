package org.broadleafcommerce.openadmin.server.service.persistence.entitymanager;

import org.hibernate.ejb.HibernateEntityManager;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/2/11
 * Time: 4:53 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DualEntityManager {

    public HibernateEntityManager getStandardManager();

    public HibernateEntityManager getSandboxManager();

}
