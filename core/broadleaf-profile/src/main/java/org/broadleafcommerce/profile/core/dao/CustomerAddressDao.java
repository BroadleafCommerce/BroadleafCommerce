/*-
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

    List<CustomerAddress> readActiveCustomerAddressesByCustomerId(Long customerId);

    CustomerAddress save(CustomerAddress customerAddress);

    CustomerAddress readCustomerAddressById(Long customerAddressId);

    CustomerAddress readCustomerAddressByIdAndCustomerId(Long customerAddressId, Long customerId);

    void makeCustomerAddressDefault(Long customerAddressId, Long customerId);

    void deleteCustomerAddressById(Long customerAddressId);

    CustomerAddress findDefaultCustomerAddress(Long customerId);

    CustomerAddress create();

    List<CustomerAddress> readBatchCustomerAddresses(int start, int pageSize);

    Long readNumberOfAddresses();

    void hardDeleteCustomerAddressesForCustomer(Long customerId);

}
