/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.expression;

import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.CountrySubdivision;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.CountrySubdivisionService;
import org.broadleafcommerce.profile.core.service.StateService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Component("blBasicAddressVariableExpression")
@ConditionalOnTemplating
public class BasicAddressVariableExpression implements BroadleafVariableExpression {

    @Resource(name = "blStateService")
    protected StateService stateService;

    @Resource(name = "blCountrySubdivisionService")
    protected CountrySubdivisionService countrySubdivisionService;

    @Resource(name = "blCountryService")
    protected CountryService countryService;

    @Override
    public String getName() {
        return "address";
    }

    @Deprecated
    public List<State> getStateOptions() {
        return stateService.findStates();
    }

    public List<CountrySubdivision> getCountrySubOptionsByISOCountry(ISOCountry isoCountry) {
        if (isoCountry == null) {
            return new ArrayList<>();
        }

        return getCountrySubOptionsByCountryAbbrev(isoCountry.getAlpha2());
    }

    public List<CountrySubdivision> getCountrySubOptionsByCountryAbbrev(String countryAbbreviation) {
        return countrySubdivisionService.findSubdivisions(countryAbbreviation);
    }

    public List<Country> getCountryOptions() {
        return countryService.findCountries();
    }

}
