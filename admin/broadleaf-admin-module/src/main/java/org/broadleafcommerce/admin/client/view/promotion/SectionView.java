/*
 * Copyright 2008-2012 the original author or authors.
 *
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

	public SectionView(String title) {
        setWidth("98%");

        Label label = new Label();
        label.setContents(title);
        label.setStyleName("bl-promo-header");
        label.setAutoHeight();
        label.setWidth100();
        addMember(label);

        contentLayout = new VStack();
        contentLayout.setStyleName("bl-promo-section");
        contentLayout.setWidth100();
        addMember(contentLayout);

	}

	public VStack getContentLayout() {
		return contentLayout;
	}

}
