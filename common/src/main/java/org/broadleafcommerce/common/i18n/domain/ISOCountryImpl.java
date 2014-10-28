/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.i18n.domain;

import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.i18n.service.type.ISOCodeStatusType;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ISO_COUNTRY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(friendlyName = "ISOCountryImpl_baseCountry")
public class ISOCountryImpl implements ISOCountry, AdminMainEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ALPHA_2")
    @AdminPresentation(friendlyName = "ISOCountryImpl_Alpha2", order=1, group = "ISOCountryImpl_Details", prominent = true)
    protected String alpha2;

    @Column(name = "NAME")
    @AdminPresentation(friendlyName = "ISOCountryImpl_Name", order=2, group = "ISOCountryImpl_Details", prominent = true)
    protected String name;

    @Column(name = "ALPHA_3")
    protected String alpha3;

    @Column(name = "NUMERIC_CODE")
    protected Integer numericCode;

    @Column(name = "STATUS")
    protected String status;

    @Override
    public String getAlpha2() {
        return alpha2;
    }

    @Override
    public void setAlpha2(String alpha2) {
        this.alpha2 = alpha2;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAlpha3() {
        return alpha3;
    }

    @Override
    public void setAlpha3(String alpha3) {
        this.alpha3 = alpha3;
    }

    @Override
    public Integer getNumericCode() {
        return numericCode;
    }

    @Override
    public void setNumericCode(Integer numericCode) {
        this.numericCode = numericCode;
    }

    @Override
    public ISOCodeStatusType getStatus() {
        return ISOCodeStatusType.getInstance(status);
    }

    @Override
    public void setStatus(ISOCodeStatusType status) {
        this.status = status == null ? null : status.getType();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        ISOCountryImpl other = (ISOCountryImpl) obj;
        if (alpha2 == null) {
            if (other.alpha2 != null)
                return false;
        } else if (!alpha2.equals(other.alpha2))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (alpha3 == null) {
            if (other.alpha3 != null)
                return false;
        } else if (!alpha3.equals(other.alpha3))
            return false;
        if (numericCode == null) {
            if (other.numericCode != null)
                return false;
        } else if (!numericCode.equals(other.numericCode))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alpha2 == null) ? 0 : alpha2.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((alpha3 == null) ? 0 : alpha3.hashCode());
        result = prime * result + ((numericCode == null) ? 0 : numericCode.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }

    @Override
    public String getMainEntityName() {
        return getName();
    }

    @Override
    public <G extends ISOCountry> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        ISOCountry cloned = createResponse.getClone();
        cloned.setAlpha2(alpha2);
        cloned.setAlpha3(alpha3);
        cloned.setName(name);
        cloned.setNumericCode(numericCode);
        cloned.setStatus(getStatus());
        return createResponse;
    }
}
