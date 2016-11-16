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

import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.common.time.domain.TemporalTimestampListener;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.SQLDelete;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@EntityListeners(value = { TemporalTimestampListener.class, CustomerAddressPersistedEntityListener.class })
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CUSTOMER_ADDRESS")
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "address.firstName", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED, booleanOverrideValue = true)),
        @AdminPresentationMergeOverride(name = "address.lastName", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED, booleanOverrideValue = true)),
        @AdminPresentationMergeOverride(name = "address.addressLine1", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.PROMINENT, booleanOverrideValue = true))
    }
)
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE)
@SQLDelete(sql = "UPDATE BLC_CUSTOMER_ADDRESS SET ARCHIVED = 'Y' WHERE CUSTOMER_ADDRESS_ID = ?")
public class CustomerAddressImpl implements CustomerAddress {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "CustomerAddressId")
    @GenericGenerator(
        name="CustomerAddressId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="CustomerAddressImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.profile.core.domain.CustomerAddressImpl")
        }
    )
    @Column(name = "CUSTOMER_ADDRESS_ID")
    protected Long id;

    @Column(name = "ADDRESS_NAME")
    @AdminPresentation(friendlyName = "CustomerAddressImpl_Address_Name", order=1,
            group = "CustomerAddressImpl_Identification", groupOrder = 1, prominent = true, gridOrder = 1)
    protected String addressName;

    @Column(name = "IS_DEFAULT")
    @AdminPresentation(friendlyName = "CustomerAddressImpl_Default_Address", order=160, group = "CustomerAddressImpl_Address")
    protected boolean isDefault = false;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity = CustomerImpl.class, optional=false)
    @JoinColumn(name = "CUSTOMER_ID")
    @AdminPresentation(excluded = true, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Customer customer;

    @Column(name = "ADDRESS_EXTERNAL_ID")
    @Index(name="CUSTOMERADDRESS_ADDRESS_INDEX", columnNames={"ADDRESS_EXTERNAL_ID"})
    protected Long addressExternalId;

    @Embedded
    protected ArchiveStatus archiveStatus = new ArchiveStatus();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getAddressName() {
        return addressName;
    }

    @Override
    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    @Override
    public boolean isDefault() { return isDefault; }

    @Override
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    @Override
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public Long getAddressExternalId() {
        return addressExternalId;
    }

    @Override
    public void setAddressExternalId(Long addressReferenceId) {
        this.addressExternalId = addressReferenceId;
    }

    @Override
    public String toString() {
        return (addressName == null) 
                ? addressExternalId.toString() : addressExternalId + " - " + addressName;
    }

    @Override
    public Character getArchived() {
       ArchiveStatus temp;
       if (archiveStatus == null) {
           temp = new ArchiveStatus();
       } else {
           temp = archiveStatus;
       }
       return temp.getArchived();
    }

    @Override
    public void setArchived(Character archived) {
        if (archiveStatus == null) {
            archiveStatus = new ArchiveStatus();
        }
        archiveStatus.setArchived(archived);
    }

    @Override
    public boolean isActive() {
        return 'Y'!=getArchived();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((addressExternalId == null) ? 0 : addressExternalId.hashCode());
        result = prime * result + ((addressName == null) ? 0 : addressName.hashCode());
        result = prime * result + ((customer == null) ? 0 : customer.hashCode());
        return result;
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
        CustomerAddressImpl other = (CustomerAddressImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (addressExternalId == null) {
            if (other.addressExternalId != null) {
                return false;
            }
        } else if (!addressExternalId.equals(other.addressExternalId)) {
            return false;
        }
        
        if (addressName == null) {
            if (other.addressName != null) {
                return false;
            }
        } else if (!addressName.equals(other.addressName)) {
            return false;
        }
        
        if (customer == null) {
            if (other.customer != null) {
                return false;
            }
        } else if (!customer.equals(other.customer)) {
            return false;
        }
        return true;
    }

//TODO: microservices - deal with multitenant cloneable
//    @Override
//    public <G extends CustomerAddress> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
//        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
//        if (createResponse.isAlreadyPopulated()) {
//            return createResponse;
//        }
//        CustomerAddress cloned = createResponse.getClone();
//        cloned.setAddressName(addressName);
//        // dont clone
//        cloned.setCustomer(customer);
//        cloned.setArchived(getArchived());
//        cloned.setAddress(address.createOrRetrieveCopyInstance(context).getClone());
//        return createResponse;
//    }
}
