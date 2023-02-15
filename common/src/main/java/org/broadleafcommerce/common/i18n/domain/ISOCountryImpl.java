/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.common.i18n.domain;

import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.i18n.service.type.ISOCodeStatusType;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;


/**
 * @author Elbert Bautista (elbertbautista)
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ISO_COUNTRY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blCountryElements")
@AdminPresentationClass(friendlyName = "ISOCountryImpl_baseCountry")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.AUDITABLE_ONLY)
})
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

}
