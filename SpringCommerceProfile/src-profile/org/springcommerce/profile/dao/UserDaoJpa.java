package org.springcommerce.profile.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springcommerce.profile.domain.User;
import org.springcommerce.profile.domain.UserRole;
import org.springframework.stereotype.Repository;

@Repository("userDao")
public class UserDaoJpa implements UserDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext
    private EntityManager em;

    public User readUserByUsername(String username) {
        return em.find(User.class, username);
    }

    @SuppressWarnings("unchecked")
    public List<UserRole> readRolesByUserId(Long userId) {
        Query query = em.createNamedQuery("READ_ROLES_BY_USER_ID");
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public User maintainUser(User user) {
        if (user.getId() == null) {
            em.persist(user);
        } else {
            user = em.merge(user);
        }
        return user;
    }
}
