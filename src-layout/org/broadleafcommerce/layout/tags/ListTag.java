package org.broadleafcommerce.layout.tags;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.Tag;

import org.apache.log4j.Logger;


public class ListTag extends BodyTagSupport {
    

    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger(this.getClass());
    
    private String headerJsp;
    private String footerJsp;
    private String listId;
    private String listClass;
    private int numAcross;
    private List<?> objectList;
    private String objectName = "listItem";
    private Iterator<?> iter;
    private int progress;
    
    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
	        out.write("<div");
	        if (listId != null) {
	            out.write(" id='" + listId + "'");
	        }
	        if (listClass != null) {
	            out.write(" class='" + listClass + "'");
	        }
            out.write(">");
            if (headerJsp != null) {
	            pageContext.include(headerJsp);
            }
	        if (getObjectList() == null) {
	            return Tag.SKIP_BODY;
	        }
	        progress = 1;
	        iter = getObjectList().listIterator();
	        if (!iter.hasNext()) {
	            return Tag.SKIP_BODY;
	        }
	        pageContext.setAttribute(objectName, iter.next());
	        out.write("<div class='listItem'>");
	        return Tag.EVAL_BODY_INCLUDE;
        } catch (IOException e) {
            log.error(e);
            return Tag.SKIP_PAGE;
        } catch (ServletException e) {
            log.error(e);
            return Tag.SKIP_PAGE;
        }
    }
    @Override
    public int doEndTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            if (footerJsp != null) {
	            pageContext.include(footerJsp);
            }
            out.write("</div><!-- end of list tag -->");
        } catch (IOException e) {
            log.error(e);
        } catch (ServletException e) {
            log.error(e);
        }
        return Tag.EVAL_PAGE;
    }
    @Override
    public int doAfterBody() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            out.write("</div>");
            if (progress % this.numAcross == 0) {
                out.write("<div style='clear:both'></div>");
            }
	        if (iter.hasNext()) {
		        pageContext.setAttribute(objectName, iter.next());
	            progress++;
	            out.write("<div class='listItem'>");
	            return IterationTag.EVAL_BODY_AGAIN;
	        } else {
	            if (progress % this.numAcross != 0) {
	                out.write("<div style='clear:both'></div>");
	            }
	            return Tag.SKIP_BODY;
	        }
        } catch (IOException e) {
            log.error(e);
            return Tag.SKIP_PAGE;
        }
    }
    
    public String getListId() {
        return listId;
    }
    public void setListId(String listId) {
        this.listId = listId;
    }
    public String getListClass() {
        return listClass;
    }
    public void setListClass(String listClass) {
        this.listClass = listClass;
    }
    public String getHeaderJsp() {
        return headerJsp;
    }
    public void setHeaderJsp(String headerJsp) {
        this.headerJsp = headerJsp;
    }
    public String getFooterJsp() {
        return footerJsp;
    }
    public void setFooterJsp(String footerJsp) {
        this.footerJsp = footerJsp;
    }
    public int getNumAcross() {
        return numAcross;
    }
    public void setNumAcross(int numAcross) {
        this.numAcross = numAcross;
    }
    public List<?> getObjectList() {
        return objectList;
    }
    public void setObjectList(List<?> objectList) {
        this.objectList = objectList;
    }
    public String getObjectName() {
        return objectName;
    }
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
    
}
