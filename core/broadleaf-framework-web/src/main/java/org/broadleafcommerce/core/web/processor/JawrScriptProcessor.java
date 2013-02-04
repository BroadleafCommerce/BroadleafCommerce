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

import net.jawr.web.JawrConstant;
import net.jawr.web.resource.bundle.handler.ResourceBundlesHandler;
import net.jawr.web.resource.bundle.renderer.BundleRenderer;
import net.jawr.web.resource.bundle.renderer.JavascriptHTMLBundleLinkRenderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

/**
 * A Thymeleaf processor that resolves a Jawr JavaScript bundle
 * 
 * @author apazzolini
 */
@Component("blJawrScriptProcessor")
public class JawrScriptProcessor extends JawrAbstractProcessor {
    
    protected static final Log LOG = LogFactory.getLog(JawrScriptProcessor.class);

    public JawrScriptProcessor() {
        super("jawr-js");
    }
    
    @Override
    public int getPrecedence() {
        return 1;
    }

    @Override
    protected BundleRenderer getBundleRenderer(ServletContext servletContext) {
        String contextAttribute = JawrConstant.JS_CONTEXT_ATTRIBUTE;
        ResourceBundlesHandler rsHandler = (ResourceBundlesHandler) servletContext.getAttribute(contextAttribute);
        return new JavascriptHTMLBundleLinkRenderer(rsHandler, false);
    }

    @Override
    protected Log getLogger() {
        return LOG;
    }
    
}
