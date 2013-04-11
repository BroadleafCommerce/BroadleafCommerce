/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
