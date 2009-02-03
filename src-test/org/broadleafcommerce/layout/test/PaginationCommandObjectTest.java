package org.broadleafcommerce.layout.test;

import java.util.LinkedList;
import java.util.List;

import org.broadleafcommerce.test.integration.BaseTest;
import org.broadleafcommerce.web.PaginationCommandObject;
import org.testng.annotations.Test;

public class PaginationCommandObjectTest extends BaseTest {
    
    @Test(groups =  {"paginationObject"})
    public void testIndices() {
        PaginationCommandObject pco = new PaginationCommandObject();
        pco.setPageNumber(1);
        pco.setPageSize(25);
        pco.setListSize(60);
        assert(pco.getStartIndex() == 25);
        assert(pco.getEndIndex() == 50);
        pco.setPageNumber(2);
        assert(pco.getStartIndex() == 50);
        assert(pco.getEndIndex() == 60);
    }
    
    @Test(groups =  {"paginationObject"})
    public void testDisplayList() {
        PaginationCommandObject pco = new PaginationCommandObject();
        List<Integer> tmp = new LinkedList<Integer>();
        for (int i = 25; i < 50; i++) {
            tmp.add(new Integer(i));
        }
        pco.setDisplayList(tmp);
        pco.setPageSize(25);
        pco.setPageNumber(1);
        Integer index3 = (Integer) pco.getDisplayList().get(3);
        Integer index13 = (Integer) pco.getDisplayList().get(13);
        assert (index3.intValue() == 28);
        assert (index13.intValue() == 38);
    }
    
    @SuppressWarnings("unchecked")
    @Test(groups =  {"paginationObject"})
    public void testFullList() {
        PaginationCommandObject pco = new PaginationCommandObject();
        List<Integer> tmp = new LinkedList<Integer>();
        for (int i = 0; i < 60; i++) {
            tmp.add(new Integer(i));
        }
        pco.setFullList(tmp);
        pco.setPageSize(25);
        pco.setPageNumber(1);
        List<Integer> dl = (List<Integer>) pco.getDisplayList();
        Integer index3 = dl.get(3);
        Integer index13 = dl.get(13);
        assert (dl.size() == 25);
        assert (index3.intValue() == 28);
        assert (index13.intValue() == 38);
        
        pco.setPageNumber(2);
        dl = (List<Integer>) pco.getDisplayList();
        index3 = dl.get(3);
        assert (dl.size() == 10);
        assert (index3.intValue() == 53);
    }
}
