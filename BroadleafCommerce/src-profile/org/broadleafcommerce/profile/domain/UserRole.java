package org.broadleafcommerce.profile.domain;

public interface UserRole {

    public Long getId();

    public void setId(Long id);

    public User getUser();

    public void setUser(User user);

    public String getRoleName();

    public void setRoleName(String roleName);
}
