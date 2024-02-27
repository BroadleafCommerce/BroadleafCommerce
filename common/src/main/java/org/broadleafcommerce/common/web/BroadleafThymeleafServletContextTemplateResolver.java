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

import org.broadleafcommerce.common.site.domain.Theme;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.Validate;

import java.util.Map;

/**
 * Overrides the Thymeleaf ContextTemplateResolver and appends the org.broadleafcommerce.common.web.Theme path to the url
 * if it exists.
 */
public class BroadleafThymeleafServletContextTemplateResolver extends SpringResourceTemplateResolver {
    
    protected String templateFolder = "";

    protected ITemplateResource computeTemplateResource(final IEngineConfiguration configuration, final String ownerTemplate,
                                                        final String template, final String resourceName, final String characterEncoding,
                                                        final Map<String, Object> templateResolutionAttributes) {
        String themePath = null;

        Theme theme = BroadleafRequestContext.getBroadleafRequestContext().getTheme();
        if (theme != null && theme.getPath() != null) {
            themePath = theme.getPath();
        }



        Validate.notNull(template, "Template name cannot be null");

        String unaliasedName = this.getTemplateAliases().get(template);
        if (unaliasedName == null) {
            unaliasedName = resourceName;
        }

        final StringBuilder resourceNameStr = new StringBuilder();
        String prefix = this.getPrefix();
        if (prefix != null && ! prefix.trim().equals("")) {

            if (themePath != null) {
                resourceNameStr.append(prefix).append(themePath).append('/').append(templateFolder);
            }
        }
        resourceNameStr.append(unaliasedName);
        String suffix = this.getSuffix();
        if (suffix != null && ! suffix.trim().equals("")) {
            resourceNameStr.append(suffix);
        }
        return super.computeTemplateResource(configuration, ownerTemplate, template, resourceNameStr.toString(), characterEncoding, templateResolutionAttributes);
    }

    public String getTemplateFolder() {
        return templateFolder;
    }

    public void setTemplateFolder(String templateFolder) {
        this.templateFolder = templateFolder;
    }
    
}


