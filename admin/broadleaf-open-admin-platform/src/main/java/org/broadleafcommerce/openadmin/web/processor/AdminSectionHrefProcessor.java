/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.web.processor;

import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.service.AdminNavigationService;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.spring3.context.SpringWebContext;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import javax.servlet.http.HttpServletRequest;

/**
 * A Thymeleaf processor that will generate the HREF of a given Admin Section.
 * This is useful in constructing the left navigation menu for the admin console.
 *
 * @author elbertbautista
 */
public class AdminSectionHrefProcessor extends AbstractModelVariableModifierProcessor {

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public AdminSectionHrefProcessor() {
        super("admin_section_href");
    }

    @Override
    public int getPrecedence() {
        return 10002;
    }

    @Override
    protected void modifyModelAttributes(Arguments arguments, Element element) {
        String href = "#";
        String resultVar = element.getAttributeValue("resultVar");
        String sectionStr = element.getAttributeValue("section");

        AdminSection section = (AdminSection) StandardExpressionProcessor.processExpression(arguments, sectionStr);
        if (section == null) {
            addToModel(arguments, resultVar, href);
            return;
        }

        HttpServletRequest request = ((SpringWebContext) arguments.getContext()).getHttpServletRequest();
        String context = request.getContextPath();
        String gwtDebug = "";

        if (request.getParameter("gwt.codesvr") != null ) {
            gwtDebug = "?gwt.codesvr=" + request.getParameter("gwt.codesvr");
        }

        href = context + section.getUrl() + gwtDebug + "#moduleKey=" + section.getModule().getModuleKey() + "&pageKey=" + section.getSectionKey();
        addToModel(arguments, resultVar, href);
    }

}
