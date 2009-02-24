package org.broadleafcommerce.profile.domain;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserRoleImpl implements UserRole, Serializable {

    private static final long serialVersionUID = 1L;

    protected Log logger = LogFactory.getLog(getClass());

    private Long id;

    private User user;

    private String roleName;

    public UserRoleImpl() {
    }

    public UserRoleImpl(User user, String roleName) {
        this.user = user;
        this.roleName = roleName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
