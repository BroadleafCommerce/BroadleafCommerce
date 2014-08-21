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

import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import java.io.Serializable;

public interface Address extends Serializable {

    public void setId(Long id);

    public Long getId();

    public void setAddressLine1(String addressLine1);

    public String getAddressLine1();

    public void setAddressLine2(String addressLine2);

    public String getAddressLine2();

    public void setAddressLine3(String addressLine3);

    public String getAddressLine3();

    public void setCity(String city);

    public String getCity();

    /**
     * @deprecated Should use {@link #setIsoCountrySubdivision()} or {@link #setStateProvinceRegion()} instead
     * The Broadleaf Country and State domains will no longer be used for addresses to better support i18n.
     * BLC_STATE and BLC_COUNTRY should primarily be used for look-ups or filtering to those country/states you wish to ship.
     */
    @Deprecated
    public void setState(State state);

    /**
     * @deprecated Should use {@link #getIsoCountrySubdivision()} or {@link #getStateProvinceRegion()} instead
     * The Broadleaf Country and State domains will no longer be used for addresses to better support i18n.
     * BLC_STATE and BLC_COUNTRY should primarily be used for look-ups or filtering to those country/states you wish to ship.
     */
    @Deprecated
    public State getState();

    /**
     * gets the ISO 3166-2 code for the country subdivision (state/region/province) where this address resides
     * @return - the code
     */
    public String getIsoCountrySubdivision();

    /**
     * sets the ISO 3166-2 code for the country subdivision (state/region/province) where this address resides
     * @param isoCountrySubdivision - ISO 3166-2 country subdivision code
     */
    public void setIsoCountrySubdivision(String isoCountrySubdivision);

    /**
     * @return - a friendly name indicating a countries subdivision, i.e. State, Province, Region etc...
     */
    public String getStateProvinceRegion();

    /**
     * sets the friendly name indicating a countries subdivision, i.e. State, Province, Region etc...
     * @param stateProvinceRegion - friendly name
     */
    public void setStateProvinceRegion(String stateProvinceRegion);

    public void setPostalCode(String postalCode);

    public String getPostalCode();

    public String getCounty();

    public void setCounty(String county);

    public String getZipFour();

    public void setZipFour(String zipFour);

    /**
     * @deprecated Should use {@link #setIsoCountryAlpha2(ISOCountry)} instead
     * The Broadleaf Country and State domains will no longer be used for addresses to better support i18n.
     * BLC_STATE and BLC_COUNTRY should primarily be used for look-ups or filtering to those country/states you wish to ship.
     */
    @Deprecated
    public void setCountry(Country country);

    /**
     * @deprecated Should use {@link #getIsoCountryAlpha2()} instead
     * The Broadleaf Country and State domains will no longer be used for addresses to better support i18n.
     * BLC_STATE and BLC_COUNTRY should primarily be used for look-ups or filtering to those country/states you wish to ship.
     */
    @Deprecated
    public Country getCountry();

    /**
     * gets the ISO 3166-1 alpha-2 code for the country where this address resides
     * @return - the ISOCountry representation of the code
     */
    public ISOCountry getIsoCountryAlpha2();

    /**
     * sets the ISO 3166-1 alpha-2 code for the country where this address resides
     * @param isoCountryAlpha2 - ISO 3166-1 alpha-2 code
     */
    public void setIsoCountryAlpha2(ISOCountry isoCountryAlpha2);

    public String getTokenizedAddress();

    public void setTokenizedAddress(String tAddress);

    public Boolean getStandardized();

    public void setStandardized(Boolean standardized);

    public String getCompanyName();

    public void setCompanyName(String companyName);

    public boolean isDefault();

    public void setDefault(boolean isDefault);

    public String getFirstName();

    public void setFirstName(String firstName);

    public String getLastName();

    public void setLastName(String lastName);

    public String getFullName();

    public void setFullName(String fullName);

    /**
     * @deprecated Should use {@link #getPhonePrimary()} instead
     * @see {@link Phone}
     */
    @Deprecated
    public String getPrimaryPhone();

    /**
     * @deprecated Should use {@link #setPhonePrimary(Phone)} instead
     * @see {@link Phone}
     */
    @Deprecated
    public void setPrimaryPhone(String primaryPhone);

    /**
     * @deprecated Should use {@link #getPhoneSecondary()} instead
     * @see {@link Phone}
     */
    @Deprecated
    public String getSecondaryPhone();

    /**
     * @deprecated Should use {@link #setPhoneSecondary(Phone)} instead
     * @see {@link Phone}
     */
    @Deprecated
    public void setSecondaryPhone(String secondaryPhone);

    /**
     * @deprecated Should use {@link #getPhoneFax()} instead
     * @see {@link Phone}
     */
    @Deprecated
    public String getFax();

    /**
     * @deprecated Should use {@link #setPhoneFax(Phone)} instead
     * @see {@link Phone}
     */
    @Deprecated
    public void setFax(String fax);

    public Phone getPhonePrimary();

    public void setPhonePrimary(Phone phonePrimary);

    public Phone getPhoneSecondary();

    public void setPhoneSecondary(Phone phoneSecondary);
    
    public Phone getPhoneFax();

    public void setPhoneFax(Phone phone);

    public String getEmailAddress();

    public void setEmailAddress(String emailAddress);

    public boolean isBusiness();

    public void setBusiness(boolean isBusiness);

    public boolean isStreet();

    public void setStreet(boolean isStreet);

    public boolean isMailing();

    public void setMailing(boolean isMailing);

    public String getVerificationLevel();

    public void setVerificationLevel(String verificationLevel);

    public boolean isActive();

    public void setActive(boolean isActive);
}
