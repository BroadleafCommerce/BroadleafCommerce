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

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationOverride;
import org.broadleafcommerce.common.presentation.AdminPresentationOverrides;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.time.domain.TemporalTimestampListener;
import org.hibernate.annotations.Index;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

@Entity
@EntityListeners(value = { TemporalTimestampListener.class })
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CUSTOMER_ADDRESS", uniqueConstraints = @UniqueConstraint(columnNames = { "CUSTOMER_ID", "ADDRESS_NAME" }))
@AdminPresentationOverrides(
    {
        @AdminPresentationOverride(name="address.state.name", value=@AdminPresentation(friendlyName = "CustomerAddressImpl_State", visibility = VisibilityEnum.FORM_HIDDEN, order=9, group = "CustomerAddressImpl_Address", readOnly = true)),
        @AdminPresentationOverride(name="address.country.name", value=@AdminPresentation(friendlyName = "CustomerAddressImpl_Country", visibility = VisibilityEnum.FORM_HIDDEN, order=12, group = "CustomerAddressImpl_Address", readOnly = true)),
        @AdminPresentationOverride(name="address.firstName", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="address.lastName", value=@AdminPresentation(excluded = true))
    }
)
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE)
public class CustomerAddressImpl implements CustomerAddress {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "CustomerAddressId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "CustomerAddressId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "CustomerAddressImpl", allocationSize = 50)
    @Column(name = "CUSTOMER_ADDRESS_ID")
    protected Long id;

    @Column(name = "ADDRESS_NAME")
    @AdminPresentation(friendlyName = "CustomerAddressImpl_Address_Name", order=1, group = "CustomerAddressImpl_Identification", groupOrder = 1, requiredOverride = RequiredOverride.REQUIRED, fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.profile.core.service.type.CustomerAddressType")
    protected String addressName;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity = CustomerImpl.class, optional=false)
    @JoinColumn(name = "CUSTOMER_ID")
    @AdminPresentation(excluded = true, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Customer customer;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = AddressImpl.class, optional=false)
    @JoinColumn(name = "ADDRESS_ID")
    @Index(name="CUSTOMERADDRESS_ADDRESS_INDEX", columnNames={"ADDRESS_ID"})
    protected Address address;
    
    
    @Column(name = "DEFAULT_FLAG")
    @AdminPresentation(friendlyName = "CustomerAddressImpl_Default_Flag", order=2, group="CustomerAddressImpl_Identification")
    protected Boolean defaultAddressFlag; 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public Boolean getDefaultAddressFlag() {
    	if (defaultAddressFlag == null) {
    		return Boolean.FALSE;
    	}
		return defaultAddressFlag;
	}

    @Override
	public void setDefaultAddressFlag(Boolean defaultAddressFlag) {
		this.defaultAddressFlag = defaultAddressFlag;
	}

	@Override
    public String toString() {
    	return (addressName == null) 
        		? address.getFirstName() + " - " + address.getAddressLine1()
        		: addressName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + ((addressName == null) ? 0 : addressName.hashCode());
        result = prime * result + ((defaultAddressFlag == null) ? 0 : defaultAddressFlag.hashCode());
        result = prime * result + ((customer == null) ? 0 : customer.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CustomerAddressImpl other = (CustomerAddressImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        
        if (addressName == null) {
            if (other.addressName != null)
                return false;
        } else if (!addressName.equals(other.addressName))
            return false;
        
        if (defaultAddressFlag == null) {
            if (other.defaultAddressFlag != null)
                return false;
        } else if (!defaultAddressFlag.equals(other.defaultAddressFlag))
            return false;        
        
        if (customer == null) {
            if (other.customer != null)
                return false;
        } else if (!customer.equals(other.customer))
            return false;
        return true;
    }


}
