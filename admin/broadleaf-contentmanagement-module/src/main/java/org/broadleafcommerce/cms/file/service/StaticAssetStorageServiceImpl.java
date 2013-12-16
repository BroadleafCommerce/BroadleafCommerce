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

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.common.AssetNotFoundException;
import org.broadleafcommerce.cms.field.type.StorageType;
import org.broadleafcommerce.cms.file.dao.StaticAssetStorageDao;
import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.file.domain.StaticAssetStorage;
import org.broadleafcommerce.cms.file.service.operation.NamedOperationManager;
import org.broadleafcommerce.common.file.domain.FileWorkArea;
import org.broadleafcommerce.common.file.service.BroadleafFileService;
import org.broadleafcommerce.common.file.service.GloballySharedInputStream;
import org.broadleafcommerce.openadmin.server.service.artifact.ArtifactService;
import org.broadleafcommerce.openadmin.server.service.artifact.image.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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

import javax.annotation.Resource;

/**
 * @author Jeff Fischer, Brian Polster
 */
@Service("blStaticAssetStorageService")
public class StaticAssetStorageServiceImpl implements StaticAssetStorageService {

    @Value("${asset.server.max.uploadable.file.size}")
    protected long maxUploadableFileSize;

    @Value("${asset.server.file.buffer.size}")
    protected int fileBufferSize = 8096;

    private static final Log LOG = LogFactory.getLog(StaticAssetStorageServiceImpl.class);

    protected String cacheDirectory;

    @Resource(name="blStaticAssetService")
    protected StaticAssetService staticAssetService;

    @Resource(name = "blFileService")
    protected BroadleafFileService broadleafFileService;

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
    protected String appendTrailingSlash(String path) {
        if (!path.endsWith("/")) {
            path = "/" + path;
        }

        return path;
    }

    /**
     * Removes trailing "/" and ensures that there is a beginning "/"
     * @param path
     * @return
     */
    protected String removeLeadingSlash(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return path;
    }

    protected boolean shouldUseSharedFile(InputStream is) {
        return (is != null && is instanceof GloballySharedInputStream);
    }
        
    protected File getFileFromLocalRepository(String cachedFileName) {
        // Look for a shared file (this represents a file that was based on a file originally in the classpath.
        File cacheFile = broadleafFileService.getSharedLocalResource(cachedFileName);
        if (cacheFile.exists()) {
            return cacheFile;
        } else {
            return broadleafFileService.getLocalResource(cachedFileName);
        }
    }
    
    protected File lookupAssetAndCreateLocalFile(StaticAsset staticAsset, File baseLocalFile)
            throws IOException, SQLException {
        if (StorageType.FILESYSTEM.equals(staticAsset.getStorageType())) {
            return broadleafFileService.getResource(staticAsset.getFullUrl());            
        } else {
            StaticAssetStorage storage = readStaticAssetStorageByStaticAssetId(staticAsset.getId());
            if (storage != null) {
                InputStream is = storage.getFileData().getBinaryStream();
                createLocalFileFromInputStream(is, baseLocalFile);
            } 
        }
        return baseLocalFile;
    }   

    protected void createLocalFileFromClassPathResource(StaticAsset staticAsset, File baseLocalFile) throws IOException {
        InputStream is = broadleafFileService.getClasspathResource(staticAsset.getFullUrl());
        createLocalFileFromInputStream(is, baseLocalFile);
    }
    
    protected void createLocalFileFromInputStream(InputStream is, File baseLocalFile) throws IOException {
        BufferedOutputStream bos = null;
        FileWorkArea workArea = null;
        try {
            if (!baseLocalFile.getParentFile().exists()) {
                if (!baseLocalFile.getParentFile().mkdirs()) {
                    throw new RuntimeException("Unable to create middle directories for file: " + 
                            baseLocalFile.getAbsolutePath());
                }
            }
            
            workArea = broadleafFileService.initializeWorkArea();
            File tmpFile = new File(appendTrailingSlash(workArea.getFilePathLocation()) +
                    baseLocalFile.getName());

            bos = new BufferedOutputStream(new FileOutputStream(tmpFile));
            
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

            FileUtils.moveFile(tmpFile, baseLocalFile);
        } finally {
            try {
                if (bos != null) {
                    bos.flush();
                    bos.close();
                }
                if (workArea != null) {
                    broadleafFileService.closeWorkArea(workArea);
                }
            } catch (Throwable e) {
                //do nothing
            }
        }
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
        String cachedFileName = constructCacheFileName(staticAsset, convertedParameters);
        
        // Look for a shared file (this represents a file that was based on a file originally in the classpath.
        File cacheFile = getFileFromLocalRepository(cachedFileName);
        if (cacheFile.exists()) {
            return buildModel(cacheFile.getAbsolutePath(), mimeType);
        }
        
        // Obtain the base file (that we may need to convert based on the parameters
        String baseCachedFileName = constructCacheFileName(staticAsset, null);
        File baseLocalFile = getFileFromLocalRepository(baseCachedFileName);
        
        if (! baseLocalFile.exists()) {
            if (broadleafFileService.checkForResourceOnClassPath(staticAsset.getFullUrl())) {
                cacheFile = broadleafFileService.getSharedLocalResource(cachedFileName);
                baseLocalFile = broadleafFileService.getSharedLocalResource(baseCachedFileName);
                createLocalFileFromClassPathResource(staticAsset, baseLocalFile);
            } else {
                baseLocalFile = lookupAssetAndCreateLocalFile(staticAsset, baseLocalFile);
            }
        }
        
        if (convertedParameters.isEmpty()) {
            return buildModel(baseLocalFile.getAbsolutePath(), mimeType);
        } else {
            FileInputStream assetStream = new FileInputStream(baseLocalFile);
            BufferedInputStream original = new BufferedInputStream(assetStream);
            original.mark(0);                                    
            
            Operation[] operations = artifactService.buildOperations(convertedParameters, original, staticAsset.getMimeType());
            InputStream converted = artifactService.convert(original, operations, staticAsset.getMimeType());
            
            createLocalFileFromInputStream(converted, cacheFile);
            if ("image/gif".equals(mimeType)) {
                mimeType = "image/png";
            }
            return buildModel(cacheFile.getAbsolutePath(), mimeType);
        }
    }

    protected Map<String, String> buildModel(String returnFilePath, String mimeType) {
        Map<String, String> model = new HashMap<String, String>(2);
        model.put("cacheFilePath", returnFilePath);
        model.put("mimeType", mimeType);

        return model;
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

    /**
     * Builds a file system path for the passed in static asset and paramaterMap.
     * 
     * @param staticAsset
     * @param parameterMap
     * @param useSharedFile
     * @return
     */
    protected String constructCacheFileName(StaticAsset staticAsset, Map<String, String> parameterMap) {
        String fileName = staticAsset.getFullUrl();

        StringBuilder sb = new StringBuilder(200);
        sb.append(fileName.substring(0, fileName.lastIndexOf('.')));
        sb.append("---");

        StringBuilder sb2 = new StringBuilder(200);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        if (staticAsset.getAuditable() != null) {
            sb2.append(format.format(staticAsset.getAuditable().getDateUpdated() == null ? staticAsset.getAuditable().getDateCreated() : staticAsset.getAuditable().getDateUpdated()));
        }
        
        if (parameterMap != null) {
            for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
                sb2.append('-');
                sb2.append(entry.getKey());
                sb2.append('-');
                sb2.append(entry.getValue());
            }
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
            FileWorkArea tempWorkArea = broadleafFileService.initializeWorkArea();
            String destFileName = tempWorkArea.getFilePathLocation() + removeLeadingSlash(staticAsset.getFullUrl());

            InputStream input = file.getInputStream();
            byte[] buffer = new byte[fileBufferSize];

            File destFile = new File(destFileName);
            if (!destFile.getParentFile().exists()) {
                if (!destFile.getParentFile().mkdirs()) {
                    throw new RuntimeException("Unable to create parent directories for file: " + destFileName);
                }
            }

            OutputStream output = new FileOutputStream(destFile);
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

                broadleafFileService.addOrUpdateResource(tempWorkArea, destFile, true);
            } finally {
                output.close();
                broadleafFileService.closeWorkArea(tempWorkArea);
            }
        }
    }

}
