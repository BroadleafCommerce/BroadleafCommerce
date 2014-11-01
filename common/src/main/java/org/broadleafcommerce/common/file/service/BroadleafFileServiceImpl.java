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
import org.broadleafcommerce.common.sitemap.service.SiteMapGenerator;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;
/**
 * Many components in the Broadleaf Framework can benefit from creating and manipulating temporary files as well
 * as storing and accessing files in a remote repository (such as AmazonS3).
 * 
 * This service provides a pluggable way to provide those services via {@link FileServiceProvider} implementations.
 * 
 * This service can be used by any component that needs to write files to an area shared by multiple application servers.
 * 
 * For example usage, see {@link SiteMapGenerator}.  The Broadleaf CMS module also uses this component to load, store, and 
 * manipulate images for the file-system.   
 * 
 * Generally, the process to create a new asset in the shared file system is ...
 * 1.  Call initializeWorkArea() to get a temporary directory
 * 2.  Create files, directories, etc. using the {@link FileWorkArea#filePathLocation} as the root directory.
 * 3.  Once your file processing is complete, call {@link #addOrUpdateResources(FileWorkArea, FileApplicationType)} to
 * 4.  Call {@link #closeWorkArea()} to clear out the temporary files
 * 
 * @author bpolster
 *
 */
@Service("blFileService")
public class BroadleafFileServiceImpl implements BroadleafFileService {
    
    private static final Log LOG = LogFactory.getLog(BroadleafFileServiceImpl.class);

    @Resource(name = "blFileServiceProviders")
    protected List<FileServiceProvider> fileServiceProviders = new ArrayList<FileServiceProvider>();
    
    @Resource(name = "blDefaultFileServiceProvider")
    protected FileServiceProvider defaultFileServiceProvider;

    private static final String DEFAULT_STORAGE_DIRECTORY = System.getProperty("java.io.tmpdir");
    
    @Value("${file.service.temp.file.base.directory}")
    protected String tempFileSystemBaseDirectory;    
    
    @Value("${asset.server.max.generated.file.system.directories}")
    protected int maxGeneratedDirectoryDepth = 2;
    
    @Value("${asset.server.file.classpath.directory}")
    protected String fileServiceClasspathDirectory;

    /**
     * Create a file work area that can be used for further operations. 
     * @return
     */
    @Override
    public FileWorkArea initializeWorkArea() {
        String baseDirectory = getBaseDirectory(false);
        String tempDirectory = getTempDirectory(baseDirectory);
        FileWorkArea fw = new FileWorkArea();
        fw.setFilePathLocation(tempDirectory);
        
        File tmpDir = new File(tempDirectory);

        if (!tmpDir.exists()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Creating temp directory named " + tempDirectory);
            }
            if (!tmpDir.mkdirs()) {
                throw new FileServiceException("Unable to create temporary working directory for " + tempDirectory);
            }
        }

        return fw;
    }

    /**
     * Closes the passed in work area.   This method will delete the work area (typically a directory on the file
     * system and all items it encloses).
     * @param Work Area
     */
    @Override
    public void closeWorkArea(FileWorkArea fwArea) {
        File tempDirectory = new File(fwArea.getFilePathLocation());
        try {
            if (tempDirectory.exists()) {
                FileUtils.deleteDirectory(tempDirectory);
            }

            for (int i = 1; i < maxGeneratedDirectoryDepth; i++) {
                tempDirectory = tempDirectory.getParentFile();
                if (tempDirectory.list().length == 0 && tempDirectory.exists()) {
                    FileUtils.deleteDirectory(tempDirectory);
                }
            }

        } catch (IOException ioe) {
            throw new FileServiceException("Unable to delete temporary working directory for " + tempDirectory, ioe);
        }
    }

    @Override
    public File getResource(String name) {
        return selectFileServiceProvider().getResource(name);
    }

    protected File getLocalResource(String resourceName, boolean skipSite) {
        String baseDirectory = getBaseDirectory(skipSite);
        
        // convert the separators to the system this is currently run on
        String systemResourcePath = FilenameUtils.separatorsToSystem(resourceName);
        
        String filePath = FilenameUtils.normalize(baseDirectory + File.separator + systemResourcePath);
        return new File(filePath);
    }

    @Override
    public File getLocalResource(String resourceName) {
        return getLocalResource(resourceName, false);
    }

    @Override
    public File getSharedLocalResource(String resourceName) {
        return getLocalResource(resourceName, true);
    }

    @Override
    public File getResource(String name, Long localTimeout) {
        File returnFile = getLocalResource(name);
        if (returnFile != null && returnFile.exists()) {
            if (localTimeout != null) {
                long lastModified = returnFile.lastModified();
                long now = System.currentTimeMillis();
                if ((now - lastModified) <= localTimeout.longValue()) {
                    return returnFile;
                }
            } else {
                return returnFile;
            }
        }

        return getResource(name);
    }

    @Override
    public boolean checkForResourceOnClassPath(String name) {
        ClassPathResource resource = lookupResourceOnClassPath(name);
        return (resource != null && resource.exists());
    }

    protected ClassPathResource lookupResourceOnClassPath(String name) {
        if (fileServiceClasspathDirectory != null && !"".equals(fileServiceClasspathDirectory)) {
            try {
                String resourceName = FilenameUtils.separatorsToUnix(FilenameUtils.normalize(fileServiceClasspathDirectory + '/' + name));
                ClassPathResource resource = new ClassPathResource(resourceName);
                if (resource.exists()) {
                    return resource;
                }
            } catch (Exception e) {
                LOG.error("Error getting resource from classpath", e);
            }
        }
        return null;
    }

    @Override
    public InputStream getClasspathResource(String name) {
        try {
            ClassPathResource resource = lookupResourceOnClassPath(name);
            if (resource != null && resource.exists()) {
                InputStream assetFile = resource.getInputStream();
                BufferedInputStream bufferedStream = new BufferedInputStream(assetFile);

                // Wrapping the buffered input stream with a globally shared stream allows us to 
                // vary the way the file names are generated on the file system.    
                // This benefits us (mainly in our demo site but their could be other uses) when we
                // have assets that are shared across sites that we also need to resize. 
                GloballySharedInputStream globallySharedStream = new GloballySharedInputStream(bufferedStream);
                globallySharedStream.mark(0);
                return globallySharedStream;
            } else {
                return null;
            }
        } catch (Exception e) {
            LOG.error("Error getting resource from classpath", e);
        }
        return null;
    }

    /**
     * Removes the resource matching the passed in file name from the FileProvider
     */
    @Override
    public boolean removeResource(String resourceName) {
        return selectFileServiceProvider().removeResource(resourceName);
    }

    /**
     * Takes in a work area and a fileName. Loads the file onto the provider.
     * 
     * Passing in removeFilesFromWorkArea to true allows for more efficient file processing
     * when using a local file system as it performs a move operation instead of a copy.
     * 
     * @param workArea
     * @param applicationType
     * @param fileNames
     * @param removeFilesFromWorkArea
     */
    @Override
    public void addOrUpdateResource(FileWorkArea workArea, File file, boolean removeFilesFromWorkArea) {
        List<File> files = new ArrayList<File>();
        files.add(file);
        addOrUpdateResources(workArea, files, removeFilesFromWorkArea);
    }

    /**
     * Takes in a work area and application type and moves all of the files to the configured FileProvider.
     * 
     * @param workArea
     * @param applicationType
     */
    @Override
    public void addOrUpdateResources(FileWorkArea workArea, boolean removeFilesFromWorkArea) {
        File folder = new File(workArea.getFilePathLocation());
        List<File> fileList = new ArrayList<File>();
        buildFileList(folder, fileList);
        addOrUpdateResources(workArea, fileList, removeFilesFromWorkArea);
    }
    
    @Override
    public void addOrUpdateResources(FileWorkArea workArea, List<File> files, boolean removeFilesFromWorkArea) {
        checkFiles(workArea, files);
        selectFileServiceProvider().addOrUpdateResources(workArea, files, removeFilesFromWorkArea);
    }

    /**
     * Returns the FileServiceProvider that can handle the passed in application type.
     * 
     * By default, this method returns the component configured at blFileServiceProvider
     * 
     * @param applicationType
     * @return
     */
    protected FileServiceProvider selectFileServiceProvider() {
        return defaultFileServiceProvider;
    }

    protected void checkFiles(FileWorkArea workArea, List<File> fileList) {
        for (File file : fileList) {
            String fileName = file.getAbsolutePath();
            if (!fileName.startsWith(workArea.getFilePathLocation())) {
                throw new FileServiceException("File operation attempted on file that is not in provided work area. "
                        + fileName + ".  Work area = " + workArea.getFilePathLocation());
            }
            if (!file.exists()) {
                throw new FileServiceException("Add or Update Resource called with filename that does not exist.  " + fileName);
            }
        }
    }

    /**
     * Returns the baseDirectory for writing and reading files as the property assetFileSystemPath if it
     * exists or java.tmp.io if that property has not been set.   
     * 
     */
    protected String getBaseDirectory(boolean skipSite) {
        String path = "";
        if (StringUtils.isBlank(tempFileSystemBaseDirectory)) {
            path = DEFAULT_STORAGE_DIRECTORY;
        } else {
            path = tempFileSystemBaseDirectory;
        }

        if (!skipSite) {
            // Create site specific directory if Multi-site (all site files will be located in the same directory)
            BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
            if (brc != null && brc.getSite() != null) {
                String siteDirectory = "site-" + brc.getSite().getId();
                String siteHash = DigestUtils.md5Hex(siteDirectory);
                path = FilenameUtils.concat(path, siteHash.substring(0, 2));
                path = FilenameUtils.concat(path, siteDirectory);
            }
        }

        return path;
    }

    /**
     * Returns a directory that is unique for this work area. 
     *   
     */
    protected String getTempDirectory(String baseDirectory) {
        assert baseDirectory != null;

        Random random = new Random();

        // This code is used to ensure that we don't have thousands of sub-directories in a single parent directory.
        for (int i = 0; i < maxGeneratedDirectoryDepth; i++) {
            if (i == 4) {
                LOG.warn("Property asset.server.max.generated.file.system.directories set to high, currently set to " +
                        maxGeneratedDirectoryDepth);
                break;
            }
            // check next int value
            int num = random.nextInt(256);
            baseDirectory = FilenameUtils.concat(baseDirectory, Integer.toHexString(num));
        }
        return baseDirectory;
    }

    /**
     * Adds the file to the passed in Collection.
     * If the file is a directory, adds its children recursively.   Otherwise, just adds the file to the list.    
     * @param file
     * @param fileList
     */
    protected void buildFileList(File file, Collection<File> fileList) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (child.isDirectory()) {
                        buildFileList(child, fileList);
                    } else {
                        fileList.add(child);
                    }
                }
            }
        } else {
            fileList.add(file);
        }
    }

    public String getTempFileSystemBaseDirectory() {
        return tempFileSystemBaseDirectory;
    }
    
    public void setTempFileSystemBaseDirectory(String tempFileSystemBaseDirectory) {
        this.tempFileSystemBaseDirectory = tempFileSystemBaseDirectory;
    }

    public List<FileServiceProvider> getFileServiceProviders() {
        return fileServiceProviders;
    }

    public void setFileServiceProviders(List<FileServiceProvider> fileServiceProviders) {
        this.fileServiceProviders = fileServiceProviders;
    }

    public int getMaxGeneratedDirectoryDepth() {
        return maxGeneratedDirectoryDepth;
    }

    public void setMaxGeneratedDirectoryDepth(int maxGeneratedDirectoryDepth) {
        this.maxGeneratedDirectoryDepth = maxGeneratedDirectoryDepth;
    }

    public FileServiceProvider getDefaultFileServiceProvider() {
        return defaultFileServiceProvider;
    }

    public void setDefaultFileServiceProvider(FileServiceProvider defaultFileServiceProvider) {
        this.defaultFileServiceProvider = defaultFileServiceProvider;
    }
}
