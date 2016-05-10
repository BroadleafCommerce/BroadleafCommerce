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
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around Country.
 *
 * User: Elbert Bautista
 * Date: 4/10/12
 */
@XmlRootElement(name = "country")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class CountryWrapper extends BaseWrapper implements APIWrapper<Country>, APIUnwrapper<Country> {

    @XmlElement
    protected String name;

    @XmlElement
    protected String abbreviation;

    @Override
    public void wrapDetails(Country model, HttpServletRequest request) {
        this.name = model.getName();
        this.abbreviation = model.getAbbreviation();
    }

    @Override
    public void wrapSummary(Country model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    @Override
    public Country unwrap(HttpServletRequest request, ApplicationContext appContext) {
        CountryService countryService = (CountryService) appContext.getBean("blCountryService");
        if (this.abbreviation != null) {
            Country country = countryService.findCountryByAbbreviation(this.abbreviation);
            return country;
        }

        return null;
    }

    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    
    /**
     * @return the abbreviation
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    
    /**
     * @param abbreviation the abbreviation to set
     */
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }
}
