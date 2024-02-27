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

package org.broadleafcommerce.common.web;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;

import java.util.Map;

/**
 * Placeholder component to support a custom TemplateResolver.
 * 
 * Utilized by the Broadleaf Commerce CustomTemplate extension to introduce themes at the DB level.
 *
 * @author bpolster
 */
public class NullBroadleafTemplateResolver implements ITemplateResolver {

    @Override
    public String getName() {
        return "NullBroadleafTemplateResolver";
    }

    @Override
    public Integer getOrder() {
        return 9999;
    }

    @Override
    public TemplateResolution resolveTemplate(IEngineConfiguration configuration, String ownerTemplate, String template, Map<String, Object> templateResolutionAttributes) {
        return null;
    }
}
