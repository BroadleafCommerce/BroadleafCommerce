package org.broadleafcommerce.profile.dao;

import java.util.List;

import org.broadleafcommerce.profile.domain.CustomerRole;

public interface RoleDao {

    public List<CustomerRole> readCustomerRolesByCustomerId(Long customerId);
}
