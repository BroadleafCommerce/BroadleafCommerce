package org.broadleafcommerce.layout.tags;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.broadleafcommerce.marketing.domain.TargetContent;
import org.broadleafcommerce.marketing.service.TargetContentService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ContentDisplayTag extends BodyTagSupport {
    private static final long serialVersionUID = 1L;

    private String varList;
    private String varFirstItem;
    private String contentKey;
    private String contentType;

    public String getContentKey() {
        return contentKey;
    }
    public void setContentKey(String contentKey) {
        this.contentKey = contentKey;
    }
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getVarList() {
        return varList;
    }

    public void setVarList(String varList) {
        this.varList = varList;
    }
    public String getVarFirstItem() {
        return varFirstItem;
    }
    public void setVarFirstItem(String varFirstItem) {
        this.varFirstItem = varFirstItem;
    }
    @Override
    public int doStartTag() throws JspException{
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
        TargetContentService targetContentService = (TargetContentService) applicationContext.getBean("targetContentService");
        List<TargetContent> targetContent = targetContentService.findTargetContentsByNameType(contentKey, contentType);
        pageContext.setAttribute(varList, targetContent);
        pageContext.setAttribute(varFirstItem, (targetContent.size() > 0)?targetContent.get(0):null);
        return EVAL_PAGE;
    }
}
