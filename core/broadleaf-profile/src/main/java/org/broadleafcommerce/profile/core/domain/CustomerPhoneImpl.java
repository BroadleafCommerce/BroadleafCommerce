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

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@EntityListeners(value = {CustomerPhonePersistedEntityListener.class})
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CUSTOMER_PHONE", uniqueConstraints = @UniqueConstraint(name="CSTMR_PHONE_UNIQUE_CNSTRNT", columnNames = { "CUSTOMER_ID", "PHONE_NAME" }))
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "phone.phoneNumber", mergeEntries = {
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.GROUP, overrideValue = CustomerPhoneAdminPresentation.GroupName.PhoneInfo),
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.ORDER, intOverrideValue = 2000),
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.PROMINENT, booleanOverrideValue = true)}),
        @AdminPresentationMergeOverride(name = "phone.isDefault", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.GROUP, overrideValue = CustomerPhoneAdminPresentation.GroupName.Defaults)),
        @AdminPresentationMergeOverride(name = "phone.countryCode", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.VISIBILITY, overrideValue = "HIDDEN_ALL")),
        @AdminPresentationMergeOverride(name = "phone.extension", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.VISIBILITY, overrideValue = "HIDDEN_ALL")),
        @AdminPresentationMergeOverride(name = "phone.isActive", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.VISIBILITY, overrideValue = "HIDDEN_ALL"))
    }
)
public class CustomerPhoneImpl implements CustomerPhone, CustomerPhoneAdminPresentation {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "CustomerPhoneId")
    @GenericGenerator(
        name="CustomerPhoneId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="CustomerPhoneImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.profile.core.domain.CustomerPhoneImpl")
        }
    )
    @Column(name = "CUSTOMER_PHONE_ID")
    protected Long id;

    @Column(name = "PHONE_NAME")
    @AdminPresentation(friendlyName = "CustomerPhoneImpl_Phone_Name",
            group = GroupName.PhoneInfo, order = FieldOrder.PHONE_NAME,
            groupOrder = 1, prominent = true, gridOrder = 1)
    protected String phoneName;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity = CustomerImpl.class, optional=false)
    @JoinColumn(name = "CUSTOMER_ID")
    @AdminPresentation(excluded = true, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Customer customer;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = PhoneImpl.class, optional=false)
    @JoinColumn(name = "PHONE_ID")
    @Index(name="CUSTPHONE_PHONE_INDEX", columnNames={"PHONE_ID"})
    protected Phone phone;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getPhoneName() {
        return phoneName;
    }

    @Override
    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public Phone getPhone() {
        return phone;
    }

    @Override
    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((customer == null) ? 0 : customer.hashCode());
        result = prime * result + ((phone == null) ? 0 : phone.hashCode());
        result = prime * result + ((phoneName == null) ? 0 : phoneName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        CustomerPhoneImpl other = (CustomerPhoneImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (customer == null) {
            if (other.customer != null)
                return false;
        } else if (!customer.equals(other.customer))
            return false;
        if (phone == null) {
            if (other.phone != null)
                return false;
        } else if (!phone.equals(other.phone))
            return false;
        if (phoneName == null) {
            if (other.phoneName != null)
                return false;
        } else if (!phoneName.equals(other.phoneName))
            return false;
        return true;
    }

    @Override
    public <G extends CustomerPhone> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        CustomerPhone cloned = createResponse.getClone();
        // dont clone
        cloned.setCustomer(customer);
        cloned.setPhoneName(phoneName);
        cloned.setPhone(phone);
        return createResponse;
    }
}
