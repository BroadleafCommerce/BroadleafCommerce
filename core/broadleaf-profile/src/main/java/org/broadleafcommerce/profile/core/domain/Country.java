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

/**
 * This entity should be used only for lookup and filtering purposes only.
 * For example, to help populate a drop-down to those Countries you only wish to ship to.
 *
 * {@link org.broadleafcommerce.profile.core.domain.Address} no longer references this and Address
 * implementations should be updated to use {@link org.broadleafcommerce.common.i18n.domain.ISOCountry} instead.
 * This is to accommodate International Billing/Shipping Addresses which may not necessarily be restricted
 * to countries represented by this entity.
 *
 * {@link http://www.iso.org/iso/country_codes.htm}
 * {@link http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2}
 *
 * @author Elbert Bautista (elbertbautista)
 */
public interface Country extends Serializable {

    /**
     * The primary key - Ideally, implementations should use the ISO 3166-1 alpha-2 code of the country.
     * e.g. "US" or "GB"
     * {@link http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2}
     */
    public String getAbbreviation();

    /**
     * sets the abbreviation for this Country
     * @param abbreviation - e.g. "US" or "GB"
     */
    public void setAbbreviation(String abbreviation);

    /**
     * The name for the Country
     * e.g. "United States", "United Kingdom"
     * @return - the name of the Country
     */
    public String getName();

    /**
     * sets the name of the Country
     * @param name - e.g. "United States", "United Kingdom"
     */
    public void setName(String name);

}
