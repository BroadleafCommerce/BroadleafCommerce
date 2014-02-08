/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.cms.web.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.structure.domain.StructuredContentType;
import org.broadleafcommerce.cms.structure.service.StructuredContentService;
import org.broadleafcommerce.cms.web.BroadleafProcessURLFilter;
import org.broadleafcommerce.common.RequestDTO;
import org.broadleafcommerce.common.TimeDTO;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.structure.dto.StructuredContentDTO;
import org.broadleafcommerce.common.time.SystemTime;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Tag used to display structured content that is maintained with the Broadleaf CMS.
 *
 * Usage based on the following attributes:<br>
 * <ul>
 *     <li>contentType (required) - specifies the content you are retrieving</li>
 *     <li>contentName - if included will retrieve only content that matches the name.   When no name is specified,
 *                       all matching content items of the passed in type are retrieved.</li>
 *     <li>count - if specified limits the results to a specified number of items.   The content will be returned
 *                 according to priority.   If content items share the same priority, then they will be returned
 *                 randomly.  Consider the example with 5 matching items with priorities (1,2,3,3,3) respectively.  If
 *                 the count is set to 3.   Items 1 and 2 will ALWAYS be returned.   The third item returned will
 *                 randomy rotate through the 3rd, 4th, and 5th item.
 *     </li>
 *     <li>contentListVar - allows you to specify an alternate name for the list of content results.   By default,
 *                          the results are returned in the page attributed "contentList"</li>
 *     <li>contentItemVar - since a typical usage is to only return one item, the first item is returned in the
 *                          variable "contentItem".   This variable can be used to change the attribute name.</li>
 *     <li>numResultsVar  - variable holding the returns the number of results being returned to through the tag-lib.
 *                          defaults to "numResults".</li>
 *     <li>locale         - the locale being targeted for the content.   Defaults to locale that exists in
 *                          the requestAttribute "blLocale".   This is typically setup through Broadleaf's
 *                          ProcessURLFilter.</li>
 * </ul>
 */
public class DisplayContentTag extends BodyTagSupport {
    private static final long serialVersionUID = 1L;

    public static final String BLC_RULE_MAP_PARAM = "blRuleMap";

    // The following attribute is set in BroadleafProcessURLFilter
    public static final String REQUEST_DTO = "blRequestDTO";

    private String contentType;
    private String contentName;
    private Object product;
    private Integer count;
    private String contentListVar;
    private String contentItemVar;
    private String numResultsVar;
    private Locale locale;

    private StructuredContentService structuredContentService;
    private StaticAssetService staticAssetService;
    
    public DisplayContentTag() {
        initVariables();
    }


    /**
     * MVEL is used to process the content targeting rules.
     *
     *
     * @param request
     * @return
     */
    private Map<String,Object> buildMvelParameters(HttpServletRequest request) {
        TimeDTO timeDto = new TimeDTO(SystemTime.asCalendar());
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


    protected void initServices() {
        if (structuredContentService == null || staticAssetService == null) {
            WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
            structuredContentService = (StructuredContentService) applicationContext.getBean("blStructuredContentService");
            staticAssetService = (StaticAssetService) applicationContext.getBean("blStaticAssetService");
        }
    }
    
    public boolean isSecure(HttpServletRequest request) {
        boolean secure = false;
        if (request != null) {
             secure = ("HTTPS".equalsIgnoreCase(request.getScheme()) || request.isSecure());
        }
        return secure;
    }

    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        Map<String, Object> mvelParameters = buildMvelParameters(request);
        initServices();

        List<StructuredContentDTO> contentItems;
        StructuredContentType structuredContentType = structuredContentService.findStructuredContentTypeByName(contentType);
        assert(contentName != null && !"".equals(contentName) && structuredContentType != null);

        if (locale == null) {
            locale = (Locale) request.getAttribute(BroadleafProcessURLFilter.LOCALE_VAR);
        }

        int cnt = (count == null) ? Integer.MAX_VALUE : count;

        if (structuredContentType == null) {
            contentItems = structuredContentService.lookupStructuredContentItemsByName(contentName, locale, cnt, mvelParameters, isSecure(request));
        } else {
            if (contentName == null || "".equals(contentName)) {
                contentItems = structuredContentService.lookupStructuredContentItemsByType(structuredContentType, locale, cnt, mvelParameters, isSecure(request));
            } else {
                contentItems = structuredContentService.lookupStructuredContentItemsByName(structuredContentType, contentName, locale, cnt, mvelParameters, isSecure(request));
            }
        }
                
        pageContext.setAttribute(getNumResultsVar(), contentItems.size());
        if (contentItems.size() > 0) {
            List<Map<String,String>> contentItemFields = new ArrayList<Map<String, String>>();
            for(StructuredContentDTO item : contentItems) {
                contentItemFields.add(item.getValues());
            }
            pageContext.setAttribute(contentItemVar, contentItemFields.get(0));
            pageContext.setAttribute(contentListVar, contentItemFields);
            pageContext.setAttribute("structuredContentList", contentItems);
            pageContext.setAttribute(numResultsVar, contentItems.size());
        } else {
            pageContext.setAttribute(contentItemVar, null);
            pageContext.setAttribute(contentListVar, null);
            pageContext.setAttribute("structuredContentList", null);
            pageContext.setAttribute(numResultsVar, 0);
        }
        
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() throws JspException {
        int returnVal = super.doEndTag();
        initVariables();
        return returnVal;
    }

    private void initVariables() {
        contentType=null;
        contentName=null;
        product=null;
        count=null;
        locale=null; 
        contentListVar = "contentList";
        contentItemVar = "contentItem";
        numResultsVar = "numResults";
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
