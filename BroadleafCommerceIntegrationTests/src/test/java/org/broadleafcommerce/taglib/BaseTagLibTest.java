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
package org.broadleafcommerce.taglib.test;

import javax.servlet.jsp.PageContext;

import junit.framework.TestCase;

import org.broadleafcommerce.catalog.service.CatalogService;
import org.easymock.classextension.EasyMock;
import org.springframework.mock.web.MockHttpServletRequest;

abstract public class BaseTagLibTest extends TestCase {
    protected MockHttpServletRequest request;
    protected PageContext pageContext;
    protected CatalogService catalogService;

    public BaseTagLibTest() {
        pageContext = EasyMock.createMock(PageContext.class);
        request = new MockHttpServletRequest();
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
