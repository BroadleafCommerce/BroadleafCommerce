/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.service.PageService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Based on the passed in request, this processor determines if a CMS managed page exists
 * that matches the passed in URL.
 *
 * The {@code ProcessURLFilter} will check it's internal cache to determine which URL processor
 * should be invoked for a passed in URL.  If it is unable to find a matching processor in cache,
 * then it will call each processor in turn to provide an attempt to process the URL.
 *
 * Created by bpolster.
 */
@Component("blPageURLProcessor")
public class PageURLProcessor implements URLProcessor {

    private static final Log LOG = LogFactory.getLog(PageURLProcessor.class);


    @Resource(name = "blPageService")
    private PageService pageService;
    private String blcPageTemplateDirectory ="/WEB-INF/jsp/templates";
    private static final String PAGE_ATTRIBUTE_NAME = "BLC_PAGE";

    /**
     * Implementors of this interface will return true if they are able to process the
     * current in request.
     *
     * Implementors of this method will need to rely on the BroadleafRequestContext class
     * which provides access to the current sandbox, locale, request, and response via a
     * threadlocal context
     *
     * @see BroadleafRequestContext
     *
     * @return true if this URLProcessor is able to process the passed in request
     */
    @Override
    public boolean canProcessURL(String key) {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        Page p = pageService.findPageByURI(context.getSandbox(), context.getLocale(), key);
        context.getRequest().setAttribute(PAGE_ATTRIBUTE_NAME, p);
        return (p != null);
    }

    /**
     * Determines if the requestURI for the passed in request matches a custom content
     * managed page.   If so, the request is forwarded to the correct page template.
     *
     * The page object will be stored in the request attribute "BLC_PAGE".
     *
     * @param key The URI to process
     *
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    public void processURL(String key) throws IOException, ServletException {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        Page p = (Page) context.getRequest().getAttribute(PAGE_ATTRIBUTE_NAME);
        if (p == null) {
            p = pageService.findPageByURI(context.getSandbox(), context.getLocale(), key);
        }

        if (p != null) {
            String templateJSPPath = new StringBuilder(blcPageTemplateDirectory).append(p.getPageTemplate().getTemplatePath()).append(".jsp").toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Forwarding to page: " + templateJSPPath);
            }
            context.getRequest().setAttribute(PAGE_ATTRIBUTE_NAME, p);
            RequestDispatcher rd = context.getRequest().getRequestDispatcher(templateJSPPath);
            rd.forward(context.getRequest(), context.getResponse());
        }
    }

    /**
     * Defaults to /WEB-INF/jsp/templates but can be overridden via Spring configuration.
     * @return
     */
    public String getBlcPageTemplateDirectory() {
        return blcPageTemplateDirectory;
    }

    /**
     * Sets the directory where JSP page templates are stored.   If this method is
     * not called, the default value of "/WEB-INF/jsp/templates" is used.
     */
    public void setBlcPageTemplateDirectory(String blcPageTemplateDirectory) {
        this.blcPageTemplateDirectory = blcPageTemplateDirectory;
    }
}
