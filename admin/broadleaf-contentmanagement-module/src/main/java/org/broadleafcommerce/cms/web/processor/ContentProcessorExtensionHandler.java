/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.web.processor;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.structure.dto.StructuredContentDTO;
import org.broadleafcommerce.common.web.deeplink.DeepLink;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;

import java.util.List;
import java.util.Map;

/**
 * Extension handler for the {@link ContentProcessor}
 *
 * @author Elbert Bautista (elbertbautista)
 */
public interface ContentProcessorExtensionHandler extends ExtensionHandler {

    /**
     * This method returns whether or not this implementation should handle the content lookup
     * This is determined by the element attribute values that are passed in.
     *
     * @param element - the Thymeleaf Processor element
     * @return ExtensionResultStatusType
     */
    public ExtensionResultStatusType shouldHandleContentLookup(Element element);

    /**
     * This method returns content given a specific element attribute
     * <br>
     * Returns active content items for the passed in sandbox that match the passed in type.
     * <br>
     * For example, the AdvancedCMS module allows lookup by "layoutArea", you would then
     * pass in an extensionFieldName="layoutArea", extensionFieldValue="My Layout Area 1"
     * This would return all the content items in that particular layout area. Other modules
     * may pass in their specific lookup criteria. See the modules docs for more details.
     * <br>
     * The SandBox parameter impacts the results as follows.  If a <code>SandBoxType</code> of
     * production is passed in, only those items in that SandBox are returned.
     * <br>
     * If a non-production SandBox is passed in, then the method will return the items associatd
     * with the related production SandBox and then merge in the results of the passed in SandBox.
     *
     * @param contentItems - the list of DTOs to add to
     * @param sandBox - the sandbox to find structured content items (null indicates items that are in production for
     *                  sites that are single tenant.
     * @param locale - the locale
     * @param count - the max number of content items to return
     * @param ruleDTOs - a Map of objects that will be used in MVEL processing.
     * @param secure - set to true if the request is being served over https
     * @param element - the Thymeleaf Processor element
     * @return - ExtensionResultStatusType
     * @see ContentProcessor
     */
    public ExtensionResultStatusType lookupContentByElementAttribute(List<StructuredContentDTO> contentItems,
            SandBox sandBox, Locale locale, Integer count, Map<String,Object> ruleDTOs,
            boolean secure, Element element);

    /**
     * This method will add any additional attributes to the model that the extension needs
     *
     * @param arguments - the Thymeleaf Processor arguments
     * @param element - the Thymeleaf Processor element
     * @return - ExtensionResultStatusType
     */
    public ExtensionResultStatusType addAdditionalFieldsToModel(Arguments arguments, Element element);

    /**
     * Provides a hook point for an extension of content processor to optionally add in deep links
     * for a content item based on its extension fields
     * @param links
     * @param arguments
     * @param element
     * @return ExtensionResultStatusType
     */
    public ExtensionResultStatusType addExtensionFieldDeepLink(List<DeepLink> links, Arguments arguments, Element element);
    
    /**
     * Provides a hook point to allow extension handlers to modify the generated deep links.
     * 
     * @param links
     * @return ExtensionResultStatusType
     */
    public ExtensionResultStatusType postProcessDeepLinks(List<DeepLink> links);

}
