/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2020 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.processor;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafTagReplacementProcessor;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.broadleafcommerce.presentation.model.BroadleafTemplateElement;
import org.broadleafcommerce.presentation.model.BroadleafTemplateModel;
import org.broadleafcommerce.presentation.model.BroadleafTemplateNonVoidElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("blGoogleEndTagManagerProcessor")
public class GoogleEndTagManagerProcessor extends AbstractBroadleafTagReplacementProcessor {

    @Autowired
    Environment env;

    @Override
    public String getName() {
        return "google_end_tag_manager";
    }

    @Override
    public int getPrecedence() {
        return 0;
    }

    @Override
    public BroadleafTemplateModel getReplacementModel(String tagName, Map<String, String> tagAttributes, BroadleafTemplateContext context) {
        if (StringUtils.isBlank(getTagManagerAccountId())) {
            return context.createModel();
        }

        StringBuffer sb = new StringBuffer();

        addTagManagerImport(sb);

        return buildScriptTagFromString(context, sb);
    }

    private BroadleafTemplateModel buildScriptTagFromString(BroadleafTemplateContext context, StringBuffer sb) {
        BroadleafTemplateModel model = context.createModel();

        BroadleafTemplateNonVoidElement scriptTag = context.createNonVoidElement("noscript");

        BroadleafTemplateElement script = context.createTextElement(sb.toString());

        scriptTag.addChild(script);
        model.addElement(scriptTag);

        return model;
    }

    private StringBuffer addTagManagerImport(StringBuffer sb) {
        sb.append("<iframe src=\"https://www.googletagmanager.com/ns.html?id=");
        sb.append(getTagManagerAccountId());
        sb.append("\" height=\"0\" width=\"0\" style=\"display:none;visibility:hidden\"></iframe>");

        return sb;
    }

    private String getTagManagerAccountId() {
        return env.getProperty("googleTagManager.accountId");
    }
}
