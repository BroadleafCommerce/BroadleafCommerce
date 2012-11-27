package org.broadleafcommerce.profile.core.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author Jerry Ocanas (jocanas)
 */
public interface GroupMembership extends Serializable {

    public Long getId();

    public void setId(Long id);

    public CustomerGroup getCustomerGroup();

    public void setCustomerGroup(CustomerGroup customerGroup);

    public Customer getCustomer();

    public void setCustomer(Customer customer);

    public List<Role> getRoles();

    public void setRoles(List<Role> roles);
}
