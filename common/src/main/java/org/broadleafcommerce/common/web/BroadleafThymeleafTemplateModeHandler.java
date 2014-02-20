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
package org.broadleafcommerce.common.web;

import org.thymeleaf.templatemode.ITemplateModeHandler;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templatewriter.AbstractGeneralTemplateWriter;
import org.thymeleaf.templatewriter.CacheAwareGeneralTemplateWriter;
import org.thymeleaf.templatewriter.ITemplateWriter;

/**
 * Overrides the Thymeleaf ContextTemplateResolver and appends the org.broadleafcommerce.common.web.Theme path to the url
 * if it exists.
 */
public class BroadleafThymeleafTemplateModeHandler implements ITemplateModeHandler {

    private ITemplateModeHandler handler;

    public BroadleafThymeleafTemplateModeHandler(ITemplateModeHandler handler) {
        super();
        this.handler = handler;
    }

    public String getTemplateModeName() {
        return handler.getTemplateModeName();
    }

    public ITemplateParser getTemplateParser() {
        return handler.getTemplateParser();
    }

    public ITemplateWriter getTemplateWriter() {
        if (handler.getTemplateWriter() instanceof AbstractGeneralTemplateWriter) {
            return new CacheAwareGeneralTemplateWriter((AbstractGeneralTemplateWriter) handler.getTemplateWriter());
        } else {
            return handler.getTemplateWriter();
        }
    }
}


