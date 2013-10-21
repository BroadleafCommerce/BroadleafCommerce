package org.broadleafcommerce.common.file.service;

import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.domain.SiteImpl;
import org.broadleafcommerce.common.web.BroadleafRequestContext;

import java.io.File;

import junit.framework.TestCase;

public class FileSystemFileServiceProviderTest extends TestCase {

    /**
     * For example, if the URL is /product/myproductimage.jpg, then the MD5 would be
     * 35ec52a8dbd8cf3e2c650495001fe55f resulting in the following file on the filesystem
     * {assetFileSystemPath}/64/a7/myproductimage.jpg.
     * 
     * If there is a "siteId" in the BroadleafRequestContext then the site is also distributed
     * using a similar algorithm but the system attempts to keep images for sites in their own
     * directory resulting in an extra two folders required to reach any given product.   So, for
     * site with id 125, the system will MD5 "site125" in order to build the URL string.   "site125" has an md5
     * string of "7d905e85b8cb72a0477632be2c342bd6".    
     * 
     * So, in this case with the above product URL in site125, the full URL on the filesystem
     * will be:
     * 
     * {assetFileSystemPath}/7d/site125/64/a7/myproductimage.jpg.
     * @throws Exception
     */
    public void testBuildFileName() throws Exception {
        FileSystemFileServiceProvider provider = new FileSystemFileServiceProvider();
        provider.fileSystemBaseDirectory = "/test";
        provider.maxGeneratedDirectoryDepth = 2;
        File file = provider.getResource("/product/myproductimage.jpg");
        assertTrue(file.getAbsolutePath().equals("/test/35/ec/myproductimage.jpg"));

        BroadleafRequestContext brc = new BroadleafRequestContext();
        BroadleafRequestContext.setBroadleafRequestContext(brc);

        Site site = new SiteImpl();
        site.setId(125L);
        brc.setSite(site);

        // try with site specific directory
        file = provider.getResource("/product/myproductimage.jpg");
        assertTrue(file.getAbsolutePath().equals("/test/7f/site-125/35/ec/myproductimage.jpg"));

        // without slash
        file = provider.getResource("testwithoutslash");
        assertTrue(file.getAbsolutePath().equals("/test/7f/site-125/e9/90/testwithoutslash"));

        // try with 3 max generated directories
        provider.maxGeneratedDirectoryDepth = 3;
        file = provider.getResource("/product/myproductimage.jpg");
        assertTrue(file.getAbsolutePath().equals("/test/7f/site-125/35/ec/52/myproductimage.jpg"));
    }
}