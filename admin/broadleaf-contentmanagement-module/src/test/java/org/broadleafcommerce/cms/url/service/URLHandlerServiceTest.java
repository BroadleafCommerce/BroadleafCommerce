/*-
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

    public List<URLHandler> buildAllUrlHandlerList() {
        List<URLHandler> handlerList = new ArrayList<URLHandler>();
        handlerList.add(createHandler("/simple_url", "/NewSimpleUrl", false));
        handlerList.addAll(buildRegExUrlHandlerList());
        return handlerList;
    }

    public List<URLHandler> buildRegExUrlHandlerList() {
        List<URLHandler> handlerList = new ArrayList<URLHandler>();

        handlerList.add(createHandler("^/simple_regex$", "/NewSimpleRegex", true));
        handlerList.add(createHandler("/blogs/(.*)/(.*)$", "/newblogs/$2/$1", true));
        handlerList.add(createHandler("(.*)/shirts-tops(.*)", "$1/shirts$2", true));
        return handlerList;
    }

    protected URLHandler createHandler(String incomingUrl, String newUrl, Boolean isRegEx) {
        URLHandler handler = new URLHandlerImpl();
        handler.setIncomingURL(incomingUrl);
        handler.setNewURL(newUrl);
        handler.setUrlRedirectType(URLRedirectType.REDIRECT_PERM);
        handler.setRegexHandler(isRegEx);
        return handler;
    }

    public void setUp() throws Exception {
        handlerService = new URLHandlerServiceImpl();

        URLHandlerDao handlerDao = EasyMock.createMock(URLHandlerDao.class);
        handlerService.urlHandlerDao = handlerDao;
        EasyMock.expect(handlerDao.findAllURLHandlers()).andReturn(buildAllUrlHandlerList());
        EasyMock.expect(handlerDao.findAllRegexURLHandlers()).andReturn(buildRegExUrlHandlerList());
        EasyMock.replay(handlerDao);
    }

    //checkForMatches is the RegEx test.  A non-regex URLHandler should not be found
    @Test
    public void testNotFoundSimpleUrlWithCheckForMatches() {
        URLHandler h = handlerService.checkForMatches("/simple_url");
        assertTrue(h == null);
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

    @Test
    public void testRegEx() {
        URLHandler h = handlerService.checkForMatches("/blogs/first/second");
        assertTrue(h != null);
        assertTrue(h.getNewURL().equals("/newblogs/second/first"));
    }

    @Test
    public void testRegExStartsWithSpecialRegExChar() {
        URLHandler h = handlerService.checkForMatches("/merchandise/shirts-tops/mens");
        String expectedNewURL = "/merchandise/shirts/mens";
        assertTrue(h != null);
        assertTrue(expectedNewURL.equals(h.getNewURL()));
    }

}
