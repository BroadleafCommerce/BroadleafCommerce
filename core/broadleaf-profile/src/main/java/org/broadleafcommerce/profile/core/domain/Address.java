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

    public void setState(State state);

    public State getState();

    public void setPostalCode(String postalCode);

    public String getPostalCode();

    public String getCounty();

    public void setCounty(String county);

    public String getZipFour();

    public void setZipFour(String zipFour);

    public void setCountry(Country country);

    public Country getCountry();

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

    public String getVerificationLevel();

    public void setVerificationLevel(String verificationLevel);

    public boolean isActive();

    public void setActive(boolean isActive);
}
