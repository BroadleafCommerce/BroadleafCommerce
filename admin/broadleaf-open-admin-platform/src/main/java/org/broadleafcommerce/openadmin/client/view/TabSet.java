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

package org.broadleafcommerce.openadmin.client.view;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @author jfischer
 *
 */
public class TabSet extends com.smartgwt.client.widgets.tab.TabSet {

    /**
     * 
     */
    public TabSet() {
        super();
    }

    /**
     * @param jsObj
     */
    public TabSet(JavaScriptObject jsObj) {
        super(jsObj);
    }

    public void setPaneMargin(int margin) {
        setAttribute("paneMargin", String.valueOf(margin), false);
    }
    
}
