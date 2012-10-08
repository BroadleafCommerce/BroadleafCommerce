
package org.broadleafcommerce.openadmin.client;

import java.util.ArrayList;
import java.util.List;

public class ModuleSectionPojo {

    String sectionTitle;

    String sectionViewKey;

    String sectionViewClass;

    String sectionPresenterKey;

    String sectionPresenterClass;

    List<String> sectionPermissions;

    public ModuleSectionPojo(String sectionTitle, String sectionViewKey, String sectionViewClass, String sectionPresenterKey, String sectionPresenterClass, List<String> sectionPermissions) {
        this.sectionTitle = sectionTitle;
        this.sectionViewKey = sectionViewKey;
        this.sectionViewClass = sectionViewClass;
        this.sectionPresenterKey = sectionPresenterKey;
        this.sectionPresenterClass = sectionPresenterClass;
        this.sectionPermissions=new ArrayList<String>(sectionPermissions);
    }

    protected String getSectionTitle() {
        return sectionTitle;
    }

    protected void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    protected String getSectionViewKey() {
        return sectionViewKey;
    }

    protected void setSectionViewKey(String sectionViewKey) {
        this.sectionViewKey = sectionViewKey;
    }

    protected String getSectionViewClass() {
        return sectionViewClass;
    }

    protected void setSectionViewClass(String sectionViewClass) {
        this.sectionViewClass = sectionViewClass;
    }

    protected String getSectionPresenterKey() {
        return sectionPresenterKey;
    }

    protected void setSectionPresenterKey(String sectionPresenterKey) {
        this.sectionPresenterKey = sectionPresenterKey;
    }

    protected String getSectionPresenterClass() {
        return sectionPresenterClass;
    }

    protected void setSectionPresenterClass(String sectionPresenterClass) {
        this.sectionPresenterClass = sectionPresenterClass;
    }

    protected List<String> getSectionPermissions() {
        return sectionPermissions;
    }

    protected void setSectionPermissions(List<String> sectionPermissions) {
        this.sectionPermissions = sectionPermissions;
    }

}
