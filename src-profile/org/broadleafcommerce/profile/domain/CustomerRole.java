package org.broadleafcommerce.profile.domain;

public interface CustomerRole {

    public Long getId();

    public void setId(Long id);

    public Customer getCustomer();

    public void setCustomer(Customer customer);

    public Role getRole();

    public void setRole(Role role);

    public String getRoleName();
}
