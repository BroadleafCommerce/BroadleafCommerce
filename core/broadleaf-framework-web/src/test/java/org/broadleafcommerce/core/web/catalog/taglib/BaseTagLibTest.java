/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License” located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License” located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.catalog.taglib;

import junit.framework.TestCase;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.easymock.classextension.EasyMock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

abstract public class BaseTagLibTest extends TestCase {
    
    protected HttpServletRequest request;
    protected PageContext pageContext;
    protected CatalogService catalogService;

    public BaseTagLibTest() {
        pageContext = EasyMock.createMock(PageContext.class);
        request = EasyMock.createMock(HttpServletRequest.class);
        catalogService = EasyMock.createMock(CatalogService.class);
        setup();
    }

    public void replayBaseMockObjects() {
        EasyMock.replay(request, pageContext, catalogService);
    }

    public void replayAdditionalMockObjects(){
        EasyMock.replay(request, pageContext, catalogService);
    }

    public void replayAdditionalMockObjects(Object o){
        EasyMock.replay(request, pageContext, catalogService, o);
    }

    public void replayAdditionalMockObjects(Object o1, Object o2){
        EasyMock.replay(request, pageContext, catalogService, o1, o2);
    }

    public void setPageContext(PageContext pageContext) {
        this.pageContext = pageContext;
    }

    public void verifyBaseMockObjects() {
        EasyMock.verify(request, pageContext, catalogService);
    }

    public void verifyBaseMockObjects(Object o) {
        EasyMock.verify(request, pageContext, catalogService, o);
    }

    public void verifyBaseMockObjects(Object o1, Object o2) {
        EasyMock.verify(request, pageContext, catalogService, o1, o2);
    }
    
    public abstract void setup();
    
}
