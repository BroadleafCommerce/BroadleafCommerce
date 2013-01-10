/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.admin.client.view.promotion;

import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

/**
 * 
 * @author jfischer
 *
 */
public class SectionView extends VLayout {
    
    protected VStack contentLayout;
    protected ToolStrip toolbar;
    
    public SectionView(String title) {
        setLayoutBottomMargin(10);
        toolbar = new ToolStrip();
        toolbar.setWidth100();
        Label label = new Label(title);
        label.setWrap(false);
        toolbar.addSpacer(6);
        toolbar.addMember(label);
        addMember(toolbar);
        contentLayout = new VStack(10);
        addMember(contentLayout);
    }

    public VStack getContentLayout() {
        return contentLayout;
    }

    public ToolStrip getToolbar() {
        return toolbar;
    }

}
