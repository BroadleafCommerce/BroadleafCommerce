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

import java.util.ArrayList;
import java.util.List;

/**
 * DTO object to represent the components of a Section (a Module has many of these)
 * 
 */
public class Section {

    protected String sectionTitle;
    protected String sectionViewKey;
    protected String sectionViewClass;
    protected String sectionPresenterKey;
    protected String sectionPresenterClass;
    protected List<String> sectionPermissions;

    public Section(String sectionTitle, String sectionViewKey, String sectionViewClass, String sectionPresenterKey, String sectionPresenterClass, List<String> sectionPermissions) {
        this.sectionTitle = sectionTitle;
        this.sectionViewKey = sectionViewKey;
        this.sectionViewClass = sectionViewClass;
        this.sectionPresenterKey = sectionPresenterKey;
        this.sectionPresenterClass = sectionPresenterClass;
        this.sectionPermissions = new ArrayList<String>(sectionPermissions);
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getSectionViewKey() {
        return sectionViewKey;
    }

    public void setSectionViewKey(String sectionViewKey) {
        this.sectionViewKey = sectionViewKey;
    }

    public String getSectionViewClass() {
        return sectionViewClass;
    }

    public void setSectionViewClass(String sectionViewClass) {
        this.sectionViewClass = sectionViewClass;
    }

    public String getSectionPresenterKey() {
        return sectionPresenterKey;
    }

    public void setSectionPresenterKey(String sectionPresenterKey) {
        this.sectionPresenterKey = sectionPresenterKey;
    }

    public String getSectionPresenterClass() {
        return sectionPresenterClass;
    }

    public void setSectionPresenterClass(String sectionPresenterClass) {
        this.sectionPresenterClass = sectionPresenterClass;
    }

    public List<String> getSectionPermissions() {
        return sectionPermissions;
    }

    public void setSectionPermissions(List<String> sectionPermissions) {
        this.sectionPermissions = sectionPermissions;
    }

}
