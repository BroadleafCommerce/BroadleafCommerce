/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
