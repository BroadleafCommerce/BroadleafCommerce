/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        TargetContentService targetContentService = (TargetContentService) applicationContext.getBean("blTargetContentService");
        List<TargetContent> targetContent = targetContentService.findTargetContentsByNameType(contentKey, contentType);
        pageContext.setAttribute(varList, targetContent);
        pageContext.setAttribute(varFirstItem, (targetContent.size() > 0)?targetContent.get(0):null);
        return EVAL_PAGE;
    }
}
