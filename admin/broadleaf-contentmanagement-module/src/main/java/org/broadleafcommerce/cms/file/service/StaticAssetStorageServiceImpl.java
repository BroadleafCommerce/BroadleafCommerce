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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.common.AssetNotFoundException;
import org.broadleafcommerce.cms.field.type.StorageType;
import org.broadleafcommerce.cms.file.dao.StaticAssetStorageDao;
import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.file.domain.StaticAssetStorage;
import org.broadleafcommerce.cms.file.service.operation.NamedOperationManager;
import org.broadleafcommerce.common.file.service.GloballySharedInputStream;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.server.service.artifact.ArtifactService;
import org.broadleafcommerce.openadmin.server.service.artifact.image.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

/**
 * @author Jeff Fischer, Brian Polster
 */
@Service("blStaticAssetStorageService")
public class StaticAssetStorageServiceImpl implements StaticAssetStorageService {

    @Value("${asset.server.file.system.path}")
    protected String assetFileSystemPath;

    @Value("${asset.server.file.classpath.directory}")
    protected String assetFileClasspathDirectory;

    @Value("${asset.server.max.generated.file.system.directories}")
    protected int assetServerMaxGeneratedDirectories;

    @Value("${asset.server.max.uploadable.file.size}")
    protected long maxUploadableFileSize;

    @Value("${asset.server.file.buffer.size}")
    protected int fileBufferSize = 8096;

    private static final Log LOG = LogFactory.getLog(StaticAssetStorageServiceImpl.class);
    private static final String DEFAULT_STORAGE_DIRECTORY = System.getProperty("java.io.tmpdir");

    protected String cacheDirectory;

    @Resource(name="blStaticAssetService")
    protected StaticAssetService staticAssetService;

    @Resource(name="blArtifactService")
    protected ArtifactService artifactService;

    @Resource(name="blStaticAssetStorageDao")
    protected StaticAssetStorageDao staticAssetStorageDao;

    @Resource(name="blNamedOperationManager")
    protected NamedOperationManager namedOperationManager;

    protected StaticAsset findStaticAsset(String fullUrl) {
        StaticAsset staticAsset = staticAssetService.findStaticAssetByFullUrl(fullUrl);

        return staticAsset;
    }

    /**
     * Removes trailing "/" and ensures that there is a beginning "/"
     * @param path
     * @return
     */
    protected String fixPath(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    @Override
    public String generateStorageFileName(StaticAsset staticAsset, boolean useSharedFile) {
        return generateStorageFileName(staticAsset.getFullUrl(), useSharedFile);
    }

    /**
     * Returns the baseDirectory for writing and reading files as the property assetFileSystemPath if it
     * exists or java.tmp.io if that property has not been set.
     */
    protected String getBaseDirectory() {
        if (assetFileSystemPath != null && !"".equals(assetFileSystemPath.trim())) {
            return assetFileSystemPath;
        } else {
            return DEFAULT_STORAGE_DIRECTORY;
        }
    }

    @Override
    public String generateStorageFileName(String fullUrl, boolean useSharedFile) {
        String baseDirectory = getBaseDirectory();
        StringBuilder fileName = new StringBuilder(fixPath(baseDirectory));
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc != null) {
            Site site = brc.getSite();
            if (site != null && !useSharedFile) {
                String siteDirectory = "/site-" + site.getId();
                String siteHash = DigestUtils.md5Hex(siteDirectory);
                fileName = fileName.append("/").append(siteHash.substring(0, 2)).append(siteDirectory);
            }
        }
        
        // Create directories based on hash
        String fileHash = DigestUtils.md5Hex(fullUrl);
        for (int i = 0; i < assetServerMaxGeneratedDirectories; i++) {
            if (i == 4) {
                LOG.warn("Property assetServerMaxGeneratedDirectories set to high, ignoring values past 4 - value set to" +
                        assetServerMaxGeneratedDirectories);
                break;
            }
            fileName = fileName.append("/").append(fileHash.substring(i * 2, (i + 1) * 2));
        }

        int pos = fullUrl.lastIndexOf("/");
        if (pos >= 0) {
            // Use the fileName as specified if possible.
            fileName = fileName.append(fullUrl.substring(pos));
        } else {
            // Just use the hash since we didn't find a filename for this one.
            fileName = fileName.append("/").append(fullUrl);
        }

        return fileName.toString();
    }

    protected boolean shouldUseSharedFile(InputStream is) {
        return (is != null && is instanceof GloballySharedInputStream);
    }

    @Transactional("blTransactionManagerAssetStorageInfo")
    @Override
    public Map<String, String> getCacheFileModel(String fullUrl, Map<String, String> parameterMap) throws Exception {
        StaticAsset staticAsset = findStaticAsset(fullUrl);
        if (staticAsset == null) {
            throw new AssetNotFoundException("Unable to find an asset for the url (" + fullUrl + ")");
        }
        String mimeType = staticAsset.getMimeType();

        //extract the values for any named parameters
        Map<String, String> convertedParameters = namedOperationManager.manageNamedParameters(parameterMap);   
        String returnFilePath = null;

        if (StorageType.FILESYSTEM.equals(staticAsset.getStorageType()) && convertedParameters.isEmpty()) {
            InputStream classPathInputStream = getResourceFromClasspath(staticAsset);
            if (classPathInputStream != null) {
                // Create a file system cache file representing this file.
                String cacheName = constructCacheFileName(staticAsset, convertedParameters, true);
                File cacheFile = new File(cacheName);
                if (!cacheFile.exists()) {
                    createCacheFile(classPathInputStream, cacheFile);
                }
                returnFilePath = cacheFile.getAbsolutePath();
            } else {
                returnFilePath = generateStorageFileName(staticAsset.getFullUrl(), false);
            }
        } else {
            String sharedCacheName = constructCacheFileName(staticAsset, convertedParameters, true);
            File cacheFile = new File(sharedCacheName);

            // See if the shared file exists.   This is primarily to support a multi-tenant
            // implementation that shares assets across the tenants.   If not, check for the 
            // site specific file.
            if (!cacheFile.exists()) {
                String cacheName = constructCacheFileName(staticAsset, convertedParameters, false);
                cacheFile = new File(cacheName);
            }

            if (!cacheFile.exists()) {
                InputStream original = findInputStreamForStaticAsset(staticAsset);
                boolean useSharedFile = shouldUseSharedFile(original);
    
                if (!convertedParameters.isEmpty()) {
                    Operation[] operations = artifactService.buildOperations(convertedParameters, original, staticAsset.getMimeType());
                    InputStream converted = artifactService.convert(original, operations, staticAsset.getMimeType());
                    createCacheFile(converted, cacheFile);
                    if ("image/gif".equals(mimeType)) {
                        mimeType = "image/png";
                    }
                } else {
                    if (useSharedFile) {
                        cacheFile = new File(sharedCacheName);
                        createCacheFile(original, cacheFile);
                    } else {
                        createCacheFile(original, cacheFile);
                    }
                }
            }
            returnFilePath = cacheFile.getAbsolutePath();
        }
        Map<String, String> model = new HashMap<String, String>(2);
        model.put("cacheFilePath", returnFilePath);
        model.put("mimeType", mimeType);

        return model;
    }

    protected InputStream findInputStreamForStaticAsset(StaticAsset staticAsset) throws SQLException, IOException {
        InputStream classPathInputStream = getResourceFromClasspath(staticAsset);
        if (classPathInputStream != null) {
            return classPathInputStream;
        }

        if (StorageType.DATABASE.equals(staticAsset.getStorageType())) {
            StaticAssetStorage storage = readStaticAssetStorageByStaticAssetId(staticAsset.getId());
            //there are filter operations to perform on the asset
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = null;
            try {
                is = storage.getFileData().getBinaryStream();
                byte[] buffer = new byte[fileBufferSize];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                baos.flush();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Throwable e) {
                        //do nothing
                    }
                }
            }
            InputStream original = new ByteArrayInputStream(baos.toByteArray());
            return original;
        } else if (StorageType.FILESYSTEM.equals(staticAsset.getStorageType())) {
            FileInputStream assetFile = new FileInputStream(generateStorageFileName(staticAsset.getFullUrl(), false));
            BufferedInputStream bufferedStream = new BufferedInputStream(assetFile);
            bufferedStream.mark(0);
            return bufferedStream;
        } else {
            throw new IllegalArgumentException("Unknown storage type while trying to read static asset.");
        }
    }

    protected InputStream getResourceFromClasspath(StaticAsset staticAsset) {
        if (assetFileClasspathDirectory != null && !"".equals(assetFileClasspathDirectory)) {
            try {
                ClassPathResource resource = new ClassPathResource(assetFileClasspathDirectory + staticAsset.getFullUrl());

                if (resource.exists()) {
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
        }
        return null;
    }

    @Transactional("blTransactionManagerAssetStorageInfo")
    @Override
    public StaticAssetStorage findStaticAssetStorageById(Long id) {
        return staticAssetStorageDao.readStaticAssetStorageById(id);
    }

    @Transactional("blTransactionManagerAssetStorageInfo")
    @Override
    public StaticAssetStorage create() {
        return staticAssetStorageDao.create();
    }

    @Transactional("blTransactionManagerAssetStorageInfo")
    @Override
    public StaticAssetStorage readStaticAssetStorageByStaticAssetId(Long id) {
        return staticAssetStorageDao.readStaticAssetStorageByStaticAssetId(id);
    }

    @Transactional("blTransactionManagerAssetStorageInfo")
    @Override
    public StaticAssetStorage save(StaticAssetStorage assetStorage) {
        return staticAssetStorageDao.save(assetStorage);
    }

    @Transactional("blTransactionManagerAssetStorageInfo")
    @Override
    public void delete(StaticAssetStorage assetStorage) {
        staticAssetStorageDao.delete(assetStorage);
    }

    @Transactional("blTransactionManagerAssetStorageInfo")
    @Override
    public Blob createBlob(MultipartFile uploadedFile) throws IOException {
        return staticAssetStorageDao.createBlob(uploadedFile);
    }

    protected void createCacheFile(InputStream is, File cacheFile) throws SQLException, IOException {
        if (!cacheFile.getParentFile().exists()) {
            if (!cacheFile.getParentFile().mkdirs()) {
                throw new RuntimeException("Unable to create middle directories for file: " + cacheFile.getAbsolutePath());
            }
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(cacheFile));
        try {
            boolean eof = false;
            int temp;
            while (!eof) {
                temp = is.read();
                if (temp < 0) {
                    eof = true;
                } else {
                    bos.write(temp);
                }
            }
        } finally {
            try {
                bos.flush();
                bos.close();
            } catch (Throwable e) {
                //do nothing
            }
        }
    }

    /**
     * Builds a file system path for the passed in static asset and paramaterMap.
     * 
     * If in a multi-site implementation, the system will also prefix the filepath with a site-identifier
     * unless the useSharedFile parameter is set to true.
     * 
     * @param staticAsset
     * @param parameterMap
     * @param useSharedFile
     * @return
     */
    protected String constructCacheFileName(StaticAsset staticAsset, Map<String, String> parameterMap, boolean useSharedFile) {
        String fileName = generateStorageFileName(staticAsset, useSharedFile);

        StringBuilder sb = new StringBuilder(200);
        sb.append(fileName.substring(0, fileName.lastIndexOf('.')));
        sb.append("---");

        StringBuilder sb2 = new StringBuilder(200);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        if (staticAsset.getAuditable() != null) {
            sb2.append(format.format(staticAsset.getAuditable().getDateUpdated() == null ? staticAsset.getAuditable().getDateCreated() : staticAsset.getAuditable().getDateUpdated()));
        }
        
        for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
            sb2.append('-');
            sb2.append(entry.getKey());
            sb2.append('-');
            sb2.append(entry.getValue());
        }

        String digest;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(sb2.toString().getBytes());
            BigInteger number = new BigInteger(1,messageDigest);
            digest = number.toString(16);
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        sb.append(pad(digest, 32, '0'));
        sb.append(fileName.substring(fileName.lastIndexOf('.')));

        return sb.toString();
    }

    protected String pad(String s, int length, char pad) {
        StringBuilder buffer = new StringBuilder(s);
        while (buffer.length() < length) {
            buffer.insert(0, pad);
        }
        return buffer.toString();
    }

    @Transactional("blTransactionManagerAssetStorageInfo")
    @Override
    public void createStaticAssetStorageFromFile(MultipartFile file, StaticAsset staticAsset) throws IOException {
        if (StorageType.DATABASE.equals(staticAsset.getStorageType())) {
            StaticAssetStorage storage = staticAssetStorageDao.create();
            storage.setStaticAssetId(staticAsset.getId());
            Blob uploadBlob = staticAssetStorageDao.createBlob(file);
            storage.setFileData(uploadBlob);
            staticAssetStorageDao.save(storage);
        } else if (StorageType.FILESYSTEM.equals(staticAsset.getStorageType())) {
            InputStream input = file.getInputStream();
            byte[] buffer = new byte[fileBufferSize];
            String destFileName = generateStorageFileName(staticAsset.getFullUrl(), false);
            String tempFileName = destFileName.substring(0, destFileName.lastIndexOf("/") + 1) + UUID.randomUUID().toString();
            File tmpFile = new File(tempFileName);
            if (!tmpFile.getParentFile().exists()) {
                if (!tmpFile.getParentFile().mkdirs()) {
                    throw new RuntimeException("Unable to create parent directories for file: " + destFileName);
                }
            }
            OutputStream output = new FileOutputStream(tmpFile);
            boolean deleteFile = false;
            try {
                int bytesRead;
                int totalBytesRead = 0;
                while ((bytesRead = input.read(buffer)) != -1) {
                    totalBytesRead += bytesRead;
                    if (totalBytesRead > maxUploadableFileSize) {
                        deleteFile = true;
                        throw new IOException("Maximum Upload File Size Exceeded");
                    }
                    output.write(buffer, 0, bytesRead);
                }
            } finally {
                output.close();
                if (deleteFile && tmpFile.exists()) {
                    tmpFile.delete();
                }
            }
            File newFile = new File(destFileName);
            if (!tmpFile.renameTo(newFile)) {
                if (!newFile.exists()) {
                    throw new RuntimeException("Unable to rename temp file to create file named: " + destFileName);
                }
            }
        }
    }

}
