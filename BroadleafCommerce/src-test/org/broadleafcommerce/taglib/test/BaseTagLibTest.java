package org.broadleafcommerce.taglib.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import junit.framework.TestCase;

import org.broadleafcommerce.catalog.service.CatalogService;
import org.easymock.classextension.EasyMock;

abstract public class BaseTagLibTest extends TestCase {
    protected HttpServletRequest request;
    protected PageContext pageContext;
    protected CatalogService catalogService;

    public BaseTagLibTest() {
        pageContext = EasyMock.createMock(PageContext.class);
        request = EasyMock.createStrictMock(HttpServletRequest.class);
        catalogService = EasyMock.createMock(CatalogService.class);
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
}
