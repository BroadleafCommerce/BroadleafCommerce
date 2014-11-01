/*
 * #%L
 * broadleaf-theme
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
package org.broadleafcommerce.core.web.resolver;

import org.thymeleaf.templateresolver.TemplateResolver;

/**
 * This {@link TemplateResolver} serves as a placeholder class that can be used to inject 
 * a {@link DatabaseResourceResolver}. It doesn't need to actually override any methods from
 * TemplateResolver.
 * 
 * The injection happens in XML configuration.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class DatabaseTemplateResolver extends TemplateResolver {
    
}
