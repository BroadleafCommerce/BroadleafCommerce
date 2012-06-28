package org.broadleafcommerce.openadmin.client.view.dynamic.form;

import org.broadleafcommerce.openadmin.client.view.dynamic.RichTextToolbar;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.widgets.WidgetCanvas;
import com.smartgwt.client.widgets.form.fields.CanvasItem;

public class HTMLTextItem extends CanvasItem {
	private final RichTextArea textArea;
	private final RichTextToolbar toolBar;
	public HTMLTextItem() {
		super();
		textArea = new RichTextArea();
		toolBar = new RichTextToolbar(getTextArea());
		VerticalPanel vp = new VerticalPanel();
		getTextArea().setWidth("100%");
		getTextArea().setHeight("100%");
		vp.add(getToolBar());
		vp.add(getTextArea());
		///CanvasItem cItem = new CanvasItem();
		
		WidgetCanvas wc=new WidgetCanvas(vp);
		wc.setCanDragResize(true);
		setCanvas(wc);
		final HTMLTextItem item=this;

		Command saveCommand=new Command() {
			@Override
			public void execute() {
		    	item.storeValue(getHTMLValue());
			}
		};
		//since this is a wrpper for gwt composite, we need to tell the parent form when something change
		//change will then be reflected in the dynamic form , for example enabling the save button. 
		toolBar.setSaveCommand(saveCommand);
	}
	private RichTextArea getTextArea() {
		return textArea;
	}
	private RichTextToolbar getToolBar() {
		return toolBar;
	}

	public void setHTMLValue(String value) {
	     storeValue(value);
	     getToolBar().setHTML(value);
		 //getTextArea().setHTML(value);
	}

	public String getHTMLValue() {
		String x=getToolBar().getHTML();
		return x;
	} 
	public void addAssetHandler(final Command command) {
		toolBar.addAssetHandler(command);
	}
	public void insertAsset(String fileExtension, String name,
			String staticAssetFullUrl) {
		//let the toolbar handle how to add the new image url inside the textare
		toolBar.insertAsset(fileExtension, name, staticAssetFullUrl);
		
	}

}
