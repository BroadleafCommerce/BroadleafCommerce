/*
 * Copyright 2012 the original author or authors.
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

package org.broadleafcommerce.cms.web.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.structure.domain.StructuredContentType;
import org.broadleafcommerce.cms.structure.dto.StructuredContentDTO;
import org.broadleafcommerce.cms.structure.service.StructuredContentService;
import org.broadleafcommerce.common.RequestDTO;
import org.broadleafcommerce.common.TimeDTO;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.thymeleaf.Arguments;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.dom.Element;
import org.thymeleaf.spring3.context.SpringWebContext;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

/**
 * Processor used to display structured content that is maintained with the Broadleaf CMS.
 *
 * Usage based on the following attributes:<br>
 * <ul>
 *     <li>contentType (required) - specifies the content you are retrieving</li>
 *     <li>contentName - if included will retrieve only content that matches the name.   When no name is specified,
 *                       all matching content items of the passed in type are retrieved.</li>
 *     <li>maxResults - if specified limits the results to a specified number of items.   The content will be returned
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
 * </ul>
 */
@Component("blContentProcessor")
public class ContentProcessor extends AbstractModelVariableModifierProcessor {

    protected final Log LOG = LogFactory.getLog(getClass());
    public static final String REQUEST_DTO = "blRequestDTO";
    public static final String BLC_RULE_MAP_PARAM = "blRuleMap";
    
    private StructuredContentService structuredContentService;
    private StaticAssetService staticAssetService;        
    
    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public ContentProcessor() {
        super("content");
    }
    
    @Override
    public int getPrecedence() {
        return 10000;
    }
    
    /**
     * Returns a default name
     * @param element
     * @param valueName
     * @return
     */
    private String getAttributeValue(Element element, String valueName, String defaultValue) {
        String returnValue = element.getAttributeValue(valueName);
        if (returnValue == null) {
            return defaultValue;
        } else {
            return returnValue;
        }
    }   

    @Override
    protected void modifyModelAttributes(Arguments arguments, Element element) {        
        String contentType = element.getAttributeValue("contentType");
        String contentName = element.getAttributeValue("contentName");
        String maxResultsStr = element.getAttributeValue("maxResults");
        Integer maxResults = null;
        if (maxResultsStr != null) {
            maxResults = Ints.tryParse(maxResultsStr);
        }
        if (maxResults == null) {
            maxResults = Integer.MAX_VALUE;
        }
        
        String contentListVar = getAttributeValue(element, "contentListVar", "contentList");
        String contentItemVar = getAttributeValue(element, "contentItemVar", "contentItem");
        String numResultsVar = getAttributeValue(element, "numResultsVar", "numResults");

        initServices(arguments);
                
        IWebContext context = (IWebContext) arguments.getContext();     
        HttpServletRequest request = context.getHttpServletRequest();   
        BroadleafRequestContext blcContext = BroadleafRequestContext.getBroadleafRequestContext();
        
        Map<String, Object> mvelParameters = buildMvelParameters(request, arguments, element);
        SandBox currentSandbox = blcContext.getSandbox();

        List<StructuredContentDTO> contentItems;
        StructuredContentType structuredContentType = structuredContentService.findStructuredContentTypeByName(contentType);

        Locale locale = blcContext.getLocale();
            

        if (structuredContentType == null) {
            contentItems = structuredContentService.lookupStructuredContentItemsByName(currentSandbox, contentName, locale, maxResults, mvelParameters, isSecure(request));
        } else {
            if (contentName == null || "".equals(contentName)) {
                contentItems = structuredContentService.lookupStructuredContentItemsByType(currentSandbox, structuredContentType, locale, maxResults, mvelParameters, isSecure(request));
            } else {
                contentItems = structuredContentService.lookupStructuredContentItemsByName(currentSandbox, structuredContentType, contentName, locale, maxResults, mvelParameters, isSecure(request));
            }
        }                       
                            
        if (contentItems.size() > 0) {
            List<Map<String,String>> contentItemFields = new ArrayList<Map<String, String>>();          
            
            for(StructuredContentDTO item : contentItems) {
                contentItemFields.add(item.getValues());
            }
            addToModel(arguments, contentItemVar, contentItemFields.get(0));
            addToModel(arguments, contentListVar, contentItemFields);
            addToModel(arguments, numResultsVar, contentItems.size());
        } else {
            if (LOG.isInfoEnabled()) {
                LOG.info("**************************The contentItems is null*************************");
            }
            addToModel(arguments, contentItemVar, null);
            addToModel(arguments, contentListVar, null);
            addToModel(arguments, numResultsVar, 0);
        }       
    }
    
    /**
     * MVEL is used to process the content targeting rules.
     *
     * @param request
     * @return
     */
    private Map<String, Object> buildMvelParameters(HttpServletRequest request, Arguments arguments, Element element) {
        TimeZone timeZone = BroadleafRequestContext.getBroadleafRequestContext().getTimeZone();

        final TimeDTO timeDto;
        if (timeZone != null) {
            timeDto = new TimeDTO(SystemTime.asCalendar(timeZone));
        } else {
            timeDto = new TimeDTO();
        }

        RequestDTO requestDto = (RequestDTO) request.getAttribute(REQUEST_DTO);

        Map<String, Object> mvelParameters = new HashMap<String, Object>();
        mvelParameters.put("time", timeDto);
        mvelParameters.put("request", requestDto);

        String productString = element.getAttributeValue("product");

        if (productString != null) {
            Object product = StandardExpressionProcessor.processExpression(arguments, productString);
            if (product != null) {
                mvelParameters.put("product", product);
            }
        }

        @SuppressWarnings("unchecked")
        Map<String,Object> blcRuleMap = (Map<String,Object>) request.getAttribute(BLC_RULE_MAP_PARAM);
        if (blcRuleMap != null) {
            for (String mapKey : blcRuleMap.keySet()) {
                mvelParameters.put(mapKey, blcRuleMap.get(mapKey));
            }
        }

        return mvelParameters;
    }   
    
    protected void initServices(Arguments arguments) {
        if (structuredContentService == null || staticAssetService == null) {
            final ApplicationContext applicationContext = ((SpringWebContext) arguments.getContext()).getApplicationContext();            
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
    
}
