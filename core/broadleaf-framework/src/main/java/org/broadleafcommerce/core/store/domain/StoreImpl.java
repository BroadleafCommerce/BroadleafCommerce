/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.store.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "BLC_STORE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "StoreImpl_baseStore")
@Inheritance(strategy = InheritanceType.JOINED)
@SQLDelete(sql="UPDATE BLC_STORE SET ARCHIVED = 'Y' WHERE STORE_ID = ?")
public class StoreImpl implements Store {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "StoreId")
    @GenericGenerator(
            name="StoreId",
            strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                    @Parameter(name="segment_value", value="StoreImpl"),
                    @Parameter(name="entity_name", value="org.broadleafcommerce.core.store.domain.StoreImpl")
            }
    )
    @Column(name = "STORE_ID", nullable = false)
    @AdminPresentation(friendlyName = "StoreImpl_Store_ID", visibility = VisibilityEnum.HIDDEN_ALL)
    private Long id;

    @Column(name = "STORE_NAME", nullable = false)
    @AdminPresentation(friendlyName = "StoreImpl_Store_Name", order = Presentation.FieldOrder.NAME,
            group = Presentation.Group.Name.General, groupOrder = Presentation.Group.Order.General,
            prominent = true, gridOrder = 1, columnWidth = "200px",
            requiredOverride = RequiredOverride.REQUIRED)
    private String name;


    @Column(name = "ADDRESS_1")
    @AdminPresentation(friendlyName = "StoreImpl_address1", order = Presentation.FieldOrder.ADDRESS_1,
            group = Presentation.Group.Name.Location, groupOrder = Presentation.Group.Order.Location,
            gridOrder = 2, columnWidth = "200px")
    private String address1;

    @Column(name = "ADDRESS_2")
    @AdminPresentation(friendlyName = "StoreImpl_address2", order = Presentation.FieldOrder.ADDRESS_2,
            group = Presentation.Group.Name.Location, groupOrder = Presentation.Group.Order.Location,
            gridOrder = 3, columnWidth = "200px")
    private String address2;

    @Column(name = "STORE_CITY")
    @AdminPresentation(friendlyName = "StoreImpl_city", order = Presentation.FieldOrder.CITY,
            group = Presentation.Group.Name.Location, groupOrder = Presentation.Group.Order.Location,
            prominent = true, gridOrder = 4)
    private String city;

    @Column(name = "STORE_STATE")
    @AdminPresentation(friendlyName = "StoreImpl_State", order = Presentation.FieldOrder.STATE,
            group = Presentation.Group.Name.Location, groupOrder = Presentation.Group.Order.Location,
            prominent = true, gridOrder = 5)
    private String state;

    @Column(name = "STORE_ZIP")
    @AdminPresentation(friendlyName = "StoreImpl_Zip", order = Presentation.FieldOrder.ZIP,
            group = Presentation.Group.Name.Location, groupOrder = Presentation.Group.Order.Location,
            prominent = true, gridOrder = 6)
    private String zip;

    @Column(name = "STORE_COUNTRY")
    @AdminPresentation(friendlyName = "StoreImpl_Country", order = Presentation.FieldOrder.COUNTRY,
            group = Presentation.Group.Name.Location, groupOrder = Presentation.Group.Order.Location,
            gridOrder = 7, columnWidth = "200px")
    private String country;

    @Column(name = "STORE_PHONE")
    @AdminPresentation(friendlyName = "StoreImpl_Phone", order = Presentation.FieldOrder.PHONE,
            group = Presentation.Group.Name.Location, groupOrder = Presentation.Group.Order.Location,
            gridOrder = 8, columnWidth = "200px")
    private String phone;

    @Column(name = "LATITUDE")
    @AdminPresentation(friendlyName = "StoreImpl_lat", order = Presentation.FieldOrder.LATITUDE,
            tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced,
            group = Presentation.Group.Name.Geocoding, groupOrder = Presentation.Group.Order.Geocoding,
            gridOrder = 9, columnWidth = "200px")
    private Double latitude;

    @Column(name = "LONGITUDE")
    @AdminPresentation(friendlyName = "StoreImpl_lng", order = Presentation.FieldOrder.LONGITUDE,
            tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced,
            group = Presentation.Group.Name.Geocoding, groupOrder = Presentation.Group.Order.Geocoding,
            gridOrder = 10, columnWidth = "200px")
    private Double longitude;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getId()
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getId()
     */
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setId(java.lang.Long)
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getName()
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setName(java.lang.String)
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getAddress1()
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getAddress1()
     */
    public String getAddress1() {
        return address1;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setAddress1(java.lang.String)
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setAddress1(java.lang.String)
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getAddress2()
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getAddress2()
     */
    public String getAddress2() {
        return address2;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setAddress2(java.lang.String)
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setAddress2(java.lang.String)
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getCity()
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getCity()
     */
    public String getCity() {
        return city;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setCity(java.lang.String)
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setCity(java.lang.String)
     */
    public void setCity(String city) {
        this.city = city;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getZip()
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getZip()
     */
    public String getZip() {
        return zip;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setZip(java.lang.String)
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setZip(java.lang.String)
     */
    public void setZip(String zip) {
        this.zip = zip;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getCountry()
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getCountry()
     */
    public String getCountry() {
        return country;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setCountry(java.lang.String)
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setCountry(java.lang.String)
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getPhone()
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getPhone()
     */
    public String getPhone() {
        return phone;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setPhone(java.lang.String)
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setPhone(java.lang.String)
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getLongitude()
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getLongitude()
     */
    public Double getLongitude() {
        return longitude;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setLongitude(java.lang.Float)
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setLongitude(java.lang.Float)
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getLatitude()
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getLatitude()
     */
    public Double getLatitude() {
        return latitude;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setLatitude(java.lang.Float)
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setLatitude(java.lang.Float)
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setState(java.lang.String)
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#setState(java.lang.String)
     */
    public void setState(String state) {
        this.state = state;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getState()
     */
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.store.domain.Store#getState()
     */
    public String getState() {
        return state;
    }

    public static class Presentation {

        public static class Tab {
            public static class Name {
                public static final String Advanced = "StoreImpl_Advanced_Tab";

            }

            public static class Order {
                public static final int Advanced = 7000;
            }
        }

        public static class Group {
            public static class Name {
                public static final String General = "StoreImpl_Store_General";
                public static final String Location = "StoreImpl_Store_Location";
                public static final String Geocoding = "StoreImpl_Store_Geocoding";
            }

            public static class Order {
                public static final int General = 1000;
                public static final int Location = 2000;
                public static final int Geocoding = 3000;
            }
        }

        public static class FieldOrder {
            public static final int NAME = 1000;
            public static final int ADDRESS_1 = 2000;
            public static final int ADDRESS_2 = 3000;
            public static final int CITY = 4000;
            public static final int STATE = 5000;
            public static final int ZIP = 6000;
            public static final int COUNTRY = 7000;
            public static final int PHONE = 8000;
            public static final int LATITUDE = 9000;
            public static final int LONGITUDE = 10000;
        }
    }

}
