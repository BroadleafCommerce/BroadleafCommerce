/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.file.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
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
        String tmpdir = FileUtils.getTempDirectoryPath();
        if (!tmpdir.endsWith(File.separator)) {
            tmpdir = tmpdir + File.separator;
        }
        provider.fileSystemBaseDirectory = FilenameUtils.concat(tmpdir, "test");
        provider.maxGeneratedDirectoryDepth = 2;
        File file = provider.getResource("/product/myproductimage.jpg");
        
        String resultPath = tmpdir + StringUtils.join(new String[] {"test", "35", "ec", "myproductimage.jpg"}, File.separator);
        assertEquals(file.getAbsolutePath(), FilenameUtils.normalize(resultPath));

        BroadleafRequestContext brc = new BroadleafRequestContext();
        BroadleafRequestContext.setBroadleafRequestContext(brc);

        Site site = new SiteImpl();
        site.setId(125L);
        brc.setSite(site);

        // try with site specific directory
        file = provider.getResource("/product/myproductimage.jpg");
        resultPath = tmpdir + StringUtils.join(new String[] {"test", "c8", "site-125", "35", "ec", "myproductimage.jpg"}, File.separator);
        assertEquals(file.getAbsolutePath(), resultPath);

        // try with 3 max generated directories
        provider.maxGeneratedDirectoryDepth = 3;
        file = provider.getResource("/product/myproductimage.jpg");
        resultPath = tmpdir + StringUtils.join(new String[] {"test", "c8", "site-125", "35", "ec", "52", "myproductimage.jpg"}, File.separator);
        assertEquals(file.getAbsolutePath(), resultPath);
        
        // Remove the request context from thread local so it doesn't get in the way of subsequent tests
        BroadleafRequestContext.setBroadleafRequestContext(null);
    }
    
}