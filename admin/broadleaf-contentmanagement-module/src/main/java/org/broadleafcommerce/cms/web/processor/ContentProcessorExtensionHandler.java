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

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.web.deeplink.DeepLink;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;

import java.util.List;

/**
 * Extension handler for the {@link ContentProcessor}
 *
 * @author Elbert Bautista (elbertbautista)
 */
public interface ContentProcessorExtensionHandler extends ExtensionHandler {

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
