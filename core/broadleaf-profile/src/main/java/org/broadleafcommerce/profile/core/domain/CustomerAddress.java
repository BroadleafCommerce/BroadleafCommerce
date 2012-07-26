/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.profile.core.domain;

import java.io.Serializable;

public interface CustomerAddress extends Serializable {

    public void setId(Long id);

    public Long getId();

    public void setAddressName(String addressName);

    public String getAddressName();

    public Customer getCustomer();

    public void setCustomer(Customer customer);

    public Address getAddress();

    public void setAddress(Address address);
    
    /**
     * If set to true, this address is the address that will populate the address on the checkout form
     * for a logged in user.
     * 
     * @return
     */
    public Boolean getDefaultAddressFlag();

    /**
     * Used to set the indicator that the current address should be treated as the default address.
     * The behavior of the system is undefined if there are multiple default addresses for 
     * a customer.
     * 
     * @param defaultAddressFlag
     */
	public void setDefaultAddressFlag(Boolean defaultAddressFlag);
	

}
