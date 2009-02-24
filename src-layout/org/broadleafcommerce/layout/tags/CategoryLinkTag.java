package org.broadleafcommerce.layout.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.apache.log4j.Logger;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.web.CommerceRequestStateImpl;

public class CategoryLinkTag extends BodyTagSupport {
    private Logger log = Logger.getLogger(this.getClass());
    private static final long serialVersionUID = 1L;
    private Category category;
    private String styleClass;
    private boolean link;

	@Override
	public int doStartTag() throws JspException {
    	JspWriter out = pageContext.getOut();
    	HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
    	CommerceRequestStateImpl requestState = (CommerceRequestStateImpl) CommerceRequestStateImpl.getRequestState(request);

    	try {
        	if (getCategory() == null){
        		return Tag.SKIP_BODY;
        	}
    		String style = (getStyleClass() != null)? getStyleClass(): "";
    		if (isLink()){
    			out.write("<a class='" + style + "' href='" + request.getContextPath() + "/" + requestState.getCatalogPrefix() + "/" + buildLink(null, category) + "'>" + category.getName() + "</a>");
    		} else{
    			out.write(request.getContextPath() + "/" + requestState.getCatalogPrefix() + "/" + buildLink(null, category));
        	}
		} catch (IOException e) {
            log.error(e);
            return Tag.SKIP_PAGE;
		}


		return super.doStartTag();
	}

    private String buildLink(String link, Category category){
    	if (category == null){
    		return link;
    	} else {
    		if (link == null){
    			link = category.getUrlKey();
    		} else {
    			link = category.getUrlKey() + "/" + link;
    		}
    	}
    	return buildLink(link, category.getParentCategory());
    }

	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public boolean isLink() {
		return link;
	}

	public void setLink(boolean link) {
		this.link = link;
	}
}
