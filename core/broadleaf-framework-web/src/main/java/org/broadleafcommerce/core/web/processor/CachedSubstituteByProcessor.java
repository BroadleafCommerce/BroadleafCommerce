/*
 * #%L
 * BroadleafCommerce Framework Web
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

package org.broadleafcommerce.core.web.processor;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.fragment.WholeFragmentSpec;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.element.AbstractElementProcessor;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.fragment.StandardFragment;
import org.thymeleaf.standard.fragment.StandardFragmentProcessor;
import org.thymeleaf.standard.processor.attr.AbstractStandardFragmentHandlingAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardFragmentAttrProcessor;

import java.util.List;

/**
 * @author Andre Azzolini (apazzolini), bpolster
 */
public class CachedSubstituteByProcessor extends AbstractElementProcessor {

    public static final int ATTR_PRECEDENCE = 100;
    public static final String ATTR_NAME = "substituteby";

    protected Cache cache;
    protected ITemplateCacheKeyResolver cacheKeyResolver = new SimpleCacheKeyResolver();

    public CachedSubstituteByProcessor() {
        super(ATTR_NAME);
    }

    @Override
    public final ProcessorResult processElement(final Arguments arguments, final Element element) {
        String template = element.getAttributeValue("template");

        if (checkCacheForElement(arguments, element, template)) {
            // This template has been cached.
            element.clearChildren();
        } else {
            final List<Node> fragmentNodes = computeFragment(arguments, element, "template", template);
            element.clearChildren();
            element.setChildren(fragmentNodes);
        }
        return ProcessorResult.OK;
    }

    /**
     * If this template was found in cache, adds the response to the element and returns true.
     * 
     * If not found in cache, adds the cacheKey to the element so that the Writer can cache after the
     * first process.
     * 
     * @param arguments
     * @param element
     * @param template
     * @return
     */
    protected boolean checkCacheForElement(Arguments arguments, Element element, String template) {
        String cacheKeyParam = "";
        String cacheKeyAttrValue = element.getAttributeValueFromNormalizedName("cachekey");

        if (cacheKeyAttrValue != null) {
            Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                    .parseExpression(arguments.getConfiguration(), arguments, cacheKeyAttrValue);
            cacheKeyParam = (String) expression.execute(arguments.getConfiguration(), arguments);
        }

        String cacheKey = cacheKeyResolver.resolveCacheKey(template, cacheKeyParam);
        element.setAttribute("cachekey", cacheKey);

        net.sf.ehcache.Element cacheElement = getCache().get(cacheKey);
        if (cacheElement != null && !cacheElement.isExpired()) {
            element.setAttribute("blcacheresponse", (String) cacheElement.getObjectValue());
            return true;
        } else {
            return false;
        }
    }


    /**
     * <b>NOTE</b> This method is copied from {@link AbstractStandardFragmentHandlingAttrProcessor#computeFragment}
     * 
     * @param arguments
     * @param element
     * @param attributeName
     * @param attributeValue
     * @return
     */
    protected final List<Node> computeFragment(final Arguments arguments, final Element element,
            final String attributeName, final String attributeValue) {
        final String dialectPrefix = Attribute.getPrefixFromAttributeName(attributeName);

        final String fragmentSignatureAttributeName =
                getFragmentSignatureUnprefixedAttributeName(arguments, element, attributeName, attributeValue);

        final StandardFragment fragment =
                StandardFragmentProcessor.computeStandardFragmentSpec(
                        arguments.getConfiguration(), arguments, attributeValue, dialectPrefix, fragmentSignatureAttributeName);

        final List<Node> extractedNodes =
                fragment.extractFragment(arguments.getConfiguration(), arguments, arguments.getTemplateRepository());

        final boolean removeHostNode = getRemoveHostNode(arguments, element);

        // If fragment is a whole document (no selection inside), we should never remove its parent node/s
        // Besides, we know that StandardFragmentProcessor.computeStandardFragmentSpec only creates two types of
        // IFragmentSpec objects: WholeFragmentSpec and DOMSelectorFragmentSpec.
        final boolean isWholeDocument = (fragment.getFragmentSpec() instanceof WholeFragmentSpec);

        if (extractedNodes == null || removeHostNode || isWholeDocument) {
            return extractedNodes;
        }

        // Host node is NOT to be removed, therefore what should be removed is the top-level elements of the returned
        // nodes.

        final Element containerElement = new Element("container");

        for (final Node extractedNode : extractedNodes) {
            // This is done in this indirect way in order to preserver internal structures like e.g. local variables.
            containerElement.addChild(extractedNode);
            containerElement.extractChild(extractedNode);
        }

        final List<Node> extractedChildren = containerElement.getChildren();
        containerElement.clearChildren();

        return extractedChildren;
    }

    protected String getFragmentSignatureUnprefixedAttributeName(final Arguments arguments, final Element element,
            final String attributeName, final String attributeValue) {
        return StandardFragmentAttrProcessor.ATTR_NAME;
    }

    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }

    protected boolean getRemoveHostNode(Arguments arguments, Element element) {
        return false;
    }

    public ITemplateCacheKeyResolver getCacheKeyResolver() {
        return cacheKeyResolver;
    }

    public void setCacheKeyResolver(ITemplateCacheKeyResolver cacheKeyResolver) {
        this.cacheKeyResolver = cacheKeyResolver;
    }

    public Cache getCache() {
        if (cache == null) {
            cache = CacheManager.getInstance().getCache("blTemplateElements");
        }
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }
}
