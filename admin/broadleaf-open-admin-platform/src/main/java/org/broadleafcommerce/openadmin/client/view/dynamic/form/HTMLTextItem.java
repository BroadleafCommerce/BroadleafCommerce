package org.broadleafcommerce.openadmin.client.view.dynamic.form;

import org.broadleafcommerce.openadmin.client.view.dynamic.RichTextToolbar;
import org.broadleafcommerce.openadmin.client.view.dynamic.RichTextToolbar.DisplayType;

import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.widgets.WidgetCanvas;
import com.smartgwt.client.widgets.form.fields.CanvasItem;

public class HTMLTextItem extends CanvasItem {

	private final RichTextArea textArea;
	private final RichTextToolbar toolBar;
	public HTMLTextItem() {
		this( DisplayType.DETAILED);
	}
	public HTMLTextItem(DisplayType displayType) {
		super();
		textArea = new RichTextArea();
	    textArea.addInitializeHandler(new InitializeHandler() {
            public void onInitialize(InitializeEvent ie) {
            IFrameElement fe = (IFrameElement)
            textArea.getElement().cast();
            if(fe==null) return;
            Style s = fe.getContentDocument().getBody().getStyle();
            s.setProperty("fontFamily", "helvetica, sans-serif");
            s.setProperty("fontSize", "12");
            
            }
    }); 
		toolBar = new RichTextToolbar(getTextArea(),displayType);
		VerticalPanel vp = new VerticalPanel();
	    if(displayType == displayType.DETAILED) {
		    getTextArea().setWidth("100%");
		    getTextArea().setHeight("100%");
	    	toolBar.setWidth("100%");
	    	toolBar.setHeight("100%");
	    } else {
	    	getTextArea().setWidth("100%");
			getTextArea().setHeight("60px");
			toolBar.setWidth("100%");
			toolBar.setHeight("40px");
	    }
	    vp.setBorderWidth(2);

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
		//setValue(value);
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
@Override
public Object getValue() {
	// TODO Auto-generated method stub
	
	return getHTMLValue();
}
@Override
public void setValue(Object value) {
	
    super.setValue(value);
	setHTMLValue(value.toString());
}
@Override
public void setValue(String value) {
	
	
	if(value==null) value="";
	super.setValue(value);
	setHTMLValue(value);
}

 @Override
public void setDisabled(Boolean disabled) {
	// TODO Auto-generated method stub
	super.setDisabled(disabled);
	getToolBar().setVisible(!disabled);
	getTextArea().setEnabled(!disabled);
}

}
