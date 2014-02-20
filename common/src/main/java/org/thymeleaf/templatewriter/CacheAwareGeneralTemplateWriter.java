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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.CacheableNode;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Node;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

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

    @Override
    public void write(final Arguments arguments, final Writer writer, final Document document) 
                throws IOException {
        delegateWriter.write(arguments, writer, document);
    }
    
    @Override
    public void writeNode(final Arguments arguments, final Writer writer, final Node node) 
            throws IOException {
        if (node instanceof CacheableNode) {
            CacheableNode cn = (CacheableNode) node;

            Element element = getCache().get(cn.getCacheKey());
            String valueToWrite;
            
            if (element != null && element.getObjectValue() != null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Read template from cache - " + cn.getCacheKey());
                }
                valueToWrite = (String) element.getObjectValue();
            } else {
                StringWriter w2 = new StringWriter();
                delegateWriter.writeNode(arguments, w2, cn.getDelegateNode());
                valueToWrite = w2.toString();

                element = new Element(cn.getCacheKey(), valueToWrite);
                getCache().put(element);
            }
            
            writer.write(valueToWrite);
        } else {
            delegateWriter.writeNode(arguments, writer, node);
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
