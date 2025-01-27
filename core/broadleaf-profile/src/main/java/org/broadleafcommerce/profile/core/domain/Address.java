/*-
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.i18n.domain.ISOCountry;

import java.io.Serializable;

public interface Address extends Serializable, MultiTenantCloneable<Address> {

    Long getId();

    void setId(Long id);

    String getAddressLine1();

    void setAddressLine1(String addressLine1);

    String getAddressLine2();

    void setAddressLine2(String addressLine2);

    String getAddressLine3();

    void setAddressLine3(String addressLine3);

    String getCity();

    void setCity(String city);

    /**
     * gets the ISO 3166-2 code for the country subdivision (state/region/province) where this address resides
     *
     * @return - the code
     */
    String getIsoCountrySubdivision();

    /**
     * sets the ISO 3166-2 code for the country subdivision (state/region/province) where this address resides
     *
     * @param isoCountrySubdivision - ISO 3166-2 country subdivision code
     */
    void setIsoCountrySubdivision(String isoCountrySubdivision);

    /**
     * @return - a friendly name indicating a countries subdivision, i.e. State, Province, Region etc...
     */
    String getStateProvinceRegion();

    /**
     * sets the friendly name indicating a countries subdivision, i.e. State, Province, Region etc...
     *
     * @param stateProvinceRegion - friendly name
     */
    void setStateProvinceRegion(String stateProvinceRegion);

    String getPostalCode();

    void setPostalCode(String postalCode);

    String getCounty();

    void setCounty(String county);

    String getZipFour();

    void setZipFour(String zipFour);

    /**
     * gets the ISO 3166-1 alpha-2 code for the country where this address resides
     *
     * @return - the ISOCountry representation of the code
     */
    ISOCountry getIsoCountryAlpha2();

    /**
     * sets the ISO 3166-1 alpha-2 code for the country where this address resides
     *
     * @param isoCountryAlpha2 - ISO 3166-1 alpha-2 code
     */
    void setIsoCountryAlpha2(ISOCountry isoCountryAlpha2);

    String getTokenizedAddress();

    void setTokenizedAddress(String tAddress);

    Boolean getStandardized();

    void setStandardized(Boolean standardized);

    String getCompanyName();

    void setCompanyName(String companyName);

    boolean isDefault();

    void setDefault(boolean isDefault);

    String getFirstName();

    void setFirstName(String firstName);

    String getLastName();

    void setLastName(String lastName);

    String getFullName();

    void setFullName(String fullName);

    /**
     * @see {@link Phone}
     * @deprecated Should use {@link #getPhonePrimary()} instead
     */
    @Deprecated
    String getPrimaryPhone();

    /**
     * @see {@link Phone}
     * @deprecated Should use {@link #setPhonePrimary(Phone)} instead
     */
    @Deprecated
    void setPrimaryPhone(String primaryPhone);

    /**
     * @see {@link Phone}
     * @deprecated Should use {@link #getPhoneSecondary()} instead
     */
    @Deprecated
    String getSecondaryPhone();

    /**
     * @see {@link Phone}
     * @deprecated Should use {@link #setPhoneSecondary(Phone)} instead
     */
    @Deprecated
    void setSecondaryPhone(String secondaryPhone);

    /**
     * @see {@link Phone}
     * @deprecated Should use {@link #getPhoneFax()} instead
     */
    @Deprecated
    String getFax();

    /**
     * @see {@link Phone}
     * @deprecated Should use {@link #setPhoneFax(Phone)} instead
     */
    @Deprecated
    void setFax(String fax);

    Phone getPhonePrimary();

    void setPhonePrimary(Phone phonePrimary);

    Phone getPhoneSecondary();

    void setPhoneSecondary(Phone phoneSecondary);

    Phone getPhoneFax();

    void setPhoneFax(Phone phone);

    String getEmailAddress();

    void setEmailAddress(String emailAddress);

    boolean isBusiness();

    void setBusiness(boolean isBusiness);

    boolean isStreet();

    void setStreet(boolean isStreet);

    boolean isMailing();

    void setMailing(boolean isMailing);

    String getVerificationLevel();

    void setVerificationLevel(String verificationLevel);

    boolean isActive();

    void setActive(boolean isActive);

}
