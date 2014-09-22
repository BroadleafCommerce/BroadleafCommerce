/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.common.extensibility.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.extensibility.context.merge.ImportProcessor;
import org.broadleafcommerce.common.extensibility.context.merge.exceptions.MergeException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Provides merged xml bean import into a declaring Spring application context. Generally, implementation overrides and custom
 * beans are included in a client implementation's war file and declared in a patchConfigLocations context-param in web.xml, which
 * is utilized by {@link org.broadleafcommerce.common.web.extensibility.MergeContextLoaderListener}. However, in some circumstances,
 * it may be desirable to load all the Broadleaf Commerce Spring beans into a declaring application context without the
 * aid of MergeContextLoaderListener. A sample use case for this would be a system that needs to run Broadleaf Commerce
 * as a set of embedded services, perhaps not inside of a traditional web container. Usage is as follows:
 *
 * {@code
 * <embedded:mergeImport>
 *     <embedded:location value="classpath:/my_app_context.xml"/>
 *     ...
 * </embedded:mergeImport>
 * }
 *
 * Since a new custom namespace is introduced, this special syntax requires the introduction of a new schema into app context
 * xml:
 *
 * {@code
 * <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:embedded="http://schema.broadleafcommerce.org/embedded"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
                http://www.springframework.org/schema/beans/spring-beans-4.1.xsd
                http://www.springframework.org/schema/context
                http://www.springframework.org/schema/context/spring-context-4.1.xsd
                http://schema.broadleafcommerce.org/embedded
                http://schema.broadleafcommerce.org/embedded/embedded-3.1.xsd">
 * }
 *
 * Note the introduction of the new embedded namespace. Using this syntax, Broadleaf Commerce merge functionality can be
 * achieved on a declared list of app context files and the resulting merged app context will be imported into the
 * declaring app context. This is conceptually similar to Spring's standard {@code <import location="..."/>} syntax for
 * importing additional app context xml files.
 *
 * @author Jeff Fischer
 */
public class EmbeddedBeanDefinitionParser extends AbstractBeanDefinitionParser {

    private static Log LOG = LogFactory.getLog(EmbeddedBeanDefinitionParser.class);

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        try {
            String[] broadleafConfigLocations = StandardConfigLocations.retrieveAll(StandardConfigLocations.APPCONTEXTTYPE);

            ArrayList<ResourceInputStream> sources = new ArrayList<ResourceInputStream>(20);
            for (String location : broadleafConfigLocations) {
                InputStream source = getClass().getClassLoader().getResourceAsStream(location);
                if (source != null) {
                    sources.add(new ResourceInputStream(source, location));
                }
            }
            ResourceInputStream[] filteredSources = new ResourceInputStream[]{};
            filteredSources = sources.toArray(filteredSources);

            List<Resource> locations = new ArrayList<Resource>();
            List<Element> overrideItemElements = DomUtils.getChildElementsByTagName(element, "location");
            for (Element overrideItem : overrideItemElements) {
                String location = overrideItem.getAttribute("value");
                Resource[] resources = ((ResourcePatternResolver) parserContext.getReaderContext().getResourceLoader()).getResources(location);
                if (ArrayUtils.isEmpty(resources)) {
                    LOG.warn("Unable to find the resource: " + location);
                } else {
                    locations.add(resources[0]);
                }
            }

            ResourceInputStream[] patches = new ResourceInputStream[locations.size()];
            for (int i = 0; i < locations.size(); i++) {
                patches[i] = new ResourceInputStream(locations.get(i).getInputStream(), locations.get(i).getDescription());

                if (patches[i] == null || patches[i].available() <= 0) {
                    throw new IOException("Unable to open an input stream on specified application context resource: " + locations.get(i).getDescription());
                }
            }

            ImportProcessor importProcessor = new ImportProcessor(parserContext.getReaderContext().getResourceLoader());
            try {
                filteredSources = importProcessor.extract(filteredSources);
                patches = importProcessor.extract(patches);
            } catch (MergeException e) {
                throw new FatalBeanException("Unable to merge source and patch locations", e);
            }

            Resource[] resources = new MergeApplicationContextXmlConfigResource().getConfigResources(filteredSources, patches);
            parserContext.getReaderContext().getReader().loadBeanDefinitions(resources[0]);
            return null;
        } catch (IOException e) {
            throw ExceptionHelper.refineException(e);
        }
    }
}
