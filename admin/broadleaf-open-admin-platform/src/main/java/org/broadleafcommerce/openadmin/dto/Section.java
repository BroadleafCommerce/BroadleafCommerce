/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
