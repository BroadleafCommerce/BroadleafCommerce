package org.broadleafcommerce.gwt.client.widget;

public class LoadingMask {
	
	private String html;
	
	public LoadingMask(String detailText) {
		this("Processing...", detailText == null ? "" : detailText);
	}
	
	public LoadingMask(String headerText, String detailText) {
		setHtml("<h2>"
			+ headerText
			+ "</h2><img class='progressGrey' src='images/progressGrey.gif' width='323' height='28' /><p>"
			+ detailText + "</p>");
	}
	
	public void setHtml(String html) {
		this.html = html;
	}

	public String getHtml() {
		return html;
	}
	
}
