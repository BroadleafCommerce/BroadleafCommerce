package org.broadleafcommerce.profile.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.User;
import org.broadleafcommerce.profile.domain.UserRole;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("userDao")
public class UserDaoJpa implements UserDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName="blPU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    public User readUserByUsername(String username) {
        Query query = em.createNamedQuery("BC_sREAD_USER_BY_USER_NAME");
        query.setParameter("username", username);
        try {
            return (User) query.getSingleResult();
        } catch (NoResultException ne) {
            return null;
        }
    }

    public User readUserByEmail(String emailAddress) {
        Query query = em.createNamedQuery("BC_READ_USER_BY_EMAIL");
        query.setParameter("email", emailAddress);
        try {
            return (User) query.getSingleResult();
        } catch (NoResultException ne) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<UserRole> readUserRolesByUserId(Long userId) {
        Query query = em.createNamedQuery("BC_READ_ROLES_BY_USER_ID");
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public User save(User user) {
        User retUser = user;
        if (retUser.getId() == null) {
            em.persist(retUser);
        } else {
            retUser = em.merge(retUser);
        }
        return retUser;
    }

    @SuppressWarnings("unchecked")
    public User readUserById(Long id) {
        return (User) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.domain.User"), id);
    }
}
