/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
    private CacheAwareGeneralTemplateWriter writer;

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
            if (writer == null) {
                writer = new CacheAwareGeneralTemplateWriter((AbstractGeneralTemplateWriter) handler.getTemplateWriter());
            }
            return writer;
        } else {
            return handler.getTemplateWriter();
        }
    }
}


