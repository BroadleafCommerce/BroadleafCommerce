package org.broadleafcommerce.cms.page.service;

import org.broadleafcommerce.cms.page.dao.PageDao;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageImpl;
import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfiguration;
import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfigurationImpl;
import org.broadleafcommerce.common.sitemap.exception.SiteMapException;
import org.broadleafcommerce.common.sitemap.service.SiteMapGeneratorTest;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapChangeFreqType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapGeneratorType;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapPriorityType;
import org.easymock.EasyMock;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Page site map generator tests
 * 
 * @author Joshua Skorton (jskorton)
 */
public class PageSiteMapGeneratorTest extends SiteMapGeneratorTest {
    
    @Test
    public void testProductSiteMapGenerator() throws SiteMapException, IOException {

        Page p1 = new PageImpl();
        p1.setFullUrl("/about_us");
        Page p2 = new PageImpl();
        p2.setFullUrl("/faq");
        Page p3 = new PageImpl();
        p3.setFullUrl("/new-to-hot-sauce");

        List<Page> pages = new ArrayList<Page>();
        pages.add(p1);
        pages.add(p2);
        pages.add(p3);

        PageDao pageDao = EasyMock.createMock(PageDao.class);
        EasyMock.expect(pageDao.readOnlineAndIncludedPages(5, 0, "fullUrl")).andReturn(pages);
        EasyMock.replay(pageDao);

        PageSiteMapGenerator psmg = new PageSiteMapGenerator();
        psmg.setPageDao(pageDao);
        psmg.setRowLimit(5);

        SiteMapGeneratorConfiguration smgc = new SiteMapGeneratorConfigurationImpl();
        smgc.setDisabled(false);
        smgc.setSiteMapGeneratorType(SiteMapGeneratorType.PAGE);
        smgc.setSiteMapChangeFreq(SiteMapChangeFreqType.HOURLY);
        smgc.setSiteMapPriority(SiteMapPriorityType.POINT5);

        testGenerator(smgc, psmg);

        compareFiles(siteMapService.getTempDirectory() + "sitemap_index.xml", "src/test/resources/org/broadleafcommerce/sitemap/page/sitemap_index.xml");
        compareFiles(siteMapService.getTempDirectory() + "sitemap.xml", "src/test/resources/org/broadleafcommerce/sitemap/page/sitemap.xml");
        compareFiles(siteMapService.getTempDirectory() + "sitemap1.xml", "src/test/resources/org/broadleafcommerce/sitemap/page/sitemap1.xml");
    
    }

}
