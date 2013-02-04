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

package org.broadleafcommerce.core.web.processor;

import net.jawr.web.resource.bundle.renderer.BundleRenderer;
import net.jawr.web.resource.bundle.renderer.BundleRendererContext;
import net.jawr.web.servlet.RendererRequestUtils;

import org.apache.commons.logging.Log;
import org.springframework.stereotype.Component;
import org.thymeleaf.Arguments;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractUnescapedTextChildModifierAttrProcessor;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * A Thymeleaf processor that resolves a Jawr JavaScript bundle
 * 
 * @author apazzolini
 */
@Component("blJawrScriptProcessor")
public abstract class JawrAbstractProcessor extends AbstractUnescapedTextChildModifierAttrProcessor {
    
    public JawrAbstractProcessor(String elementName) {
        super(elementName);
    }

    @Override
    public int getPrecedence() {
        return 100000;
    }

    protected abstract BundleRenderer getBundleRenderer(ServletContext servletContext);

    protected abstract Log getLogger();

    @Override
    protected String getText(Arguments arguments, Element element, String attributeName) {
        String bundleName = element.getAttributeValue(attributeName);
        bundleName = "/WEB-INF/default-theme" + bundleName;
        WebContext webContext = (WebContext) arguments.getContext();
        ServletContext servletContext = webContext.getServletContext();
        HttpServletRequest request = webContext.getHttpServletRequest();

        BundleRenderer renderer = getBundleRenderer(servletContext);
        BundleRendererContext bundleRenderedCtx = RendererRequestUtils.getBundleRendererContext(request, renderer);

        StringWriter out = new StringWriter();
        try {
            renderer.renderBundleLinks(bundleName, bundleRenderedCtx, out);
        } catch (IOException e) {
            getLogger().error(e);
        }
        out.flush();
        
        return out.toString();
    }
    
}
