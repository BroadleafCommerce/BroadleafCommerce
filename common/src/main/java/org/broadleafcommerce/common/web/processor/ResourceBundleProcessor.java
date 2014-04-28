/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.web.processor;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.resource.service.ResourceBundlingService;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.resource.BroadleafResourceHttpRequestHandler;
import org.broadleafcommerce.common.web.util.ProcessorUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.element.AbstractElementProcessor;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;


/**
 * A Thymeleaf processor that will take in a list of resource files to merge.
 * 
 * @author apazzolini
 */
public class ResourceBundleProcessor extends AbstractElementProcessor {
    
    @Resource(name = "blResourceBundlingService")
    protected ResourceBundlingService bundlingService;
    
    protected boolean getBundleEnabled() {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        return BLCSystemProperty.resolveBooleanSystemProperty("bundle.enabled") && (brc.getSandBox() == null || brc.getAdmin());
    }

    public ResourceBundleProcessor() {
        super("bundle");
    }
    
    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    protected ProcessorResult processElement(Arguments arguments, Element element) {
        String name = element.getAttributeValue("name");
        String mappingPrefix = element.getAttributeValue("mapping-prefix");
        NestableNode parent = element.getParent();
        List<String> files = new ArrayList<String>();
        for (String file : element.getAttributeValue("files").split(",")) {
            files.add(file.trim());
        }
        
        if (getBundleEnabled()) {
            String versionedBundle = bundlingService.getVersionedBundleName(name);
            if (StringUtils.isBlank(versionedBundle)) {
                BroadleafResourceHttpRequestHandler reqHandler = getRequestHandler(name, arguments);
                try {
                    versionedBundle = bundlingService.registerBundle(name, files, reqHandler);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                    .parseExpression(arguments.getConfiguration(), arguments, "@{'" + mappingPrefix + versionedBundle + "'}");
            String value = (String) expression.execute(arguments.getConfiguration(), arguments);
            Element e = getElement(value);
            parent.insertAfter(element, e);
        } else {
            List<String> additionalBundleFiles = bundlingService.getAdditionalBundleFiles(name);
            if (additionalBundleFiles != null) {
                files.addAll(additionalBundleFiles);
            }
            for (String file : files) {
                file = file.trim();
                Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                        .parseExpression(arguments.getConfiguration(), arguments, "@{'" + mappingPrefix + file + "'}");
                String value = (String) expression.execute(arguments.getConfiguration(), arguments);
                Element e = getElement(value);
                parent.insertBefore(element, e);
            }
        }
        
        parent.removeChild(element);
        return ProcessorResult.OK;
    }
    
    protected Element getScriptElement(String src) {
        Element e = new Element("script");
        e.setAttribute("type", "text/javascript");
        e.setAttribute("src", src);
        return e;
    }
    
    protected Element getLinkElement(String src) {
        Element e = new Element("link");
        e.setAttribute("rel", "stylesheet");
        e.setAttribute("href", src);
        return e;
    }
    
    protected Element getElement(String src) {
        if (src.contains(";")) {
            src = src.substring(0, src.indexOf(';'));
        }
        
        if (src.endsWith(".js")) {
            return getScriptElement(src);
        } else if (src.endsWith(".css")) {
            return getLinkElement(src);
        } else {
            throw new IllegalArgumentException("Unknown extension for: " + src + " - only .js and .css are supported");
        }
    }
    
    protected BroadleafResourceHttpRequestHandler getRequestHandler(String name, Arguments arguments) {
        BroadleafResourceHttpRequestHandler handler = null;
        if (name.endsWith(".js")) {
            handler = ProcessorUtils.getJsRequestHandler(arguments);
        } else if (name.endsWith(".css")) {
            handler = ProcessorUtils.getCssRequestHandler(arguments);
        }
        
        if (handler == null) {
            throw new IllegalArgumentException("Unknown extension for: " + name + " - only .js and .css are supported");
        }
        
        return handler;
    }
    
}
