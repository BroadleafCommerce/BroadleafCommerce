package org.broadleafcommerce.profile.domain;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//@Entity
//@EntityListeners(value = { TemporalTimestampListener.class })
//@Table(name = "USER_ROLE")
public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
//    @Transient
    private final Log logger = LogFactory.getLog(getClass());

//    @Id
//    @GeneratedValue
//    @Column(name = "USER_ROLE_ID")
    private Long id;

//    @ManyToOne(targetEntity = BroadleafUser.class)
//    @JoinColumn(name = "USER_ID")
    private User user;

//    @Column(name = "ROLE_NAME")
    private String roleName;

    public UserRole() {
    }

    public UserRole(User user, String roleName) {
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
