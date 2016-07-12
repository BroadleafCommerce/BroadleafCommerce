/*
 * #%L
 * BroadleafCommerce Framework Web
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

package org.broadleafcommerce.core.web.processor;

import org.broadleafcommerce.common.web.dialect.AbstractBroadleafModelVariableModifierProcessor;
import org.broadleafcommerce.common.web.domain.BroadleafThymeleafContext;
import org.broadleafcommerce.core.web.processor.extension.HeadProcessorExtensionListener;
import org.springframework.stereotype.Component;

import java.util.Map;

import javax.annotation.Resource;

/**
 * A Thymeleaf processor that will include the standard head element. It will also set the
 * following variables for use by the head fragment.
 * 
 * <ul>
 *  <li><b>pageTitle</b> - The title of the page</li>
 *  <li><b>additionalCss</b> - An additional, page specific CSS file to include</li>
 *  <li><b>metaDescription</b> - Optional, Content for the Meta-Description tag</li>
 *  <li><b>metaKeywords</b> - Optional, Content for the Meta-Keywords tag</li>
 *  <li><b>metaRobot</b> - Optional, Content for the Meta-Robots tag</li>
 * </ul>
 * 
 * @author apazzolini
 *
 * @deprecated
 *
 * The entire FragmentAndTarget class has been deprecated in favor of a completely new system in Thymeleaf 2.1
 * The referenced issue can be found at https://github.com/thymeleaf/thymeleaf/issues/205
 *
 * Use th:include or th:replace within the head tag and include the variables to replicate the behaviour.
 *
 * Examples:
 *
 * <head th:include="/layout/partials/head (pageTitle='My Page Title')"></head>
 * <head th:include="/layout/partials/head (twovar=${value2},onevar=${value1})">...</head>
 *
 */
@Deprecated
@Component("blHeadProcessor")
public class HeadProcessor extends AbstractBroadleafModelVariableModifierProcessor {

    @Resource(name = "blHeadProcessorExtensionManager")
    protected HeadProcessorExtensionListener extensionManager;

    protected String HEAD_PARTIAL_PATH = "layout/partials/head";

    @Override
    public String getName() {
        return "head";
    }

    @Override
    public void populateModelVariables(String tagName, Map<String, String> tagAttributes, Map<String, Object> newModelVars, BroadleafThymeleafContext context) {
        // The pageTitle attribute could be an expression that needs to be evaluated. Try to evaluate, but fall back
        // to its text value if the expression wasn't able to be processed. This will allow things like
        // pageTitle="Hello this is a string"
        // as well as expressions like
        // pageTitle="${'Hello this is a ' + product.name}"

        String pageTitle = tagAttributes.get("pageTitle");
        pageTitle = (String) context.parseExpression(pageTitle);
        newModelVars.put("pageTitle", pageTitle);
        newModelVars.put("additionalCss", tagAttributes.get("additionalCss"));

        extensionManager.processAttributeValues(tagName, tagAttributes, newModelVars, context);

        //the commit at https://github.com/thymeleaf/thymeleaf/commit/b214d9b5660369c41538e023d4b8d7223ebcbc22 along with
        //the referenced issue at https://github.com/thymeleaf/thymeleaf/issues/205

        //        return new FragmentAndTarget(HEAD_PARTIAL_PATH, WholeFragmentSpec.INSTANCE);
    }

}
