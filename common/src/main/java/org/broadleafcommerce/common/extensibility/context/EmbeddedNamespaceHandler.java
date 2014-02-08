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

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Provide the proper parser for the mergeImport element of the custom embedded namespace. See
 * {@code EmbeddedBeanDefinitionParser} for more information.
 *
 * @author Jeff Fischer
 */
public class EmbeddedNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("mergeImport", new EmbeddedBeanDefinitionParser());
    }

}
