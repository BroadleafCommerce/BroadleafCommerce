package org.broadleafcommerce.profile.service;

import java.util.List;

import org.broadleafcommerce.profile.domain.CustomerRole;

public interface RoleService {

    public List<CustomerRole> findCustomerRolesByCustomerId(Long customerId);
}