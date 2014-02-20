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

package org.thymeleaf.templatewriter;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Node;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Random;

/**
 * Wrapper for Thymeleaf's {@link AbstractGeneralTemplateWriter} that provides content caching
 * on the node level.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class CacheAwareGeneralTemplateWriter extends AbstractGeneralTemplateWriter {

    protected static final Log LOG = LogFactory.getLog(CacheAwareGeneralTemplateWriter.class);

    protected Cache cache;
    protected AbstractGeneralTemplateWriter delegateWriter;

    public CacheAwareGeneralTemplateWriter(AbstractGeneralTemplateWriter delegateWriter) {
        this.delegateWriter = delegateWriter;
    }
    
    public void printChildProperties(Node node) {
        System.out.println("found " + node);
        if (node.getNodePropertyNames().size() > 0) {
            System.out.println("found properties on node + " + node.getNodePropertyNames());
        }
        
        if (node instanceof org.thymeleaf.dom.Element) {
            org.thymeleaf.dom.Element e = (org.thymeleaf.dom.Element) node;
            
            if (e.hasChildren()) {
                for (Node child : e.getChildren()) {
                    printChildProperties(child);
                }
            }
        }
    }

    @Override
    public void writeNode(final Arguments arguments, final Writer writer, final Node node) 
            throws IOException {
        if (!(node instanceof org.thymeleaf.dom.Element)) {
            super.writeNode(arguments, writer, node);
            return;
        }
        
        org.thymeleaf.dom.Element e = (org.thymeleaf.dom.Element) node;
        
        String cacheKey = e.getAttributeValueFromNormalizedName("cachekey");
        
        boolean forceGenerate = new Random().nextBoolean();

        if (StringUtils.isNotBlank(cacheKey)) {
            Element element = getCache().get(cacheKey);
            String valueToWrite;
            
            if (forceGenerate) { 
                element = null;
            }
            
            long start = System.currentTimeMillis();
            if (element != null && element.getObjectValue() != null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Read template from cache - " + cacheKey);
                }
                valueToWrite = (String) element.getObjectValue();
                System.out.println("It took " + (System.currentTimeMillis() - start) + "ms to read from cache");
            } else {
                e.removeAttribute("cachekey");

                StringWriter w2 = new StringWriter();
                super.writeNode(arguments, w2, node);
                valueToWrite = w2.toString();

                element = new Element(cacheKey, valueToWrite);
                getCache().put(element);
                System.out.println("It took " + (System.currentTimeMillis() - start) + "ms to generate");
            }
            
            writer.write(valueToWrite);
        } else {
            super.writeNode(arguments, writer, node);
        }
    }

    @Override
    protected boolean shouldWriteXmlDeclaration() {
        return delegateWriter.shouldWriteXmlDeclaration();
    }

    @Override
    protected boolean useXhtmlTagMinimizationRules() {
        return delegateWriter.useXhtmlTagMinimizationRules();
    }

    public Cache getCache() {
        if (cache == null) {
            cache = CacheManager.getInstance().getCache("blTemplateElements");
        }
        return cache;
    }

}
