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
package org.broadleafcommerce.cms.file.service;

import org.broadleafcommerce.cms.field.type.StorageType;
import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.file.domain.StaticAssetImpl;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.domain.SiteImpl;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import junit.framework.TestCase;

/**
 * Created by bpolster.
 */
public class StaticAssetStorageServiceImplTest extends TestCase {
    
    /**
     *      * For example, if the URL is /product/myproductimage.jpg, then the MD5 would be
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
    public void testGenerateStorageFileName() throws Exception {
        StaticAssetStorageServiceImpl staticAssetStorageService = new StaticAssetStorageServiceImpl();
        staticAssetStorageService.assetFileSystemPath = "/test";
        staticAssetStorageService.assetServerMaxGeneratedDirectories = 2;
        String fileName = staticAssetStorageService.generateStorageFileName("/product/myproductimage.jpg", false);
        assertTrue(fileName.equals("/test/35/ec/myproductimage.jpg"));

        BroadleafRequestContext brc = new BroadleafRequestContext();
        BroadleafRequestContext.setBroadleafRequestContext(brc);
        
        Site site = new SiteImpl();
        site.setId(125L);
        brc.setSite(site);

        // try with site specific directory
        fileName = staticAssetStorageService.generateStorageFileName("/product/myproductimage.jpg", false);
        assertTrue(fileName.equals("/test/7f/site-125/35/ec/myproductimage.jpg"));

        // try with 3 max generated directories
        staticAssetStorageService.assetServerMaxGeneratedDirectories = 3;
        fileName = staticAssetStorageService.generateStorageFileName("/product/myproductimage.jpg", false);
        assertTrue(fileName.equals("/test/7f/site-125/35/ec/52/myproductimage.jpg"));
        
        staticAssetStorageService.assetServerMaxGeneratedDirectories = 2;
        fileName = staticAssetStorageService.generateStorageFileName("testwithoutslash", false);
        assertTrue(fileName.equals("/test/7f/site-125/e9/90/testwithoutslash"));
    }

    /**
     * Will throw an exception because the string being uploaded is too long.
     * @throws Exception
     */
    public void testUploadFileThatIsTooLarge() throws Exception {
        StaticAssetStorageServiceImpl staticAssetStorageService = new StaticAssetStorageServiceImpl();
        staticAssetStorageService.assetFileSystemPath = System.getProperty("java.io.tmpdir");
        staticAssetStorageService.assetServerMaxGeneratedDirectories = 2;

        String str = "This string is too long";
        staticAssetStorageService.maxUploadableFileSize = str.length() - 1;

        // convert String into InputStream
        InputStream is = new ByteArrayInputStream(str.getBytes());
        MockMultipartFile mpf = new MockMultipartFile("Test File", is);

        StaticAsset staticAsset = new StaticAssetImpl();
        staticAsset.setFileExtension(".jpg");
        staticAsset.setFullUrl("/product/myproduct.jpg");
        staticAsset.setStorageType(StorageType.FILESYSTEM);

        // Remember this, we may need to delete this file.
        String fileName = staticAssetStorageService.generateStorageFileName(staticAsset, false);

        boolean exceptionThrown = false;
        try {
            staticAssetStorageService.createStaticAssetStorageFromFile(mpf, staticAsset);
        } catch (Exception e) {
            exceptionThrown = true;
        }
        assertTrue("Service call threw an exception", exceptionThrown);

        File f = new File(staticAssetStorageService.assetFileSystemPath + fileName);
        if (f.exists()) {
            f.delete();
        }
    }

    /**
     * Tests uploading a file that is an allowed size.
     * @throws Exception
     */
    public void testUploadFileThatIsAllowedSize() throws Exception {
        StaticAssetStorageServiceImpl staticAssetStorageService = new StaticAssetStorageServiceImpl();
        staticAssetStorageService.assetFileSystemPath = System.getProperty("java.io.tmpdir");
        staticAssetStorageService.assetServerMaxGeneratedDirectories = 2;

        String str = "This string is not too long.";
        staticAssetStorageService.maxUploadableFileSize = str.length();

        // convert String into InputStream
        InputStream is = new ByteArrayInputStream(str.getBytes());
        MockMultipartFile mpf = new MockMultipartFile("Test File", is);

        StaticAsset staticAsset = new StaticAssetImpl();
        staticAsset.setFileExtension(".jpg");
        staticAsset.setFullUrl("/product/myproduct.jpg");
        staticAsset.setStorageType(StorageType.FILESYSTEM);

        // Remember this, we may need to delete this file.
        String fileName = staticAssetStorageService.generateStorageFileName(staticAsset, false);

        boolean exceptionThrown = false;
        try {
            staticAssetStorageService.createStaticAssetStorageFromFile(mpf, staticAsset);
        } catch (Exception e) {
            exceptionThrown = true;
        }
        assertFalse("Service call threw an exception", exceptionThrown);

        File f = new File(fileName);
        if (f.exists()) {
            f.delete();
        }
    }
}
