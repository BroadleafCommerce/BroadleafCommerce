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
package org.broadleafcommerce.cms.web.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.structure.domain.StructuredContentType;
import org.broadleafcommerce.cms.structure.service.StructuredContentService;
import org.broadleafcommerce.cms.web.deeplink.ContentDeepLinkServiceImpl;
import org.broadleafcommerce.common.RequestDTO;
import org.broadleafcommerce.common.TimeDTO;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.structure.dto.StructuredContentDTO;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.deeplink.DeepLink;
import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.thymeleaf.Arguments;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.dom.Element;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.AssignationUtils;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

import com.google.common.primitives.Ints;

/**
 * Processor used to display structured content that is maintained with the Broadleaf CMS.
 *
 * Usage based on the following attributes:<br>
 * <ul>
 *     <li>contentType (required *) - only required if an extension manager is not defined to handle content lookup.
 *                                    If the content type is not found, it will try to retrieve content from any registered
 *                                    extension handlers.
 *                                    Specifies the content you are retrieving.</li>
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
 *     <li>fieldFilters  - Thymeleaf key-value pair to filter the resulting StructuredContentDTO by particular field values.
 *                          For instance, if you had a field in a piece of structured content called 'featured' and you
 *                          wanted to return all of the featured content items, you could do the following:
 *                          
 *                          <blc:content fieldFilters="featured=${'true'},otherField=${'someValue'}" />
 *     <li>sorts         - sorts to apply to the resulting list of content. These should be key-value pairs corresponding
 *                          where the key is the field to sort and the value is the direction of the sort. If unspecified,
 *                          the default sorting is used (by priority). The sort fields must occur in the dynamic fields
 *                          for that piece of structured content. For instance:
 *                          
 *                          <blc:content sort="dynamicFieldA='DESCENDING',dynamicFieldB='ASCENDING'" />
 *                          
 *                          The list will be sorted first by dynamicFieldA descending and then dynamicFieldB ascending
 * </ul>
 */
public class ContentProcessor extends AbstractModelVariableModifierProcessor {

    protected final Log LOG = LogFactory.getLog(getClass());
    public static final String REQUEST_DTO = "blRequestDTO";
    public static final String BLC_RULE_MAP_PARAM = "blRuleMap";
    
    @Resource(name = "blStructuredContentService")
    protected StructuredContentService structuredContentService;
    
    @Resource(name = "blStaticAssetService")
    protected StaticAssetService staticAssetService;

    @Resource(name = "blContentProcessorExtensionManager")
    protected ContentProcessorExtensionManager extensionManager;
    
    @Resource(name = "blContentDeepLinkService")
    protected ContentDeepLinkServiceImpl contentDeepLinkService;
    
    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public ContentProcessor() {
        super("content");
    }
    
    public ContentProcessor(String elementName) {
        super(elementName);
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
    protected String getAttributeValue(Element element, String valueName, String defaultValue) {
        String returnValue = element.getAttributeValue(valueName);
        if (returnValue == null) {
            return defaultValue;
        } else {
            return returnValue;
        }
    }   

    @Override
    protected void modifyModelAttributes(final Arguments arguments, Element element) {        
        String contentType = element.getAttributeValue("contentType");
        String contentName = element.getAttributeValue("contentName");
        String maxResultsStr = element.getAttributeValue("maxResults");

        boolean extensionLookupNotHandled = ExtensionResultStatusType.NOT_HANDLED.equals(extensionManager.getProxy().shouldHandleContentLookup(element));

        if (StringUtils.isEmpty(contentType) && StringUtils.isEmpty(contentName) && extensionLookupNotHandled) {
            throw new IllegalArgumentException("The content processor must have a non-empty attribute value for 'contentType' or 'contentName' or register an extension manager to handle content lookup");

        }

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
        
        String fieldFilters = element.getAttributeValue("fieldFilters");
        final String sorts = element.getAttributeValue("sorts");

        IWebContext context = (IWebContext) arguments.getContext();     
        HttpServletRequest request = context.getHttpServletRequest();   
        BroadleafRequestContext blcContext = BroadleafRequestContext.getBroadleafRequestContext();
        
        Map<String, Object> mvelParameters = buildMvelParameters(request, arguments, element);
        SandBox currentSandbox = blcContext.getSandBox();

        List<StructuredContentDTO> contentItems;
        StructuredContentType structuredContentType = null;
        if (contentType != null ) {
            structuredContentType = structuredContentService.findStructuredContentTypeByName(contentType);
        }

        Locale locale = blcContext.getLocale();
            
        contentItems = getContentItems(contentName, maxResults, request, mvelParameters, currentSandbox, structuredContentType, locale, arguments, element);
        
        if (contentItems.size() > 0) {
            
            // sort the resulting list by the configured property sorts on the tag
            if (StringUtils.isNotEmpty(sorts)) {
                Collections.sort(contentItems, new Comparator<StructuredContentDTO>() {
                    @Override
                    public int compare(StructuredContentDTO o1, StructuredContentDTO o2) {
                        AssignationSequence sortAssignments = AssignationUtils.parseAssignationSequence(arguments.getConfiguration(), arguments, sorts, false);
                        CompareToBuilder compareBuilder = new CompareToBuilder();
                        for (Assignation sortAssignment : sortAssignments) {
                            String property = sortAssignment.getLeft().getStringRepresentation();
                            
                            Object val1 = o1.getPropertyValue(property);
                            Object val2 = o2.getPropertyValue(property);
                            
                            if (sortAssignment.getRight().execute(arguments.getConfiguration(), arguments).equals("ASCENDING")) {
                                compareBuilder.append(val1, val2);
                            } else {
                                compareBuilder.append(val2, val1);
                            }
                        }
                        return compareBuilder.toComparison();
                    }
                });
            }
            
            List<Map<String, Object>> contentItemFields = new ArrayList<Map<String, Object>>();          
            
            for (StructuredContentDTO item : contentItems) {
                if (StringUtils.isNotEmpty(fieldFilters)) {
                    AssignationSequence assignments = AssignationUtils.parseAssignationSequence(arguments.getConfiguration(), arguments, fieldFilters, false);
                    boolean valid = true;
                    for (Assignation assignment : assignments) {
                        
                        if (ObjectUtils.notEqual(assignment.getRight().execute(arguments.getConfiguration(), arguments),
                                                item.getValues().get(assignment.getLeft().getStringRepresentation()))) {
                            LOG.info("Excluding content " + item.getId()  + " based on the property value of " + assignment.getLeft().getStringRepresentation());
                            valid = false;
                            break;
                        }
                    }
                    if (valid) {
                        contentItemFields.add(item.getValues());
                    }
                } else {
                    contentItemFields.add(item.getValues());
                }
            }

            Map<String, Object> contentItem = null;
            if (contentItemFields.size() > 0) {
                contentItem = contentItemFields.get(0);
            }

            addToModel(arguments, contentItemVar, contentItem);
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
        
        String deepLinksVar = element.getAttributeValue("deepLinks");
        if (StringUtils.isNotBlank(deepLinksVar) && contentItems.size() > 0 ) {
            List<DeepLink> links = contentDeepLinkService.getLinks(contentItems.get(0));
            extensionManager.getProxy().addExtensionFieldDeepLink(links, arguments, element);
            extensionManager.getProxy().postProcessDeepLinks(links);
            addToModel(arguments, deepLinksVar, links);
        }
    }

    /**
     * @param contentName name of the content to be looked up (can be null)
     * @param maxResults maximum results to return
     * @param request servlet request
     * @param mvelParameters values that should be considered when filtering the content list by rules
     * @param structuredContentType the type of content that should be returned
     * @param locale current locale
     * @param arguments Thymeleaf Arguments passed into the tag
     * @param element element context that this Thymeleaf processor is being executed in
     * @return
     */
    protected List<StructuredContentDTO> getContentItems(String contentName, Integer maxResults, HttpServletRequest request,
            Map<String, Object> mvelParameters,
            SandBox currentSandbox,
            StructuredContentType structuredContentType,
            Locale locale, Arguments arguments, Element element) {
        List<StructuredContentDTO> contentItems;
        if (structuredContentType == null) {
            if (contentName == null || "".equals(contentName)) {
                contentItems = new ArrayList<StructuredContentDTO>();

                // allow modules to lookup content by a specific field
                // e.g. (For the AdvancedCMS module you can lookup by "layoutArea")
                extensionManager.getProxy().lookupContentByElementAttribute(contentItems, currentSandbox, locale, maxResults, mvelParameters, isSecure(request), element);

            } else {
                contentItems = structuredContentService.lookupStructuredContentItemsByName(contentName, locale, maxResults, mvelParameters, isSecure(request));
            }
        } else {
            if (contentName == null || "".equals(contentName)) {
                contentItems = structuredContentService.lookupStructuredContentItemsByType(structuredContentType, locale, maxResults, mvelParameters, isSecure(request));
            } else {
                contentItems = structuredContentService.lookupStructuredContentItemsByName(structuredContentType, contentName, locale, maxResults, mvelParameters, isSecure(request));
            }
        }

        //add additional fields to the model
        extensionManager.getProxy().addAdditionalFieldsToModel(arguments, element);

        return contentItems;
    }
    
    /**
     * MVEL is used to process the content targeting rules.
     *
     * @param request
     * @return
     */
    protected Map<String, Object> buildMvelParameters(HttpServletRequest request, Arguments arguments, Element element) {
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
            final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(arguments.getConfiguration());
            Expression expression = (Expression) expressionParser.parseExpression(arguments.getConfiguration(), arguments, productString);
            Object product = expression.execute(arguments.getConfiguration(), arguments);

            if (product != null) {
                mvelParameters.put("product", product);
            }
        }

        String categoryString = element.getAttributeValue("category");

        if (categoryString != null) {
            final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(arguments.getConfiguration());
            Expression expression = (Expression) expressionParser.parseExpression(arguments.getConfiguration(), arguments, productString);
            Object category = expression.execute(arguments.getConfiguration(), arguments);
            if (category != null) {
                mvelParameters.put("category", category);
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
    
    public boolean isSecure(HttpServletRequest request) {
        boolean secure = false;
        if (request != null) {
             secure = ("HTTPS".equalsIgnoreCase(request.getScheme()) || request.isSecure());
        }
        return secure;
    }
    
}
