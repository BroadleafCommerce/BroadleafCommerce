package org.broadleafcommerce.profile.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.User;
import org.broadleafcommerce.profile.domain.UserRole;
import org.springframework.stereotype.Repository;

@Repository("userDao")
public class UserDaoJpa implements UserDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext
    private EntityManager em;

    public User readUserByUsername(String username) {
        Query query = em.createNamedQuery("READ_USER_BY_USER_NAME");
        query.setParameter("username", username);
        try{
            return (User) query.getSingleResult();
        }catch(NoResultException ne){
            return null;
        }
    }
    public User readUserByEmail(String emailAddress){
        Query query = em.createNamedQuery("READ_USER_BY_EMAIL");
        query.setParameter("email", emailAddress);
        try{
            return (User) query.getSingleResult();
        }catch(NoResultException ne){
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<UserRole> readUserRolesByUserId(Long userId) {
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

    public User readUserById(Long userId) {
        return em.find(User.class, userId);
    }
}
