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
package org.broadleafcommerce.profile.core.service;

import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.service.exception.AddressVerificationException;

import java.util.List;

public interface AddressService {

    public Address saveAddress(Address address);

    public Address readAddressById(Long addressId);

    public Address create();

    public void delete(Address address);

    /**
     * Verifies the address and returns a collection of addresses. If the address was 
     * invalid but close to a match, this method should return a list of one or more addresses that may be valid. 
     * If the address is valid, implementations should return the valid address in the list. 
     * Implementations may set the tokenized address, zip four, and verification level. If the address could not 
     * be validated, implementors should throw an <code>AddressValidationException</code>.
     * 
     * For example, an address may be close, but missing zip four. This service should return 
     * the address in question with zip four populated.
     * @param address
     * @return
     */
    public List<Address> verifyAddress(Address address) throws AddressVerificationException;

    /**
     * Returns a new address instance with the given properties on the originating address
     * minus any default or active values set
     * @param address - the address to copy
     * @return
     */
    public Address copyAddress(Address orig);

    /**
     * Copies properties from the originating address to the destination address
     * minus any default or active values
     * @param address - the address to copy
     * @return
     */
    public Address copyAddress(Address dest, Address orig);

    /**
     * Convenience method that attempts to pre-populate the ISO Country Subdivision on an address.
     * Strategy attempts to identify the ISO subdivision based on the contents of the "friendly" state/province/region
     * attribute and the ISO alpha-2 country code already populated on the passed in address.
     * It will attempt to look for the "alternate abbreviation" first, and if not found will check the "name" next.
     * @param address
     */
    public void populateAddressISOCountrySub(Address address);

}
