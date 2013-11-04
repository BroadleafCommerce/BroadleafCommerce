package org.broadleafcommerce.core.catalog.service;

import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfiguration;
import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfigurationImpl;
import org.broadleafcommerce.common.sitemap.exception.SiteMapException;
import org.broadleafcommerce.common.sitemap.service.SiteMapGeneratorTest;
import org.broadleafcommerce.common.sitemap.service.type.SiteMapGeneratorType;
import org.broadleafcommerce.core.catalog.dao.ProductDao;
import org.easymock.classextension.EasyMock;

import java.io.IOException;

/**
 * Product site map generator tests
 * 
 * @author Joshua Skorton (jskorton)
 */
public class ProductSiteMapGeneratorTest extends SiteMapGeneratorTest {

    //@Test
    public void testProductSiteMapGenerator() throws SiteMapException, IOException {

        SiteMapGeneratorConfiguration smgc = new SiteMapGeneratorConfigurationImpl();
        smgc.setDisabled(false);
        smgc.setSiteMapGeneratorType(SiteMapGeneratorType.PRODUCT);

        ProductDao pd = EasyMock.createMock(ProductDao.class);
        //EasyMock.expect(mcs.findActiveConfigurationsByType(ModuleConfigurationType.SITE_MAP)).andReturn(configurations);
        EasyMock.replay(pd);

        ProductSiteMapGenerator psmg = new ProductSiteMapGenerator();
        testGenerator(smgc, psmg);

        compareFiles(siteMapService.getTempDirectory() + "sitemap_index.xml", "src/test/resources/org/broadleafcommerce/sitemap/product/sitemap_index.xml");
        compareFiles(siteMapService.getTempDirectory() + "sitemap.xml", "src/test/resources/org/broadleafcommerce/sitemap/product/sitemap.xml");
        compareFiles(siteMapService.getTempDirectory() + "sitemap1.xml", "src/test/resources/org/broadleafcommerce/sitemap/product/sitemap1.xml");

    }

}

//INSERT INTO BLC_SITE_MAP_GEN_CFG (GEN_CONFIG_ID,MODULE_CONFIG_ID,DISABLED,CHANGE_FREQ,GENERATOR_TYPE,PRIORITY) VALUES (-2,-1,TRUE,'HOURLY','PRODUCT','0.5');
//INSERT INTO BLC_SITE_MAP_GEN_CFG (GEN_CONFIG_ID,MODULE_CONFIG_ID,DISABLED,CHANGE_FREQ,GENERATOR_TYPE,PRIORITY) VALUES (-3,-1,TRUE,'HOURLY','PAGE','0.5');
//INSERT INTO BLC_SITE_MAP_GEN_CFG (GEN_CONFIG_ID,MODULE_CONFIG_ID,DISABLED,CHANGE_FREQ,GENERATOR_TYPE,PRIORITY) VALUES (-4,-1,TRUE,'HOURLY','CATEGORY','0.5');
//INSERT INTO BLC_CAT_SITE_MAP_GEN_CFG (GEN_CONFIG_ID,ROOT_CATEGORY_ID,STARTING_DEPTH,ENDING_DEPTH) VALUES (-4,2,1,1);
//INSERT INTO BLC_SITE_MAP_GEN_CFG (GEN_CONFIG_ID,MODULE_CONFIG_ID,DISABLED,CHANGE_FREQ,GENERATOR_TYPE,PRIORITY) VALUES (-5,-1,FALSE,'HOURLY','STRUCTURED_CONTENT','0.5');
//INSERT INTO BLC_ADV_SC_SITE_MAP_GEN_CFG (GEN_CONFIG_ID,TYPE_CATEGORY) VALUES (-5,'all');