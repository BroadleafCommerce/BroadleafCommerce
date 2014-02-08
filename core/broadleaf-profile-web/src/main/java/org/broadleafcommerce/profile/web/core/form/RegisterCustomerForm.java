/*
 * #%L
 * BroadleafCommerce Profile Web
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
package org.broadleafcommerce.profile.web.core.form;

import org.broadleafcommerce.profile.core.domain.Customer;

import java.io.Serializable;

public class RegisterCustomerForm implements Serializable {
    protected static final long serialVersionUID = 1L;

    protected Customer customer;
    protected String password;
    protected String passwordConfirm;
    protected String redirectUrl;

    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPasswordConfirm() {
        return passwordConfirm;
    }
    
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
    
    public String getRedirectUrl() {
        return redirectUrl;
    }
    
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
    
}
