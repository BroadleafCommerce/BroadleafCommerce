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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.audit.AuditableListener;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.persistence.PreviewStatus;
import org.broadleafcommerce.common.persistence.Previewable;
import org.broadleafcommerce.common.presentation.*;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@EntityListeners(value = { AuditableListener.class, CustomerPersistedEntityListener.class })
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CUSTOMER")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blCustomerElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "CustomerImpl_baseCustomer")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.PREVIEW, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
public class CustomerImpl implements Customer, AdminMainEntity, Previewable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CUSTOMER_ID")
    @AdminPresentation(friendlyName = "CustomerImpl_Customer_Id", group = CustomerAdminPresentation.GroupName.General,
            visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Embedded
    protected Auditable auditable = new Auditable();

    @Embedded
    protected PreviewStatus previewable = new PreviewStatus();

    @Column(name = "USER_NAME")
    @AdminPresentation(friendlyName = "CustomerImpl_UserName", order = 4000, group = CustomerAdminPresentation.GroupName.Customer,
            tab = CustomerAdminPresentation.TabName.General, tabOrder = CustomerAdminPresentation.TabOrder.General)
    protected String username;

    @Column(name = "PASSWORD")
    @AdminPresentation(excluded = true)
    protected String password;

    @Column(name = "EMAIL_ADDRESS")
    @Index(name = "CUSTOMER_EMAIL_INDEX", columnNames = { "EMAIL_ADDRESS" })
    @AdminPresentation(friendlyName = "CustomerImpl_Email_Address", order = 1000, group = CustomerAdminPresentation.GroupName.Customer,
            tab = CustomerAdminPresentation.TabName.General, tabOrder = CustomerAdminPresentation.TabOrder.General,
            prominent = true, gridOrder = 1000)
    protected String emailAddress;

    @Column(name = "FIRST_NAME")
    @AdminPresentation(friendlyName = "CustomerImpl_First_Name", order = 2000, group = CustomerAdminPresentation.GroupName.Customer,
            tab = CustomerAdminPresentation.TabName.General, tabOrder = CustomerAdminPresentation.TabOrder.General,
            prominent = true, gridOrder = 2000)
    protected String firstName;

    @Column(name = "LAST_NAME")
    @AdminPresentation(friendlyName = "CustomerImpl_Last_Name", order = 3000, group = CustomerAdminPresentation.GroupName.Customer,
            tab = CustomerAdminPresentation.TabName.General, tabOrder = CustomerAdminPresentation.TabOrder.General,
            prominent = true, gridOrder = 3000)
    protected String lastName;

    @ManyToOne(targetEntity = ChallengeQuestionImpl.class)
    @JoinColumn(name = "CHALLENGE_QUESTION_ID")
    @Index(name="CUSTOMER_CHALLENGE_INDEX", columnNames={"CHALLENGE_QUESTION_ID"})
    @AdminPresentation(friendlyName = "CustomerImpl_Challenge_Question", order = 4000,
            excluded = true)
    protected ChallengeQuestion challengeQuestion;

    @Column(name = "CHALLENGE_ANSWER")
    @AdminPresentation(friendlyName = "CustomerImpl_Challenge_Answer", order = 5000,
            excluded = true)
    protected String challengeAnswer;

    @Column(name = "PASSWORD_CHANGE_REQUIRED")
    @AdminPresentation(excluded = true,
            tab = CustomerAdminPresentation.TabName.General, tabOrder = CustomerAdminPresentation.TabOrder.General)
    protected Boolean passwordChangeRequired = false;

    @Column(name = "RECEIVE_EMAIL")
    @AdminPresentation(friendlyName = "CustomerImpl_Customer_Receive_Email",order=1000, group = CustomerAdminPresentation.GroupName.General,
            tab = CustomerAdminPresentation.TabName.General, tabOrder = CustomerAdminPresentation.TabOrder.General)
    protected Boolean receiveEmail = true;

    @Column(name = "IS_REGISTERED")
    @AdminPresentation(friendlyName = "CustomerImpl_Customer_Registered", order = 4000, group = CustomerAdminPresentation.GroupName.General,
            tab = CustomerAdminPresentation.TabName.General, tabOrder = CustomerAdminPresentation.TabOrder.General,
            prominent = true, gridOrder = 4000)
    protected Boolean registered = false;
    
    @Column(name = "DEACTIVATED")
    @AdminPresentation(friendlyName = "CustomerImpl_Customer_Deactivated", order=3000, group = CustomerAdminPresentation.GroupName.General,
            tab = CustomerAdminPresentation.TabName.General, tabOrder = CustomerAdminPresentation.TabOrder.General)
    protected Boolean deactivated = false;

    @ManyToOne(targetEntity = LocaleImpl.class)
    @JoinColumn(name = "LOCALE_CODE")
    @AdminPresentation(friendlyName = "CustomerImpl_Customer_Locale",order=4000,
        excluded = true, visibility = VisibilityEnum.GRID_HIDDEN)
    protected Locale customerLocale;
    
    @OneToMany(mappedBy = "customer", targetEntity = CustomerAttributeImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blStandardElements")
    @MapKey(name="name")
    @BatchSize(size = 50)
    @AdminPresentationMap(friendlyName = "CustomerAttributeImpl_Attribute_Name",
            deleteEntityUponRemove = true, forceFreeFormKeys = true, keyPropertyFriendlyName = "ProductAttributeImpl_Attribute_Name",
            tab = CustomerAdminPresentation.TabName.Advanced, tabOrder = CustomerAdminPresentation.TabOrder.Advanced
    )
    protected Map<String, CustomerAttribute> customerAttributes = new HashMap<String, CustomerAttribute>();

    @OneToMany(mappedBy = "customer", targetEntity = CustomerAddressImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @Where(clause = "archived != 'Y'")
    @AdminPresentationCollection(friendlyName = "CustomerImpl_Customer_Addresses", order = 1000,
            tab = CustomerAdminPresentation.TabName.ContactInfo, tabOrder = CustomerAdminPresentation.TabOrder.ContactInfo,
            addType = AddMethodType.PERSIST)
    protected List<CustomerAddress> customerAddresses = new ArrayList<CustomerAddress>();

    @OneToMany(mappedBy = "customer", targetEntity = CustomerPhoneImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @AdminPresentationCollection(friendlyName = "CustomerImpl_Customer_Phones", order = 2000,
            tab = CustomerAdminPresentation.TabName.ContactInfo, tabOrder = CustomerAdminPresentation.TabOrder.ContactInfo,
            addType = AddMethodType.PERSIST)
    protected List<CustomerPhone> customerPhones = new ArrayList<CustomerPhone>();

    @OneToMany(mappedBy = "customer", targetEntity = CustomerPaymentImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    @AdminPresentationCollection(friendlyName = "CustomerImpl_Customer_Payments", order = 3000,
            tab = CustomerAdminPresentation.TabName.ContactInfo, tabOrder = CustomerAdminPresentation.TabOrder.ContactInfo,
            addType = AddMethodType.PERSIST,
            readOnly = true)
    protected List<CustomerPayment> customerPayments  = new ArrayList<CustomerPayment>();

    @Column(name = "TAX_EXEMPTION_CODE")
    @AdminPresentation(friendlyName = "CustomerImpl_Customer_TaxExemptCode", order = 5000, group = CustomerAdminPresentation.GroupName.General,
            tab = CustomerAdminPresentation.TabName.General, tabOrder = CustomerAdminPresentation.TabOrder.General,
            visibility = VisibilityEnum.GRID_HIDDEN)
    protected String taxExemptionCode;

    @Transient
    protected String unencodedPassword;

    @Transient
    protected String unencodedChallengeAnswer;
    
    @Transient
    protected boolean anonymous;

    @Transient
    protected boolean cookied;

    @Transient
    protected boolean loggedIn;

    @Transient
    protected Map<String, Object> transientProperties = new HashMap<String, Object>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isPasswordChangeRequired() {
        return BooleanUtils.toBoolean(passwordChangeRequired);
    }

    @Override
    public void setPasswordChangeRequired(boolean passwordChangeRequired) {
        this.passwordChangeRequired = Boolean.valueOf(passwordChangeRequired);
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getEmailAddress() {
        return emailAddress;
    }

    @Override
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public ChallengeQuestion getChallengeQuestion() {
        return challengeQuestion;
    }

    @Override
    public void setChallengeQuestion(ChallengeQuestion challengeQuestion) {
        this.challengeQuestion = challengeQuestion;
    }

    @Override
    public String getChallengeAnswer() {
        return challengeAnswer;
    }

    @Override
    public void setChallengeAnswer(String challengeAnswer) {
        this.challengeAnswer = challengeAnswer;
    }

    @Override
    public String getUnencodedPassword() {
        return unencodedPassword;
    }

    @Override
    public void setUnencodedPassword(String unencodedPassword) {
        this.unencodedPassword = unencodedPassword;
    }

    @Override
    public boolean isReceiveEmail() {
        return BooleanUtils.toBoolean(receiveEmail);
    }

    @Override
    public void setReceiveEmail(boolean receiveEmail) {
        this.receiveEmail = Boolean.valueOf(receiveEmail);
    }

    @Override
    public boolean isRegistered() {
        return BooleanUtils.toBoolean(registered);
    }

    @Override
    public void setRegistered(boolean registered) {
        this.registered = Boolean.valueOf(registered);
    }

    @Override
    public String getUnencodedChallengeAnswer() {
        return unencodedChallengeAnswer;
    }

    @Override
    public void setUnencodedChallengeAnswer(String unencodedChallengeAnswer) {
        this.unencodedChallengeAnswer = unencodedChallengeAnswer;
    }

    @Override
    public Auditable getAuditable() {
        return auditable;
    }

    @Override
    public void setAuditable(Auditable auditable) {
        this.auditable = auditable;
    }

    @Override
    public boolean isAnonymous() {
        return anonymous;
    }

    @Override
    public boolean isCookied() {
        return cookied;
    }

    @Override
    public boolean isLoggedIn() {
        return loggedIn;
    }

    @Override
    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
        if (anonymous) {
            cookied = false;
            loggedIn = false;
        }
    }

    @Override
    public void setCookied(boolean cookied) {
        this.cookied = cookied;
        if (cookied) {
            anonymous = false;
            loggedIn = false;
        }
    }

    @Override
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
        if (loggedIn) {
            anonymous = false;
            cookied = false;
        }
    }

    @Override
    public Locale getCustomerLocale() {
        return customerLocale;
    }

    @Override
    public void setCustomerLocale(Locale customerLocale) {
        this.customerLocale = customerLocale;
    }

    @Override
    public Map<String, CustomerAttribute> getCustomerAttributes() {
        return customerAttributes;
    }

    @Override
    public void setCustomerAttributes(Map<String, CustomerAttribute> customerAttributes) {
        this.customerAttributes = customerAttributes;
    }

    @Override
    public boolean isDeactivated() {
        return BooleanUtils.toBoolean(deactivated);
    }

    @Override
    public void setDeactivated(boolean deactivated) {
        this.deactivated = Boolean.valueOf(deactivated);
    }

    @Override
    public List<CustomerAddress> getCustomerAddresses() {
        return customerAddresses;
    }

    @Override
    public void setCustomerAddresses(List<CustomerAddress> customerAddresses) {
        this.customerAddresses = customerAddresses;
    }

    @Override
    public List<CustomerPhone> getCustomerPhones() {
        return customerPhones;
    }

    @Override
    public void setCustomerPhones(List<CustomerPhone> customerPhones) {
        this.customerPhones = customerPhones;
    }

    @Override
    public List<CustomerPayment> getCustomerPayments() {
        return customerPayments;
    }

    @Override
    public void setCustomerPayments(List<CustomerPayment> customerPayments) {
        this.customerPayments = customerPayments;
    }

    @Override
    public String getMainEntityName() {
        if (!StringUtils.isEmpty(getFirstName()) && !StringUtils.isEmpty(getLastName())) {
            return getFirstName() + " " + getLastName();
        }
        return String.valueOf(getId());
    }

    @Override
    public Boolean getPreview() {
        if (previewable == null) {
            previewable = new PreviewStatus();
        }
        return previewable.getPreview();
    }

    @Override
    public void setPreview(Boolean preview) {
        if (previewable == null) {
            previewable = new PreviewStatus();
        }
        previewable.setPreview(preview);
    }

    @Override
    public Map<String, Object> getTransientProperties() {
        return transientProperties;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        CustomerImpl other = (CustomerImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!username.equals(other.username)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public <G extends Customer> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        Customer cloned = createResponse.getClone();
        cloned.setAnonymous(anonymous);
        cloned.setChallengeAnswer(challengeAnswer);
        cloned.setChallengeQuestion(challengeQuestion);
        cloned.setCookied(cookied);
        for(CustomerAddress entry : customerAddresses){
            CustomerAddress clonedEntry = entry.createOrRetrieveCopyInstance(context).getClone();
            clonedEntry.setCustomer(cloned);
            cloned.getCustomerAddresses().add(clonedEntry);

        }
        for(Map.Entry<String, CustomerAttribute> entry : customerAttributes.entrySet()){
            CustomerAttribute clonedEntry = entry.getValue().createOrRetrieveCopyInstance(context).getClone();
            clonedEntry.setCustomer(cloned);
            cloned.getCustomerAttributes().put(entry.getKey(),clonedEntry);
        }
        cloned.setLoggedIn(loggedIn);
        cloned.setUsername(username);
        cloned.setUnencodedPassword(unencodedPassword);
        cloned.setTaxExemptionCode(taxExemptionCode);
        cloned.setUnencodedChallengeAnswer(unencodedChallengeAnswer);
        cloned.setRegistered(registered);
        cloned.setReceiveEmail(receiveEmail);
        cloned.setPasswordChangeRequired(passwordChangeRequired);
        cloned.setPassword(password);
        cloned.setLastName(lastName);
        cloned.setFirstName(firstName);
        cloned.setEmailAddress(emailAddress);
        cloned.setDeactivated(deactivated);
        for(CustomerPayment entry : customerPayments){
            CustomerPayment clonedEntry = entry.createOrRetrieveCopyInstance(context).getClone();
            clonedEntry.setCustomer(cloned);
            cloned.getCustomerPayments().add(clonedEntry);
        }
        for(CustomerPhone entry : customerPhones){
            CustomerPhone clonedEntry = entry.createOrRetrieveCopyInstance(context).getClone();
            clonedEntry.setCustomer(cloned);
            cloned.getCustomerPhones().add(clonedEntry);
        }
        return createResponse;
    }

    @Override
    public String getTaxExemptionCode() {
        return this.taxExemptionCode;
    }

    @Override
    public void setTaxExemptionCode(String exemption) {
        this.taxExemptionCode = exemption;
    }
    
    @Override
    public boolean isTaxExempt() {
        return StringUtils.isNotEmpty(taxExemptionCode);
    }
    
}
