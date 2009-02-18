package org.broadleafcommerce.profile.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.User;
import org.broadleafcommerce.profile.domain.UserRole;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

public class UserDaoJpa implements UserDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    private JpaTemplate jpaTemplate;

    private EntityConfiguration entityConfiguration;

    public User readUserByUsername(final String username) {
        return (User) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNamedQuery("READ_USER_BY_USER_NAME");
                query.setParameter("username", username);
                try {
                    return query.getSingleResult();
                } catch (NoResultException ne) {
                    return null;
                }
            }
        });
    }

    public User readUserByEmail(final String emailAddress) {
        return (User) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNamedQuery("READ_USER_BY_EMAIL");
                query.setParameter("email", emailAddress);
                try {
                    return query.getSingleResult();
                } catch (NoResultException ne) {
                    return null;
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<UserRole> readUserRolesByUserId(final Long userId) {
        return (List<UserRole>) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNamedQuery("READ_ROLES_BY_USER_ID");
                query.setParameter("userId", userId);
                return query.getResultList();
            }
        });
    }

    public User maintainUser(final User user) {
        return (User) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                User retUser = user;
                if (retUser.getId() == null) {
                    em.persist(retUser);
                } else {
                    retUser = em.merge(retUser);
                }
                return retUser;
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public User readUserById(final Long id) {
        return (User) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                return em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.domain.User"), id);
            }
        });
    }

    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.jpaTemplate = new JpaTemplate(emf);
    }

    public void setEntityConfiguration(EntityConfiguration entityConfiguration) {
        this.entityConfiguration = entityConfiguration;
    }
}
