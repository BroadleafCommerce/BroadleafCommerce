package org.broadleafcommerce.cms.admin.server.service.handler;

import org.broadleafcommerce.cms.page.domain.PageFolder;
import org.broadleafcommerce.cms.page.domain.PageFolderImpl;
import org.broadleafcommerce.cms.page.domain.PageImpl;
import org.broadleafcommerce.cms.page.service.PageService;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/23/11
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class PageServiceProxyFactoryBean implements FactoryBean<PageService> {

    @Override
    public PageService getObject() throws Exception {
        PageServiceInvocationHandler handler = new PageServiceInvocationHandler();
        PageService proxy = (PageService) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{PageService.class}, handler);

        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return PageService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private class PageServiceInvocationHandler implements InvocationHandler {

        private PageFolder rootStructure = new PageFolderImpl();
        private HashMap<Long, PageFolder> library = new HashMap<Long, PageFolder>();

        public PageServiceInvocationHandler() {
            rootStructure.setName("root");
            rootStructure.setId(1L);
            library.put(1L, rootStructure);

            PageFolderImpl first = new PageFolderImpl();
            first.setId(2L);
            first.setName("first");
            first.setParentFolder(rootStructure);
            rootStructure.getSubFolders().add(first);
            library.put(2L, first);

            PageFolderImpl second = new PageFolderImpl();
            second.setId(3L);
            second.setName("second");
            second.setParentFolder(first);
            first.getSubFolders().add(second);
            library.put(3L, second);

            PageImpl secondPageOne = new PageImpl();
            secondPageOne.setId(4L);
            secondPageOne.setName("secondPageOne");
            secondPageOne.setParentFolder(second);
            second.getSubFolders().add(secondPageOne);
            secondPageOne.setMetaKeywords("test");
            library.put(4L, secondPageOne);

            PageImpl secondPageTwo = new PageImpl();
            secondPageTwo.setId(5L);
            secondPageTwo.setName("secondPageTwo");
            secondPageTwo.setParentFolder(second);
            second.getSubFolders().add(secondPageTwo);
            secondPageTwo.setMetaKeywords("test");
            library.put(5L, secondPageTwo);

            PageFolderImpl third = new PageFolderImpl();
            third.setId(6L);
            third.setName("third");
            third.setParentFolder(first);
            first.getSubFolders().add(third);
            library.put(6L, third);

            PageFolderImpl fourth = new PageFolderImpl();
            fourth.setId(7L);
            fourth.setName("fourth");
            fourth.setParentFolder(rootStructure);
            rootStructure.getSubFolders().add(fourth);
            library.put(7L, fourth);

            PageImpl fourthPageOne = new PageImpl();
            fourthPageOne.setId(8L);
            fourthPageOne.setName("fourthPageOne");
            fourthPageOne.setParentFolder(fourth);
            fourth.getSubFolders().add(fourthPageOne);
            fourthPageOne.setMetaKeywords("test");
            library.put(8L, fourthPageOne);
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            if (method.getName().equals("findPageById")) {
                return library.get((Long) objects[0]);
            } else if (method.getName().equals("findPageFolderChildren")) {
                if (objects[1]==null) {
                    return rootStructure.getSubFolders();
                }
                return ((PageFolder) objects[1]).getSubFolders();
            }
            throw new Exception("Operation not supported: " + method.getName());
        }

    }
}
