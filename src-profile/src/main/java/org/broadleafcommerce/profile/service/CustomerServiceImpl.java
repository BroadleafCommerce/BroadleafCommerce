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
package org.broadleafcommerce.profile.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.profile.dao.CustomerDao;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.listener.PostRegistrationObserver;
import org.broadleafcommerce.profile.util.PasswordChange;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("blCustomerService")
public class CustomerServiceImpl implements CustomerService {

    @Resource(name="blCustomerDao")
    protected CustomerDao customerDao;

    @Resource(name="blIdGenerationService")
    protected IdGenerationService idGenerationService;

    @Resource(name="passwordEncoder")
    protected PasswordEncoder passwordEncoder;

    private final List<PostRegistrationObserver> postRegisterListeners = new ArrayList<PostRegistrationObserver>();

    public Customer saveCustomer(Customer customer) {
        return saveCustomer(customer, true);
    }

    public Customer saveCustomer(Customer customer, boolean register) {
        if (register && !customer.isRegistered()) {
            customer.setRegistered(true);
        }
        if (customer.getUnencodedPassword() != null) {
            customer.setPassword(passwordEncoder.encodePassword(customer.getUnencodedPassword(), null));
        }

        // let's make sure they entered a new challenge answer (we will populate
        // the password field with hashed values so check that they have changed
        // id
        if (customer.getUnencodedChallengeAnswer() != null && !customer.getUnencodedChallengeAnswer().equals(customer.getChallengeAnswer())) {
            customer.setChallengeAnswer(passwordEncoder.encodePassword(customer.getUnencodedChallengeAnswer(), null));
        }
        return customerDao.save(customer);
    }

    public Customer registerCustomer(Customer customer, String password, String passwordConfirm) {
        // TODO: Service level validation
        customer.setRegistered(true);

        // When unencodedPassword is set the save() will encode it
        if (customer.getId() == null) {
            customer.setId(idGenerationService.findNextId("org.broadleafcommerce.profile.domain.Customer"));
        }
        customer.setUnencodedPassword(password);
        Customer retCustomer = saveCustomer(customer);
        notifyPostRegisterListeners(retCustomer);
        return retCustomer;
    }

    public Customer readCustomerByEmail(String emailAddress) {
        return customerDao.readCustomerByEmail(emailAddress);
    }

    public Customer changePassword(PasswordChange passwordChange) {
        Customer customer = readCustomerByUsername(passwordChange.getUsername());
        customer.setUnencodedPassword(passwordChange.getNewPassword());
        customer.setPasswordChangeRequired(passwordChange.getPasswordChangeRequired());
        customer = saveCustomer(customer);
        return customer;
    }

    public void addPostRegisterListener(PostRegistrationObserver postRegisterListeners) {
        this.postRegisterListeners.add(postRegisterListeners);
    }

    public void removePostRegisterListener(PostRegistrationObserver postRegisterListeners) {
        if (this.postRegisterListeners.contains(postRegisterListeners)) {
            this.postRegisterListeners.remove(postRegisterListeners);
        }
    }

    protected void notifyPostRegisterListeners(Customer customer) {
        for (Iterator<PostRegistrationObserver> iter = postRegisterListeners.iterator(); iter.hasNext();) {
            PostRegistrationObserver listener = iter.next();
            listener.processRegistrationEvent(customer);
        }
    }

    public Customer createCustomerFromId(Long customerId) {
        Customer customer = customerId != null ? readCustomerById(customerId) : null;
        if (customer == null) {
            customer = customerDao.create();
            if (customerId != null) {
                customer.setId(customerId);
            } else {
                customer.setId(idGenerationService.findNextId("org.broadleafcommerce.profile.domain.Customer"));
            }
        }
        return customer;
    }

    public Customer readCustomerByUsername(String username) {
        return customerDao.readCustomerByUsername(username);
    }

    public Customer readCustomerById(Long id) {
        return customerDao.readCustomerById(id);
    }

    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
