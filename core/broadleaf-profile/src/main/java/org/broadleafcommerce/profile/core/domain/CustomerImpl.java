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

package org.broadleafcommerce.profile.core.domain;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.audit.AuditableListener;
import org.broadleafcommerce.common.audit.AuditExcludeFieldValue;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.persistence.PreviewStatus;
import org.broadleafcommerce.common.persistence.Previewable;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@EntityListeners(value = { AuditableListener.class, CustomerPersistedEntityListener.class })
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CUSTOMER")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blCustomerElements")
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "auditable.dateCreated", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.GROUP, overrideValue = CustomerAdminPresentation.GroupName.Audit)),
        @AdminPresentationMergeOverride(name = "auditable.createdBy", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.GROUP, overrideValue = CustomerAdminPresentation.GroupName.Audit)),
        @AdminPresentationMergeOverride(name = "auditable.dateUpdated", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.GROUP, overrideValue = CustomerAdminPresentation.GroupName.Audit)),
        @AdminPresentationMergeOverride(name = "auditable.updatedBy", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.GROUP, overrideValue = CustomerAdminPresentation.GroupName.Audit))
    }
)
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.PREVIEW),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.ARCHIVE_ONLY)
})
public class CustomerImpl implements Customer, AdminMainEntity, Previewable, CustomerAdminPresentation {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CUSTOMER_ID")
    @AdminPresentation(friendlyName = "CustomerImpl_Customer_Id", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Embedded
    protected Auditable auditable = new Auditable();

    @Embedded
    protected PreviewStatus previewable = new PreviewStatus();

    @Column(name = "USER_NAME")
    @AdminPresentation(friendlyName = "CustomerImpl_UserName",
            group = GroupName.Customer, order = FieldOrder.USERNAME,
            requiredOverride = RequiredOverride.REQUIRED)
    protected String username;

    @Column(name = "PASSWORD")
    @AdminPresentation(excluded = true)
    @AuditExcludeFieldValue
    protected String password;

    @Column(name = "EMAIL_ADDRESS")
    @Index(name = "CUSTOMER_EMAIL_INDEX", columnNames = { "EMAIL_ADDRESS" })
    @AdminPresentation(friendlyName = "CustomerImpl_Email_Address",
            group = GroupName.Customer, order = FieldOrder.EMAIL,
            prominent = true, gridOrder = 1000)
    protected String emailAddress;

    @Column(name = "FIRST_NAME")
    @AdminPresentation(friendlyName = "CustomerImpl_First_Name",
            group = GroupName.Customer, order = FieldOrder.FIRST_NAME,
            prominent = true, gridOrder = 2000)
    protected String firstName;

    @Column(name = "LAST_NAME")
    @AdminPresentation(friendlyName = "CustomerImpl_Last_Name",
            group = GroupName.Customer, order = FieldOrder.LAST_NAME,
            prominent = true, gridOrder = 3000)
    protected String lastName;

    @Column(name = "EXTERNAL_ID")
    @AdminPresentation(friendlyName = "CustomerImpl_Customer_ExternalId",
            group = GroupName.Customer, order = FieldOrder.EXTERNAL_ID,
            visibility = VisibilityEnum.GRID_HIDDEN)
    protected String externalId;

    @ManyToOne(targetEntity = ChallengeQuestionImpl.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "CHALLENGE_QUESTION_ID")
    @Index(name = "CUSTOMER_CHALLENGE_INDEX", columnNames = { "CHALLENGE_QUESTION_ID" })
    @AdminPresentation(friendlyName = "CustomerImpl_Challenge_Question",
            excluded = true)
    protected ChallengeQuestion challengeQuestion;

    @Column(name = "CHALLENGE_ANSWER")
    @AdminPresentation(friendlyName = "CustomerImpl_Challenge_Answer",
            excluded = true)
    protected String challengeAnswer;

    /**
     * <p>
     *     If true, this customer must go through a reset password flow.
     * </p>
     * <p>
     *     During a site conversion or security breach or a matter of routine security policy,
     *     it may be necessary to require users to change their password. This property will
     *     not allow a user whose credentials are managed within Broadleaf to login until
     *     they have reset their password.
     * </p>
     * <p>
     *     Used by blUserDetailsService.
     * </p>
     */
    @Column(name = "PASSWORD_CHANGE_REQUIRED")
    @AdminPresentation(excluded = true)
    protected Boolean passwordChangeRequired = false;

    @Column(name = "RECEIVE_EMAIL")
    @AdminPresentation(friendlyName = "CustomerImpl_Customer_Receive_Email",
            group = GroupName.QualificationOptions, order = FieldOrder.RECIEVE_EMAIL)
    protected Boolean receiveEmail = false;

    @Column(name = "IS_REGISTERED")
    @AdminPresentation(friendlyName = "CustomerImpl_Customer_Registered",
            group = GroupName.QualificationOptions, order = FieldOrder.REGISTERED,
            prominent = true, gridOrder = 4000)
    protected Boolean registered = false;

    @Column(name = "DEACTIVATED")
    @AdminPresentation(friendlyName = "CustomerImpl_Customer_Deactivated",
            group = GroupName.QualificationOptions, order = FieldOrder.DEACTIVATED)
    protected Boolean deactivated = false;

    @ManyToOne(targetEntity = LocaleImpl.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCALE_CODE")
    @AdminPresentation(friendlyName = "CustomerImpl_Customer_Locale",
        excluded = true, visibility = VisibilityEnum.GRID_HIDDEN)
    protected Locale customerLocale;

    @OneToMany(mappedBy = "customer", targetEntity = CustomerAttributeImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blStandardElements")
    @MapKey(name = "name")
    @BatchSize(size = 50)
    @AdminPresentationMap(friendlyName = "CustomerAttributeImpl_Attribute_Name",
            tab = CustomerAdminPresentation.TabName.General,
            deleteEntityUponRemove = true, forceFreeFormKeys = true,
            keyPropertyFriendlyName = "ProductAttributeImpl_Attribute_Name")
    protected Map<String, CustomerAttribute> customerAttributes = new HashMap<>();

    @OneToMany(mappedBy = "customer", targetEntity = CustomerAddressImpl.class, cascade = { CascadeType.ALL })
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
    @Where(clause = "archived != 'Y'")
    @AdminPresentationCollection(friendlyName = "CustomerImpl_Customer_Addresses",
            group = GroupName.ContactInfo, order = FieldOrder.ADDRESSES,
            addType = AddMethodType.PERSIST)
    protected List<CustomerAddress> customerAddresses = new ArrayList<>();

    @OneToMany(mappedBy = "customer", targetEntity = CustomerPhoneImpl.class, cascade = { CascadeType.ALL })
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
    @AdminPresentationCollection(friendlyName = "CustomerImpl_Customer_Phones",
            group = GroupName.ContactInfo, order = FieldOrder.PHONES,
            addType = AddMethodType.PERSIST)
    protected List<CustomerPhone> customerPhones = new ArrayList<>();

    @OneToMany(mappedBy = "customer", targetEntity = CustomerPaymentImpl.class, cascade = { CascadeType.ALL })
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
    @BatchSize(size = 50)
    @AdminPresentationCollection(friendlyName = "CustomerImpl_Customer_Payments",
            tab = TabName.PaymentMethods, order = 1000,
            addType = AddMethodType.PERSIST,
            readOnly = true)
    protected List<CustomerPayment> customerPayments = new ArrayList<>();

    @Column(name = "IS_TAX_EXEMPT")
    @AdminPresentation(friendlyName = "CustomerImpl_Is_Tax_Exempt",
            group = GroupName.Pricing, order = FieldOrder.IS_TAX_EXEMPT,
            defaultValue = "false")
    protected Boolean isTaxExempt = false;

    @Column(name = "TAX_EXEMPTION_CODE")
    @AdminPresentation(friendlyName = "CustomerImpl_Customer_TaxExemptCode",
            group = GroupName.Pricing, order = FieldOrder.TAX_EXEMPTION_CODE,
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
    public String getExternalId() {
        return externalId;
    }

    @Override
    public void setExternalId(String externalId) {
        this.externalId = externalId;
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
        if (!StringUtils.isEmpty(getUsername())) {
            return getUsername();
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
        for (CustomerAddress entry : customerAddresses) {
            CustomerAddress clonedEntry = entry.createOrRetrieveCopyInstance(context).getClone();
            clonedEntry.setCustomer(cloned);
            cloned.getCustomerAddresses().add(clonedEntry);

        }
        for (Map.Entry<String, CustomerAttribute> entry : customerAttributes.entrySet()) {
            CustomerAttribute clonedEntry = entry.getValue().createOrRetrieveCopyInstance(context).getClone();
            clonedEntry.setCustomer(cloned);
            cloned.getCustomerAttributes().put(entry.getKey(), clonedEntry);
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
        for (CustomerPayment entry : customerPayments) {
            CustomerPayment clonedEntry = entry.createOrRetrieveCopyInstance(context).getClone();
            clonedEntry.setCustomer(cloned);
            cloned.getCustomerPayments().add(clonedEntry);
        }
        for (CustomerPhone entry : customerPhones) {
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

        if (exemption != null) {
            this.isTaxExempt = true;
        }
    }

    @Override
    public boolean isTaxExempt() {
        return isTaxExempt != null && isTaxExempt != false &&  StringUtils.isNotEmpty(taxExemptionCode);
    }

}
