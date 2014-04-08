/*
 * #%L
 * BroadleafCommerce CMS Module
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

package org.broadleafcommerce.cms.url.service;

import org.broadleafcommerce.cms.url.dao.URLHandlerDao;
import org.broadleafcommerce.cms.url.domain.URLHandler;
import org.broadleafcommerce.cms.url.domain.URLHandlerImpl;
import org.broadleafcommerce.cms.url.type.URLRedirectType;
import org.easymock.EasyMock;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * Test URL handling resolution.
 * 
 * @author bpolster
 */
public class URLHandlerServiceTest extends TestCase {
    
    URLHandlerServiceImpl handlerService = new URLHandlerServiceImpl();

    public List<URLHandler> buildUrlHandlerList() {
        List<URLHandler> handlerList = new ArrayList<URLHandler>();

        handlerList.add(createHandler("/simple_url", "/NewSimpleUrl"));
        handlerList.add(createHandler("^/simple_regex$", "/NewSimpleRegex"));
        return handlerList;
    }

    protected URLHandler createHandler(String incomingUrl, String newUrl) {
        URLHandler handler = new URLHandlerImpl();
        handler.setIncomingURL(incomingUrl);
        handler.setNewURL(newUrl);
        handler.setUrlRedirectType(URLRedirectType.REDIRECT_PERM);
        return handler;
    }

    public void setUp() throws Exception {
        handlerService = new URLHandlerServiceImpl();

        URLHandlerDao handlerDao = EasyMock.createMock(URLHandlerDao.class);
        handlerService.urlHandlerDao = handlerDao;
        EasyMock.expect(handlerDao.findAllURLHandlers()).andReturn(buildUrlHandlerList());
        EasyMock.replay(handlerDao);
    }

    @Test
    public void testFoundSimpleUrl() {
        URLHandler h = handlerService.checkForMatches("/simple_url");
        assertTrue(h.getNewURL().equals("/NewSimpleUrl"));
    }

    @Test
    public void testFoundRegExUrl() {
        URLHandler h = handlerService.checkForMatches("/simple_regex");
        assertTrue(h.getNewURL().equals("/NewSimpleRegex"));
    }

    @Test
    public void testForSubPackageBadMatchSimpleUrl() {
        URLHandler h = handlerService.checkForMatches("/simple_url/test");
        assertTrue(h == null);
    }

    @Test
    public void testFoundBadMatchComplexUrl() {
        URLHandler h = handlerService.checkForMatches("/simple_regex/test");
        assertTrue(h == null);
    }

}
