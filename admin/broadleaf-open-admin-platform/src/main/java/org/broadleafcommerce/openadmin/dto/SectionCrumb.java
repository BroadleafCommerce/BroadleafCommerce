/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.dto;

import java.io.Serializable;

/**
 * @author Jeff Fischer
 */
public class SectionCrumb implements Serializable {

    protected String sectionIdentifier;
    protected String sectionId;
    protected String originalSectionIdentifier;

    public String getSectionIdentifier() {
        return sectionIdentifier;
    }

    public void setSectionIdentifier(String sectionIdentifier) {
        this.sectionIdentifier = sectionIdentifier;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getOriginalSectionIdentifier() {
        return originalSectionIdentifier;
    }

    public void setOriginalSectionIdentifier(String originalSectionIdentifier) {
        this.originalSectionIdentifier = originalSectionIdentifier;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SectionCrumb{");
        sb.append("sectionIdentifier='").append(sectionIdentifier).append('\'');
        sb.append(", sectionId='").append(sectionId).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass())) return false;

        SectionCrumb that = (SectionCrumb) o;

        if (sectionId != null ? !sectionId.equals(that.sectionId) : that.sectionId != null) return false;
        if (sectionIdentifier != null ? !sectionIdentifier.equals(that.sectionIdentifier) : that.sectionIdentifier !=
                null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sectionIdentifier != null ? sectionIdentifier.hashCode() : 0;
        result = 31 * result + (sectionId != null ? sectionId.hashCode() : 0);
        return result;
    }
}
