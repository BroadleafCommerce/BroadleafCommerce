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

/**
 * Wrapper for Thymeleaf's {@link AbstractGeneralTemplateWriter} that provides content caching
 * on the node level.
 * 
 * @author Andre Azzolini (apazzolini), Brian Polster (bpolster)
 */
public class CacheAwareGeneralTemplateWriter extends AbstractGeneralTemplateWriter {

    protected static final Log LOG = LogFactory.getLog(CacheAwareGeneralTemplateWriter.class);

    protected Cache cache;
    protected AbstractGeneralTemplateWriter delegateWriter;

    public CacheAwareGeneralTemplateWriter(AbstractGeneralTemplateWriter delegateWriter) {
        this.delegateWriter = delegateWriter;
    }
    
    @Override
    public void writeNode(final Arguments arguments, final Writer writer, final Node node) 
            throws IOException {
        if (!(node instanceof org.thymeleaf.dom.Element)) {
            super.writeNode(arguments, writer, node);
            return;
        }
        
        org.thymeleaf.dom.Element e = (org.thymeleaf.dom.Element) node;
        
        String cacheKey = (String) e.getNodeProperty("cacheKey");
        
        if (StringUtils.isNotBlank(cacheKey)) {
            String valueToWrite = (String) e.getNodeProperty("blCacheResponse");
            
            if (valueToWrite != null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Read template from cache - " + cacheKey);
                }
            } else {
                Boolean outputParentNode = (Boolean) e.getNodeProperty("blcOutputParentNode");
                StringWriter w2 = new StringWriter();

                if (Boolean.TRUE.equals(outputParentNode)) {
                    super.writeNode(arguments, w2, e);
                } else {
                    final Node[] children = e.unsafeGetChildrenNodeArray();
                    final int childrenLen = e.numChildren();
                    for (int i = 0; i < childrenLen; i++) {
                        super.writeNode(arguments, w2, children[i]);
                    }
                }

                valueToWrite = w2.toString();

                Element element = new Element(cacheKey, valueToWrite);
                getCache().put(element);
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

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public Cache getCache() {
        if (cache == null) {
            cache = CacheManager.getInstance().getCache("blTemplateElements");
        }
        return cache;
    }

}
