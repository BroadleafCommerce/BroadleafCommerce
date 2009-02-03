package org.broadleafcommerce.profile.service;

import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.service.addressValidation.AddressStandarizationResponse;
import org.broadleafcommerce.profile.service.addressValidation.ServiceDownResponse;

public interface AddressStandardizationService extends ServiceDownResponse {
    public AddressStandarizationResponse standardizeAddress(Address addr);

    public void standardizeAndTokenizeAddress(Address address);

    public void tokenizeAddress(Address addr, boolean isStandardized);
}
