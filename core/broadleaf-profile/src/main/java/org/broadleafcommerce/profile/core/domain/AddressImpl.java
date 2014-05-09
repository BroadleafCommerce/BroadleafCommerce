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

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
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

@Entity
@EntityListeners(value = { TemporalTimestampListener.class })
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ADDRESS")
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "phonePrimary", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED,
                                    booleanOverrideValue = true)),
        @AdminPresentationMergeOverride(name = "phoneSecondary", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED,
                                    booleanOverrideValue = true)),
        @AdminPresentationMergeOverride(name = "phoneFax", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED,
                                    booleanOverrideValue = true)),
        @AdminPresentationMergeOverride(name = "phonePrimary.phoneNumber", mergeEntries = {
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED,
                                    booleanOverrideValue = false),
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.ORDER,
                                    intOverrideValue = 1300),
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.REQUIREDOVERRIDE,
                                    overrideValue = "NOT_REQUIRED"),
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.FRIENDLYNAME,
                                                overrideValue = "PhoneImpl_Primary_Phone")}),
        @AdminPresentationMergeOverride(name = "phoneSecondary.phoneNumber", mergeEntries = {
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED,
                                    booleanOverrideValue = false),
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.ORDER,
                                    intOverrideValue = 1400),
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.REQUIREDOVERRIDE,
                                    overrideValue = "NOT_REQUIRED"),
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.FRIENDLYNAME,
                                                overrideValue = "PhoneImpl_Secondary_Phone")}),
        @AdminPresentationMergeOverride(name = "phoneFax.phoneNumber", mergeEntries = {
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED,
                                    booleanOverrideValue = false),
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.ORDER,
                                    intOverrideValue = 1500),
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.REQUIREDOVERRIDE,
                                    overrideValue = "NOT_REQUIRED"),
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.FRIENDLYNAME,
                                                overrideValue = "PhoneImpl_Fax_Phone")})
    }
)
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "AddressImpl_baseAddress")
public class AddressImpl implements Address {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "AddressId")
    @GenericGenerator(
        name="AddressId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="AddressImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.profile.core.domain.AddressImpl")
        }
    )
    @Column(name = "ADDRESS_ID")
    protected Long id;

    @Column(name = "FIRST_NAME")
    @AdminPresentation(friendlyName = "AddressImpl_First_Name", order=10, group = "AddressImpl_Address")
    protected String firstName;

    @Column(name = "LAST_NAME")
    @AdminPresentation(friendlyName = "AddressImpl_Last_Name", order=20, group = "AddressImpl_Address")
    protected String lastName;

    @Column(name = "EMAIL_ADDRESS")
    @AdminPresentation(friendlyName = "AddressImpl_Email_Address", order=30, group = "AddressImpl_Address")
    protected String emailAddress;

    @Column(name = "COMPANY_NAME")
    @AdminPresentation(friendlyName = "AddressImpl_Company_Name", order=40, group = "AddressImpl_Address")
    protected String companyName;

    @Column(name = "ADDRESS_LINE1", nullable = false)
    @AdminPresentation(friendlyName = "AddressImpl_Address_1", order=50, group = "AddressImpl_Address")
    protected String addressLine1;

    @Column(name = "ADDRESS_LINE2")
    @AdminPresentation(friendlyName = "AddressImpl_Address_2", order=60, group = "AddressImpl_Address")
    protected String addressLine2;

    @Column(name = "ADDRESS_LINE3")
    @AdminPresentation(friendlyName = "AddressImpl_Address_3", order = 60, group = "AddressImpl_Address")
    protected String addressLine3;

    @Column(name = "CITY", nullable = false)
    @AdminPresentation(friendlyName = "AddressImpl_City", order=70, group = "AddressImpl_Address")
    protected String city;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = StateImpl.class)
    @JoinColumn(name = "STATE_PROV_REGION")
    @Index(name="ADDRESS_STATE_INDEX", columnNames={"STATE_PROV_REGION"})
    @AdminPresentation(friendlyName = "AddressImpl_State", order=80, group = "AddressImpl_Address")
    @AdminPresentationToOneLookup()
    protected State state;

    @Column(name = "COUNTY")
    @AdminPresentation(friendlyName = "AddressImpl_County", order=90, group = "AddressImpl_Address")
    protected String county;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = CountryImpl.class, optional = false)
    @JoinColumn(name = "COUNTRY")
    @Index(name="ADDRESS_COUNTRY_INDEX", columnNames={"COUNTRY"})
    @AdminPresentation(friendlyName = "AddressImpl_Country", order=100, group = "AddressImpl_Address")
    @AdminPresentationToOneLookup()
    protected Country country;

    @Column(name = "POSTAL_CODE", nullable = false)
    @AdminPresentation(friendlyName = "AddressImpl_Postal_Code", order=110, group = "AddressImpl_Address")
    protected String postalCode;

    @Column(name = "ZIP_FOUR")
    @AdminPresentation(friendlyName = "AddressImpl_Four_Digit_Zip", order=120, group = "AddressImpl_Address")
    protected String zipFour;

    @ManyToOne(targetEntity = PhoneImpl.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "PHONE_PRIMARY_ID")
    @Index(name="ADDRESS_PHONE_PRI_IDX", columnNames={"PHONE_PRIMARY_ID"})
    protected Phone phonePrimary;

    @ManyToOne(targetEntity = PhoneImpl.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "PHONE_SECONDARY_ID")
    @Index(name="ADDRESS_PHONE_SEC_IDX", columnNames={"PHONE_SECONDARY_ID"})
    protected Phone phoneSecondary;

    @ManyToOne(targetEntity = PhoneImpl.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "PHONE_FAX_ID")
    @Index(name="ADDRESS_PHONE_FAX_IDX", columnNames={"PHONE_FAX_ID"})
    protected Phone phoneFax;

    @Column(name = "IS_DEFAULT")
    @AdminPresentation(friendlyName = "AddressImpl_Default_Address", order=160, group = "AddressImpl_Address")
    protected boolean isDefault = false;

    @Column(name = "IS_ACTIVE")
    @AdminPresentation(friendlyName = "AddressImpl_Active_Address", order=170, group = "AddressImpl_Address")
    protected boolean isActive = true;

    @Column(name = "IS_BUSINESS")
    @AdminPresentation(friendlyName = "AddressImpl_Business_Address", order=180, group = "AddressImpl_Address")
    protected boolean isBusiness = false;

    /**
     * This is intented to be used for address verification integrations and should not be modifiable in the admin
     */
    @Column(name = "TOKENIZED_ADDRESS")
    @AdminPresentation(friendlyName = "AddressImpl_Tokenized_Address", order=190, group = "AddressImpl_Address", visibility=VisibilityEnum.HIDDEN_ALL)
    protected String tokenizedAddress;
    
    /**
     * This is intented to be used for address verification integrations and should not be modifiable in the admin
     */
    @Column(name = "STANDARDIZED")
    @AdminPresentation(friendlyName = "AddressImpl_Standardized", order=200, group = "AddressImpl_Address", visibility=VisibilityEnum.HIDDEN_ALL)
    protected Boolean standardized = Boolean.FALSE;

    /**
     * This is intented to be used for address verification integrations and should not be modifiable in the admin
     */
    @Column(name = "VERIFICATION_LEVEL")
    @AdminPresentation(friendlyName = "AddressImpl_Verification_Level", order=210, group = "AddressImpl_Address", visibility=VisibilityEnum.HIDDEN_ALL)
    protected String verificationLevel;

    @Column(name = "PRIMARY_PHONE")
    @Deprecated
    protected String primaryPhone;

    @Column(name = "SECONDARY_PHONE")
    @Deprecated
    protected String secondaryPhone;

    @Column(name = "FAX")
    @Deprecated
    protected String fax;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getAddressLine1() {
        return addressLine1;
    }

    @Override
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    @Override
    public String getAddressLine2() {
        return addressLine2;
    }

    @Override
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    @Override
    public String getAddressLine3() {
        return addressLine3;
    }

    @Override
    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public Country getCountry() {
        return country;
    }

    @Override
    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public String getPostalCode() {
        return postalCode;
    }

    @Override
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String getCounty() {
        return county;
    }

    @Override
    public void setCounty(String county) {
        this.county = county;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String getTokenizedAddress() {
        return tokenizedAddress;
    }

    @Override
    public void setTokenizedAddress(String tokenizedAddress) {
        this.tokenizedAddress = tokenizedAddress;
    }

    @Override
    public Boolean getStandardized() {
        return standardized;
    }

    @Override
    public void setStandardized(Boolean standardized) {
        this.standardized = standardized;
    }

    @Override
    public String getZipFour() {
        return zipFour;
    }

    @Override
    public void setZipFour(String zipFour) {
        this.zipFour = zipFour;
    }

    @Override
    public String getCompanyName() {
        return companyName;
    }

    @Override
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setActive(boolean isActive) {
        this.isActive = isActive;
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
    @Deprecated
    public String getPrimaryPhone() {
        return primaryPhone;
    }

    @Override
    @Deprecated
    public void setPrimaryPhone(String primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    @Override
    @Deprecated
    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    @Override
    @Deprecated
    public void setSecondaryPhone(String secondaryPhone) {
        this.secondaryPhone = secondaryPhone;
    }

    @Override
    @Deprecated
    public String getFax() {
        return this.fax;
    }

    @Override
    @Deprecated
    public void setFax(String fax) {
        this.fax = fax;
    }

    @Override
    public Phone getPhonePrimary() {
        Phone legacyPhone = new PhoneImpl();
        legacyPhone.setPhoneNumber(this.primaryPhone);
        return (phonePrimary == null && this.primaryPhone !=null)? legacyPhone : phonePrimary;
    }

    @Override
    public void setPhonePrimary(Phone phonePrimary) {
        this.phonePrimary = phonePrimary;
    }

    @Override
    public Phone getPhoneSecondary() {
        Phone legacyPhone = new PhoneImpl();
        legacyPhone.setPhoneNumber(this.secondaryPhone);
        return (phoneSecondary == null && this.secondaryPhone !=null)? legacyPhone : phoneSecondary;
    }

    @Override
    public void setPhoneSecondary(Phone phoneSecondary) {
        this.phoneSecondary = phoneSecondary;
    }

    @Override
    public Phone getPhoneFax() {
        Phone legacyPhone = new PhoneImpl();
        legacyPhone.setPhoneNumber(this.fax);
        return (phoneFax == null && this.fax != null)? legacyPhone : phoneFax;
    }

    @Override
    public void setPhoneFax(Phone phoneFax) {
        this.phoneFax = phoneFax;
    }

    @Override
    public boolean isBusiness() {
        return isBusiness;
    }

    @Override
    public void setBusiness(boolean isBusiness) {
        this.isBusiness = isBusiness;
    }

    @Override
    public String getVerificationLevel() {
        return verificationLevel;
    }

    @Override
    public void setVerificationLevel(String verificationLevel) {
        this.verificationLevel = verificationLevel;
    }

    @Override
    public String getEmailAddress() {
        return this.emailAddress;
    }

    @Override
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        AddressImpl other = (AddressImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (addressLine1 == null) {
            if (other.addressLine1 != null)
                return false;
        } else if (!addressLine1.equals(other.addressLine1))
            return false;
        if (addressLine2 == null) {
            if (other.addressLine2 != null)
                return false;
        } else if (!addressLine2.equals(other.addressLine2))
            return false;
        if (city == null) {
            if (other.city != null)
                return false;
        } else if (!city.equals(other.city))
            return false;
        if (companyName == null) {
            if (other.companyName != null)
                return false;
        } else if (!companyName.equals(other.companyName))
            return false;
        if (country == null) {
            if (other.country != null)
                return false;
        } else if (!country.equals(other.country))
            return false;
        if (county == null) {
            if (other.county != null)
                return false;
        } else if (!county.equals(other.county))
            return false;
        if (firstName == null) {
            if (other.firstName != null)
                return false;
        } else if (!firstName.equals(other.firstName))
            return false;
        if (lastName == null) {
            if (other.lastName != null)
                return false;
        } else if (!lastName.equals(other.lastName))
            return false;
        if (postalCode == null) {
            if (other.postalCode != null)
                return false;
        } else if (!postalCode.equals(other.postalCode))
            return false;
        if (state == null) {
            if (other.state != null)
                return false;
        } else if (!state.equals(other.state))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((addressLine1 == null) ? 0 : addressLine1.hashCode());
        result = prime * result + ((addressLine2 == null) ? 0 : addressLine2.hashCode());
        result = prime * result + ((city == null) ? 0 : city.hashCode());
        result = prime * result + ((companyName == null) ? 0 : companyName.hashCode());
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result + ((county == null) ? 0 : county.hashCode());
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((postalCode == null) ? 0 : postalCode.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        return result;
    }
}
