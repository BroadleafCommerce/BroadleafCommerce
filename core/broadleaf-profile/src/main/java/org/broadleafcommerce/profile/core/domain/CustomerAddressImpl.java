/*-
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
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
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blCustomerElements")
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

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity = CustomerImpl.class, optional=false)
    @JoinColumn(name = "CUSTOMER_ID")
    @AdminPresentation(excluded = true, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Customer customer;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, targetEntity = AddressImpl.class, optional=false)
    @JoinColumn(name = "ADDRESS_ID")
    @Index(name="CUSTOMERADDRESS_ADDRESS_INDEX", columnNames={"ADDRESS_ID"})
    protected Address address;

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
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return (addressName == null) 
                ? address.getFirstName() + " - " + address.getAddressLine1()
                : addressName;
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
        result = prime * result + ((address == null) ? 0 : address.hashCode());
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

        if (address == null) {
            if (other.address != null) {
                return false;
            }
        } else if (!address.equals(other.address)) {
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


    @Override
    public <G extends CustomerAddress> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        CustomerAddress cloned = createResponse.getClone();
        cloned.setAddressName(addressName);
        // dont clone
        cloned.setCustomer(customer);
        cloned.setArchived(getArchived());
        cloned.setAddress(address.createOrRetrieveCopyInstance(context).getClone());
        return createResponse;
    }
}
