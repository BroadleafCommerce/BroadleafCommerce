/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.profile.core.service;

import org.broadleafcommerce.profile.core.domain.CustomerPhone;

import java.util.List;

public interface CustomerPhoneService {

    public CustomerPhone saveCustomerPhone(CustomerPhone customerPhone);

    public List<CustomerPhone> readActiveCustomerPhonesByCustomerId(Long customerId);

    public CustomerPhone readCustomerPhoneById(Long customerPhoneId);

    public void makeCustomerPhoneDefault(Long customerPhoneId, Long customerId);

    public void deleteCustomerPhoneById(Long customerPhoneId);

    public CustomerPhone findDefaultCustomerPhone(Long customerId);

    public List<CustomerPhone> readAllCustomerPhonesByCustomerId(Long customerId);

    public CustomerPhone create();

}
