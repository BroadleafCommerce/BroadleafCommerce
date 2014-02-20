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

package org.thymeleaf.dom;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;



public class CacheableNode extends Node {
    
    private static final long serialVersionUID = 8848382034913147363L;

    protected Node delegateNode;
    protected String cacheKey;
    
    public CacheableNode() {
        this(null, null);
    }
    
    public CacheableNode(Node delegateNode, String cacheKey) {
        super(null, null);
        this.delegateNode = delegateNode;
        this.cacheKey = cacheKey;
    }
    
    public Node getDelegateNode() {
        return delegateNode;
    }
    
    public String getCacheKey() {
        return cacheKey;
    }

    @Override
    void doAdditionalSkippableComputing(boolean isSkippable) {
        delegateNode.doAdditionalSkippableComputing(isSkippable);
    }

    @Override
    void doAdditionalProcessableComputing(boolean isProcessable) {
        delegateNode.doAdditionalProcessableComputing(isProcessable);
    }

    @Override
    void doAdditionalPrecomputeNode(Configuration configuration) {
        delegateNode.doAdditionalPrecomputeNode(configuration);
    }

    @Override
    void doAdditionalProcess(Arguments arguments) {
        delegateNode.doAdditionalProcess(arguments);
    }

    @Override
    Node createClonedInstance(NestableNode newParent, boolean cloneProcessors) {
        return delegateNode.createClonedInstance(newParent, cloneProcessors);
    }

    @Override
    void doCloneNodeInternals(Node node, NestableNode newParent, boolean cloneProcessors) {
        delegateNode.doCloneNodeInternals(node, newParent, cloneProcessors);
    }

    @Override
    public void visit(DOMVisitor visitor) {
        delegateNode.visit(visitor);
    }

}
