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
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.broadleafcommerce.profile.util.PasswordChange;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("blCustomerService")
public class CustomerServiceImpl implements CustomerService {

    @Resource
    protected CustomerDao customerDao;

    @Resource
    protected IdGenerationService idGenerationService;

    @Resource
    protected PasswordEncoder passwordEncoder;

    @Resource
    protected EntityConfiguration entityConfiguration;

    /*
     * TODO cannot reference beans defined in the spring security application context here.
     * The bean visibility is in the wrong direction - perhaps.
     */
    /*@Resource
    protected EmailService emailService;

    @Resource
    protected ProviderManager authenticationManager;

    @Resource(name="blUserDetailsService")
    protected UserDetailsService userDetailsService;

    @Resource(name="blRegistrationEmailInfo")
    protected EmailInfo emailInfo;*/

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
        /*this.sendConfirmationEmail(retCustomer);

        HashMap<String, Object> emailDataMap = new HashMap<String, Object>();
        emailDataMap.put("customer", retCustomer);
        emailService.sendTemplateEmail(retCustomer.getEmailAddress(), emailInfo, emailDataMap);
        authenticateUser(customer.getUsername(), password);*/
        return retCustomer;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Customer readCustomerByEmail(String emailAddress) {
        return customerDao.readCustomerByEmail(emailAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Customer changePassword(PasswordChange passwordChange) {
        Customer customer = readCustomerByUsername(passwordChange.getUsername());
        customer.setUnencodedPassword(passwordChange.getNewPassword());
        customer.setPasswordChangeRequired(passwordChange.getPasswordChangeRequired());
        customer = saveCustomer(customer);
        //authenticateUser(passwordChange.getUsername(), passwordChange.getNewPassword());
        return customer;
    }

    /*protected void authenticateUser(String username, String password) {
        UserDetails principal = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, password, principal.getAuthorities());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }*/

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

    /*protected void sendConfirmationEmail(Customer customer) {
        if (emailInfo == null || emailInfo instanceof NullEmailInfo) {
            logger.info("Customer Registration Email not being sent because blRegistrationEmailInfo is not configured");
            return;
        }

        EmailTarget target = new EmailTargetImpl(){};
        target.setEmailAddress(customer.getEmailAddress());
        HashMap<String, Object> props = new HashMap<String, Object>();

        emailService.sendTemplateEmail(target, emailInfo, props);
    }*/

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

    @Transactional(propagation = Propagation.REQUIRED)
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
