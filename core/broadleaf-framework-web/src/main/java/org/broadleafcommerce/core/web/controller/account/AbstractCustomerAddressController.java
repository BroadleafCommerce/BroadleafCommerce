/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.controller.account;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.i18n.service.ISOService;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.web.controller.account.validator.CustomerAddressValidator;
import org.broadleafcommerce.core.web.service.InitBinderService;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.CountrySubdivision;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.service.AddressService;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.CountrySubdivisionService;
import org.broadleafcommerce.profile.core.service.CustomerAddressService;
import org.broadleafcommerce.profile.core.service.StateService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.web.bind.ServletRequestDataBinder;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * An abstract controller that provides convenience methods and resource declarations for its children.
 *
 * Operations that are shared between controllers that deal with Customer Addresses belong here.
 *
 * @author Elbert Bautista (elbertbautista)
 */
public class AbstractCustomerAddressController extends BroadleafAbstractController {

    private static final Log LOG = LogFactory.getLog(AbstractCustomerAddressController.class);

    protected static String customerAddressesView = "account/manageCustomerAddresses";
    protected static String customerAddressesRedirect = "redirect:/account/addresses";

    @Resource(name = "blCustomerAddressService")
    protected CustomerAddressService customerAddressService;

    @Resource(name = "blAddressService")
    protected AddressService addressService;

    @Resource(name = "blCountryService")
    protected CountryService countryService;

    @Resource(name = "blCustomerAddressValidator")
    protected CustomerAddressValidator customerAddressValidator;

    @Resource(name = "blStateService")
    protected StateService stateService;

    @Resource(name = "blCountrySubdivisionService")
    protected CountrySubdivisionService countrySubdivisionService;

    @Resource(name = "blISOService")
    protected ISOService isoService;

    @Resource(name = "blInitBinderService")
    protected InitBinderService initBinderService;

    /**
     * Initializes some custom binding operations for the managing an address.
     * More specifically, this method will attempt to bind state and country
     * abbreviations to actual State and Country objects when the String
     * representation of the abbreviation is submitted.
     *
     * @param request
     * @param binder
     * @throws Exception
     */
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        initBinderService.configAddressInitBinder(binder);
    }

    protected List<CountrySubdivision> populateStates() {
        return countrySubdivisionService.findSubdivisions();
    }

    protected List<Country> populateCountries() {
        return countryService.findCountries();
    }

    protected List<CustomerAddress> populateCustomerAddresses() {
        return customerAddressService.readActiveCustomerAddressesByCustomerId(CustomerState.getCustomer().getId());
    }

    public String getCustomerAddressesView() {
        return customerAddressesView;
    }

    public String getCustomerAddressesRedirect() {
        return customerAddressesRedirect;
    }

}
