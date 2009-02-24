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

public class BreadcrumbTag extends BodyTagSupport {
    private Logger log = Logger.getLogger(this.getClass());
    private static final long serialVersionUID = 1L;
    private String styleClass;
    private String separator;

	@Override
	public int doStartTag() throws JspException {
    	JspWriter out = pageContext.getOut();
    	HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
    	CommerceRequestStateImpl requestState = (CommerceRequestStateImpl) CommerceRequestStateImpl.getRequestState(request);

    	try {
        	if (requestState.getCategory() == null){
        		return Tag.SKIP_BODY;
        	}
			out.write(buildBreadcrumb(null, requestState.getCategory(), request.getContextPath() + "/" + requestState.getCatalogPrefix()));
		} catch (IOException e) {
            log.error(e);
            return Tag.SKIP_PAGE;
		}


		return super.doStartTag();
	}

    private String buildBreadcrumb(String crumb, Category category, String prefix){
    	if (category == null){
    		return crumb;
    	} else {
    		String style = (getStyleClass() != null)? getStyleClass(): "";
    		String sep = (getSeparator() != null)? getSeparator(): ">";

    		if (crumb == null){
    			crumb = "<a class='" + style + "' href='" + prefix + "/" + buildLink(null, category) + "'>" + category.getName() + "</a>";
    		} else {
    			crumb = "<a class='" + style + "' href='" + prefix + "/" + buildLink(null, category) + "'>" + category.getName() + "</a> " + sep + " " + crumb;
    		}
    	}
    	return buildBreadcrumb(crumb, category.getParentCategory(), prefix);
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

    public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

    public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

}
