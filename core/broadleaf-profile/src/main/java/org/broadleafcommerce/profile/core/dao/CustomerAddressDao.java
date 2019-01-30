/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.profile.core.dao;

import org.broadleafcommerce.profile.core.domain.CustomerAddress;

import java.util.List;

public interface CustomerAddressDao {

    public List<CustomerAddress> readActiveCustomerAddressesByCustomerId(Long customerId);

    public CustomerAddress save(CustomerAddress customerAddress);

    public CustomerAddress readCustomerAddressById(Long customerAddressId);

    public void makeCustomerAddressDefault(Long customerAddressId, Long customerId);

    public void deleteCustomerAddressById(Long customerAddressId);

    public CustomerAddress findDefaultCustomerAddress(Long customerId);

    public CustomerAddress create();

    public List<CustomerAddress> readBatchCustomerAddresses(int start, int pageSize);

    Long readNumberOfAddresses();

}
