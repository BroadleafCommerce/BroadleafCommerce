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
package org.broadleafcommerce.profile.core.domain;

import java.io.Serializable;

public interface CustomerPhone extends Serializable {

    public void setId(Long id);

    public Long getId();

    public void setPhoneName(String phoneName);

    public String getPhoneName();

    public Customer getCustomer();

    public void setCustomer(Customer customer);

    public Phone getPhone();

    public void setPhone(Phone phone);

}
