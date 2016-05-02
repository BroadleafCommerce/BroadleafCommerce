/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License” located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License” located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.common.i18n.service.ISOService;
import org.springframework.context.ApplicationContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around ISOCountry.
 *
 * @author Elbert Bautista (elbertbautista)
 */
@XmlRootElement(name = "isoCountry")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ISOCountryWrapper extends BaseWrapper implements APIWrapper<ISOCountry>, APIUnwrapper<ISOCountry> {

    @XmlElement
    protected String name;

    @XmlElement
    protected String alpha2;

    @Override
    public void wrapDetails(ISOCountry model, HttpServletRequest request) {
        this.name = model.getName();
        this.alpha2 = model.getAlpha2();
    }

    @Override
    public void wrapSummary(ISOCountry model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    @Override
    public ISOCountry unwrap(HttpServletRequest request, ApplicationContext appContext) {
        ISOService isoService = (ISOService) appContext.getBean("blISOService");
        if (this.alpha2 != null) {
            return isoService.findISOCountryByAlpha2Code(this.alpha2);
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
     * @return the alpha2
     */
    public String getAlpha2() {
        return alpha2;
    }

    
    /**
     * @param alpha2 the alpha2 to set
     */
    public void setAlpha2(String alpha2) {
        this.alpha2 = alpha2;
    }
}
