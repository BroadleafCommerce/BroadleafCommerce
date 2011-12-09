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

package org.broadleafcommerce.cms.web.structure;

import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.cms.structure.domain.StructuredContentType;
import org.broadleafcommerce.cms.structure.service.StructuredContentService;
import org.broadleafcommerce.cms.web.BroadleafProcessURLFilter;
import org.broadleafcommerce.cms.web.utility.FieldMapWrapper;
import org.broadleafcommerce.common.RequestDTO;
import org.broadleafcommerce.common.TimeDTO;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.time.SystemTime;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayContentTag extends BodyTagSupport {
    private static final long serialVersionUID = 1L;

    public static final String BLC_RULE_MAP_PARAM = "blRuleMap";

    // The following attribute is set in BroadleafProcessURLFilter
    public static final String REQUEST_DTO = "blRequestDTO";

    private String contentType;
    private String contentName;
    private Object product;
    private Integer count;
    private String contentListVar = "contentList";
    private String contentItemVar = "contentItem";
    private String numResultsVar = "numResults";
    private Locale locale;

    private StructuredContentService structuredContentService;


    /**
     * MVEL is used to process the content targeting rules.
     *
     *
     * @param request
     * @return
     */
    private Map<String,Object> buildMvelParameters(HttpServletRequest request) {
        TimeDTO timeDto = SystemTime.asTimeDTO();
        RequestDTO requestDto = (RequestDTO) request.getAttribute(REQUEST_DTO);

        Map<String, Object> mvelParameters = new HashMap<String, Object>();
        mvelParameters.put("time", timeDto);
        mvelParameters.put("request", requestDto);

        Map<String,Object> blcRuleMap = (Map<String,Object>) request.getAttribute(BLC_RULE_MAP_PARAM);
        if (blcRuleMap != null) {
            for (String mapKey : blcRuleMap.keySet()) {
                mvelParameters.put(mapKey, blcRuleMap.get(mapKey));
            }
        }

        if (product != null) {
            mvelParameters.put("product", product);
        }

        return mvelParameters;
    }


    protected StructuredContentService getStructuredContentService(PageContext context) {
        if (structuredContentService == null) {
            WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
            structuredContentService = (StructuredContentService) applicationContext.getBean("blStructuredContentService");
        }
        return structuredContentService;
    }

    // The following FieldMapWrapper allows for JSP syntax to be item.body
    // instead of item.body.value
    private List<Map> wrapFieldMaps(List<StructuredContent> contentItems) {
        if (contentItems != null) {
            List<Map> contentFieldList = new ArrayList<Map>();
            for (StructuredContent item : contentItems) {
                contentFieldList.add(new FieldMapWrapper(item.getStructuredContentFields()));
            }
            return contentFieldList;

        } else {
            return null;
        }
    }


    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        Map<String, Object> mvelParameters = buildMvelParameters(request);
        SandBox currentSandbox = (SandBox) request.getAttribute(BroadleafProcessURLFilter.SANDBOX_VAR);

        List<StructuredContent> contentItems;
        StructuredContentType structuredContentType = getStructuredContentService(pageContext).findStructuredContentTypeByName(contentType);

        if (locale == null) {
            locale = (Locale) request.getAttribute(BroadleafProcessURLFilter.LOCALE_VAR);
        }

        int cnt = (count == null) ? Integer.MAX_VALUE : count;

        if (contentName == null || "".equals(contentName)) {
            contentItems = structuredContentService.lookupStructuredContentItemsByType(currentSandbox, structuredContentType, locale, cnt, mvelParameters);
        } else {
            contentItems = structuredContentService.lookupStructuredContentItemsByName(currentSandbox, structuredContentType, contentName, locale, cnt, mvelParameters);
        }

        pageContext.setAttribute(getNumResultsVar(), contentItems.size());
        if (contentItems.size() > 0) {
            List<Map> returnFields = wrapFieldMaps(contentItems);
            pageContext.setAttribute(contentItemVar, returnFields.get(0));
            pageContext.setAttribute(contentListVar, returnFields);
            pageContext.setAttribute("structuredContentList", contentItems);
            pageContext.setAttribute(numResultsVar, contentItems.size());
        } else {
            pageContext.setAttribute(contentItemVar, contentItems);
            pageContext.setAttribute(contentListVar, null);
            pageContext.setAttribute("structuredContentList", null);
            pageContext.setAttribute(numResultsVar, 0);
        }
        
        return EVAL_BODY_INCLUDE;
    }


    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getContentListVar() {
        return contentListVar;
    }

    public void setContentListVar(String contentVar) {
        this.contentListVar = contentVar;
    }

    public String getContentItemVar() {
        return contentItemVar;
    }

    public void setContentItemVar(String contentItemVar) {
        this.contentItemVar = contentItemVar;
    }

    public String getNumResultsVar() {
        return numResultsVar;
    }

    public void setNumResultsVar(String numResultsVar) {
        this.numResultsVar = numResultsVar;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Object getProduct() {
        return product;
    }

    public void setProduct(Object product) {
        this.product = product;
    }
}
