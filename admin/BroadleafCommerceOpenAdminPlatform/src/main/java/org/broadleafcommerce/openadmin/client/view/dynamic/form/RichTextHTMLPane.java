package org.broadleafcommerce.openadmin.client.view.dynamic.form;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Element;
import com.smartgwt.client.widgets.HTMLPane;

public class RichTextHTMLPane extends HTMLPane {

	public RichTextHTMLPane() {
		super();
	}
	
    public void setValue(String value) {
    	Node node = findIFrame();
    	setRichTextValue(node, value==null?"":value);
    }

    public String getValue()  {
    	Node node = findIFrame();
    	String response = findRichTextValue(node);
        return response;
    }
    
    public Node findIFrame() {
    	Element element = getDOM();
    	Node iframe =  findIFrame(element.getChildNodes());
    	return iframe;
    }
    
    public Node findIFrame(NodeList<Node> childNodes) {
    	for (int i = 0; i < childNodes.getLength(); i++) {
    		Node item = childNodes.getItem(i);
    		if (item instanceof Element && "IFRAME".equals(((Element) item).getTagName())) {
				return item;
			} else {
				Node childIFrame = findIFrame(item.getChildNodes());
				if (childIFrame != null) {
					return childIFrame;
				}
			}
    	}
    	return null;
    }
   
	public static native String findRichTextValue(Node iframeNode) /*-{
		return iframeNode.contentWindow.tinyMCE.get('richTextContent').getContent();
	}-*/;

	public static native void setRichTextValue(Node iframeNode, String value) /*-{
		var currentIFrameNode = iframeNode;
		var currentValue = value;
		iframeNode.onload = function() {
			currentIFrameNode.contentWindow.document.content = currentValue;
		}
	}-*/;
}
