package org.broadleafcommerce.core.inventory.domain;

/**
 * Copyright 2012 the original author or authors.
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

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverrides;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table(name = "BLC_FULFILLMENT_LOCATION")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@Inheritance(strategy = InheritanceType.JOINED)
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "FulfillmentLocationImpl_baseFulfillmentLocation")
@AdminPresentationOverrides(
        {
                @AdminPresentationOverride(name="address.addressLine1", value = @AdminPresentation(friendlyName="AddressImpl_Address_1", prominent = true))
        }
)
public class FulfillmentLocationImpl implements FulfillmentLocation {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "FulfillmentLocationId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "FulfillmentLocationId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "FulfillmentLocationImpl", allocationSize = 50)
    @Column(name = "FULFILLMENT_LOCATION_ID")
    protected Long id;

    @OneToOne(cascade = CascadeType.ALL, targetEntity = AddressImpl.class, optional = false)
    @JoinColumn(name = "ADDRESS_ID")
    protected Address address;

    @Column(name = "PICKUP_LOCATION", nullable = false)
    @AdminPresentation(friendlyName = "FulfillmentLocationImpl_pickupLocation", prominent = true)
    protected Boolean pickupLocation = Boolean.FALSE;

    @Column(name = "SHIPPING_LOCATION", nullable = false)
    @AdminPresentation(friendlyName = "FulfillmentLocationImpl_shippingLocation", prominent = true)
    protected Boolean shippingLocation = Boolean.TRUE;

    @Column(name = "DEFAULT_LOCATION", nullable = false)
    @AdminPresentation(friendlyName = "FulfillmentLocationImpl_defaultLocation", prominent = true)
    protected Boolean defaultLocation = Boolean.FALSE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Boolean getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Boolean pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Boolean getShippingLocation() {
        return shippingLocation;
    }

    public void setShippingLocation(Boolean shippingLocation) {
        this.shippingLocation = shippingLocation;
    }

    public Boolean getDefaultLocation() {
        return defaultLocation;
    }

    public void setDefaultLocation(Boolean defaultLocation) {
        this.defaultLocation = defaultLocation;
    }
}
