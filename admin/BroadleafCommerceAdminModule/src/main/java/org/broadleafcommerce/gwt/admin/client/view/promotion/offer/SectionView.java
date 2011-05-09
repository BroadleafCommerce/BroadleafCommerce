package org.broadleafcommerce.gwt.admin.client.view.promotion.offer;

import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

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
