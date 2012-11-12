
package org.broadleafcommerce.openadmin.client.dto;

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
