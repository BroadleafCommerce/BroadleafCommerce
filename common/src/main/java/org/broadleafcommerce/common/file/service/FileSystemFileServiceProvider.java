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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.file.FileServiceException;
import org.broadleafcommerce.common.file.domain.FileWorkArea;
import org.broadleafcommerce.common.file.service.type.FileApplicationType;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Default implementation of FileServiceProvider that uses the local file system to store files created by Broadleaf
 * components.
 * 
 * This Provider can only be used in production systems that run on a single server or those that have a shared filesystem
 * mounted to the application servers.
 * 
 * @author bpolster
 *
 */
@Service("blDefaultFileServiceProvider")
public class FileSystemFileServiceProvider implements FileServiceProvider {

    @Value("${asset.server.file.system.path}")
    protected String fileSystemBaseDirectory;

    @Value("${asset.server.max.generated.file.system.directories}")
    protected int maxGeneratedDirectoryDepth;

    private static final String DEFAULT_STORAGE_DIRECTORY = System.getProperty("java.io.tmpdir");

    private static final Log LOG = LogFactory.getLog(FileSystemFileServiceProvider.class);

    // Allows for small errors in the configuration (e.g. no trailing slash or whitespace).
    protected String baseDirectory;

    @Override
    public File getResource(String url) {
        return getResource(url, FileApplicationType.ALL);
    }

    @Override
    public File getResource(String url, FileApplicationType applicationType) {
        String fileName = buildResourceName(url);
        String filePath = FilenameUtils.normalize(getBaseDirectory() + File.separator + fileName);
        return new File(filePath);
    }

    @Override
    public void addOrUpdateResources(FileWorkArea area, List<File> files, boolean removeResourcesFromWorkArea) {
        for (File srcFile : files) {
            if (!srcFile.getAbsolutePath().startsWith(area.getFilePathLocation())) {
                throw new FileServiceException("Attempt to update file " + srcFile.getAbsolutePath() +
                        " that is not in the passed in WorkArea " + area.getFilePathLocation());
            }

            String fileName = srcFile.getAbsolutePath().substring(area.getFilePathLocation().length());
            
            // before building the resource name, convert the file path to a url-like path
            String url = FilenameUtils.separatorsToUnix(fileName);
            String resourceName = buildResourceName(url);
            String destinationFilePath = FilenameUtils.normalize(getBaseDirectory() + File.separator + resourceName);
            File destFile = new File(destinationFilePath);
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            
            try {
                if (removeResourcesFromWorkArea) {
                    if (destFile.exists()) {
                        FileUtils.deleteQuietly(destFile);
                    }
                    FileUtils.moveFile(srcFile, destFile);
                } else {
                    FileUtils.copyFile(srcFile, destFile);
                }
            } catch (IOException ioe) {
                throw new FileServiceException("Error copying resource named " + fileName + " from workArea " +
                        area.getFilePathLocation() + " to " + resourceName, ioe);
            }
        }
    }

    @Override
    public boolean removeResource(String name) {
        String resourceName = buildResourceName(name);
        String filePathToRemove = FilenameUtils.normalize(getBaseDirectory() + File.separator + resourceName);
        File fileToRemove = new File(filePathToRemove);
        return fileToRemove.delete();
    }

    /**
     * Stores the file on the file-system by performing an MD5 hash of the 
     * the passed in fileName
     * 
     * To ensure that files can be stored and accessed in an efficient manner, the 
     * system creates directories based on the characters in the hash.   
     * 
     * For example, if the URL is /product/myproductimage.jpg, then the MD5 would be
     * 35ec52a8dbd8cf3e2c650495001fe55f resulting in the following file on the filesystem
     * {assetFileSystemPath}/35/ec/myproductimage.jpg.  
     * 
     * The hash for the filename will include a beginning slash before performing the MD5.   This
     * is done largely for backward compatibility with similar functionality in BLC 3.0.0.
     * 
     * This algorithm has the following benefits:
     * - Efficient file-system storage with
     * - Balanced tree of files that supports 10 million files
     * 
     * If support for more files is needed, implementors should consider one of the following approaches:
     * 1.  Overriding the maxGeneratedFileSystemDirectories property from its default of 2 to 3
     * 2.  Overriding this method to introduce an alternate approach
     * 
     * @param url The URL used to represent an asset for which a name on the fileSystem is desired.
     * 
     * @return
     */
    protected String buildResourceName(String url) {
        // Create directories based on hash
        String fileHash = null;
        // Intentionally not using File.separator here since URLs should always end with /
        if (!url.startsWith("/")) {
            fileHash = DigestUtils.md5Hex("/" + url);
        } else {
            fileHash = DigestUtils.md5Hex(url);
        }

        String resourceName = "";
        for (int i = 0; i < maxGeneratedDirectoryDepth; i++) {
            if (i == 4) {
                LOG.warn("Property maxGeneratedDirectoryDepth set to high, ignoring values past 4 - value set to" +
                        maxGeneratedDirectoryDepth);
                break;
            }
            resourceName = FilenameUtils.concat(resourceName, fileHash.substring(i * 2, (i + 1) * 2));
        }

        // use the filename from the URL which is everything after the last slash
        return FilenameUtils.concat(resourceName, FilenameUtils.getName(url));
    }

    /**
     * Returns a base directory (unique for each tenant in a multi-tenant installation.
     * Creates the directory if it does not already exist.
     */
    protected String getBaseDirectory() {
        if (baseDirectory == null) {
            if (StringUtils.isNotBlank(fileSystemBaseDirectory)) {
                baseDirectory = fileSystemBaseDirectory;
            } else {
                baseDirectory = DEFAULT_STORAGE_DIRECTORY;
            }
        }

        return getSiteDirectory(baseDirectory);
    }

    /**
     * Creates a unique directory on the file system for each site.
     * Each site may be in one of 255 base directories.   This model efficiently supports up to 65,000 sites
     * served from a single file system based on most OS systems ability to quickly access files as long
     * as there are not more than 255 directories.
     * 
     * @param The starting directory for local files which must end with a '/';
     */
    protected String getSiteDirectory(String baseDirectory) {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc != null) {
            Site site = brc.getSite();
            if (site != null) {
                String siteDirectory = "site-" + site.getId();
                String siteHash = DigestUtils.md5Hex(siteDirectory);
                String sitePath = FilenameUtils.concat(siteHash.substring(0, 2), siteDirectory);
                return FilenameUtils.concat(baseDirectory, sitePath);
            }
        }

        return baseDirectory;
    }
}
