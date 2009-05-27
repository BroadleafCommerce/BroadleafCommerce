package org.broadleafcommerce.profile.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.profile.dao.RoleDao;
import org.broadleafcommerce.profile.domain.CustomerRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("roleService")
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleDao roleDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<CustomerRole> findCustomerRolesByCustomerId(Long customerId) {
        return roleDao.readCustomerRolesByCustomerId(customerId);
    }
}