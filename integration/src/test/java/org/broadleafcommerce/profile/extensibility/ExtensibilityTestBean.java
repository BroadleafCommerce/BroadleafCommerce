/*
 * #%L
 * BroadleafCommerce Integration
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
package org.broadleafcommerce.profile.extensibility;

/**
 * 
 * @author jfischer
 *
 */
public class ExtensibilityTestBean {
    
    protected String testProperty = "none";
    protected String testProperty2 = "none2";
    
    /**
     * @return the testProperty
     */
    public String getTestProperty() {
        return testProperty;
    }
    
    /**
     * @param testProperty the testProperty to set
     */
    public void setTestProperty(String testProperty) {
        this.testProperty = testProperty;
    }
    
    /**
     * @return the testProperty2
     */
    public String getTestProperty2() {
        return testProperty2;
    }
    
    /**
     * @param testProperty2 the testProperty2 to set
     */
    public void setTestProperty2(String testProperty2) {
        this.testProperty2 = testProperty2;
    }

}
