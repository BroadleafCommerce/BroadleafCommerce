/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.cms.file.service;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.common.AssetNotFoundException;
import org.broadleafcommerce.cms.field.type.StorageType;
import org.broadleafcommerce.cms.file.dao.StaticAssetStorageDao;
import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.file.domain.StaticAssetStorage;
import org.broadleafcommerce.cms.file.service.operation.NamedOperationManager;
import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.file.domain.FileWorkArea;
import org.broadleafcommerce.common.file.service.BroadleafFileService;
import org.broadleafcommerce.common.file.service.GloballySharedInputStream;
import org.broadleafcommerce.common.io.ConcurrentFileOutputStream;
import org.broadleafcommerce.common.util.StreamCapableTransactionalOperationAdapter;
import org.broadleafcommerce.common.util.StreamingTransactionCapableUtil;
import org.broadleafcommerce.openadmin.server.service.artifact.ArtifactService;
import org.broadleafcommerce.openadmin.server.service.artifact.image.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Jeff Fischer, Brian Polster
 */
@Service("blStaticAssetStorageService")
public class StaticAssetStorageServiceImpl implements StaticAssetStorageService {

    private static final Log LOG = LogFactory.getLog(StaticAssetStorageServiceImpl.class);

    protected static final String DEFAULT_ADMIN_IMAGE_EXTENSIONS = "bmp,jpg,jpeg,png,img,tiff,gif";

    public static final long DEFAULT_ASSET_UPLOAD_SIZE = 1024 * 1024 * 10;

    // 8kb default for the buffer for moving files around
    protected static final int DEFAULT_BUFFER_SIZE = 8096;

    protected String cacheDirectory;

    @Autowired
    protected Environment env;

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

    @Resource(name = "blStaticAssetServiceExtensionManager")
    protected StaticAssetServiceExtensionManager extensionManager;

    @Resource(name="blStreamingTransactionCapableUtil")
    protected StreamingTransactionCapableUtil transUtil;

    @Value("${image.artifact.recompress.formats:png}")
    protected String recompressFormats = "png";

    @Autowired(required = false)
    protected MultipartProperties defaultMultipartSettings;

    @Resource(name = "blConcurrentFileOutputStream")
    protected ConcurrentFileOutputStream concurrentFileOutputStream;


    protected StaticAsset findStaticAsset(String fullUrl) {
        StaticAsset staticAsset = staticAssetService.findStaticAssetByFullUrl(fullUrl);

        return staticAsset;
    }

    protected boolean shouldUseSharedFile(InputStream is) {
        return (is != null && is instanceof GloballySharedInputStream);
    }

    protected File getFileFromLocalRepository(String cachedFileName) {
        // Look for a shared file (this represents a file that was based on a file originally in the classpath.
        File cacheFile = null;
        if (extensionManager != null) {
            ExtensionResultHolder holder = new ExtensionResultHolder();
            ExtensionResultStatusType result = extensionManager.getProxy().fileExists(cachedFileName, holder);
            if (ExtensionResultStatusType.HANDLED.equals(result)) {
                cacheFile = (File) holder.getResult();
            }
        }
        if (cacheFile == null) {
            cacheFile = broadleafFileService.getSharedLocalResource(cachedFileName);
        }
        if (cacheFile.exists()) {
            return cacheFile;
        } else {
            return broadleafFileService.getLocalResource(cachedFileName);
        }
    }

    protected File lookupAssetAndCreateLocalFile(StaticAsset staticAsset, File baseLocalFile)
            throws IOException, SQLException {
        if (StorageType.FILESYSTEM.equals(staticAsset.getStorageType())) {
            File returnFile = broadleafFileService.getResource(staticAsset.getFullUrl());
            if (!returnFile.getAbsolutePath().equals(baseLocalFile.getAbsolutePath())) {
                createLocalFileFromInputStream(new FileInputStream(returnFile), baseLocalFile);
            }
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
        try {
            if (!baseLocalFile.getParentFile().exists()) {
                boolean directoriesCreated = false;
                if (!baseLocalFile.getParentFile().exists()) {
                    directoriesCreated = baseLocalFile.getParentFile().mkdirs();
                    if (!directoriesCreated) {
                        // There is a chance that another VM created the directories.   If not, we may not have
                        // proper permissions and this is an error we need to report.
                        if (!baseLocalFile.getParentFile().exists()) {
                            throw new RuntimeException("Unable to create middle directories for file: " +
                                    baseLocalFile.getAbsolutePath());
                        }
                    }
                }
            }

            concurrentFileOutputStream.write(is, baseLocalFile);

        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Override
    public Map<String, String> getCacheFileModel(String fullUrl, Map<String, String> parameterMap) throws Exception {
        StaticAsset staticAsset = findStaticAsset(fullUrl);
        if (staticAsset == null) {
            throw new AssetNotFoundException("Unable to find an asset for the url (" + fullUrl + ")");
        }
        String mimeType = staticAsset.getMimeType();

        //extract the values for any named parameters
        boolean shouldRecompress = shouldRecompress(mimeType);
        Map<String, String> convertedParameters = namedOperationManager.manageNamedParameters(parameterMap);
        if (shouldRecompress && MapUtils.isEmpty(convertedParameters)) {
            convertedParameters.put("recompress","true");
        }
        String cachedFileName = constructCacheFileName(staticAsset, convertedParameters);
      
        if (shouldRecompress) {
            convertedParameters.remove("recompress");
        }

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


        if (!shouldRecompress && convertedParameters.isEmpty()) {
            return buildModel(baseLocalFile.getAbsolutePath(), mimeType);
        } 
        else {
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

    protected boolean shouldRecompress(String mimeType) {
        String[] formats = null;
        if (!StringUtils.isEmpty(recompressFormats)) {
            formats = recompressFormats.split(",");
        }
        boolean response = false;
        if (!ArrayUtils.isEmpty(formats)) {
            for (String format : formats) {
                if (mimeType.toLowerCase().contains(format.toLowerCase())) {
                    response = true;
                    break;
                }
            }
        }
        return response;
    }

    protected Map<String, String> buildModel(String returnFilePath, String mimeType) {
        Map<String, String> model = new HashMap<String, String>(2);
        model.put("cacheFilePath", returnFilePath);
        model.put("mimeType", mimeType);

        return model;
    }

    @Override
    public StaticAssetStorage findStaticAssetStorageById(final Long id) {
        final StaticAssetStorage[] storage = new StaticAssetStorage[1];
        transUtil.runTransactionalOperation(new StreamCapableTransactionalOperationAdapter() {
            @Override
            public void execute() {
                storage[0] = staticAssetStorageDao.readStaticAssetStorageById(id);
            }
        }, RuntimeException.class);
        return storage[0];
    }

    @Override
    public StaticAssetStorage create() {
        return staticAssetStorageDao.create();
    }

    @Override
    public StaticAssetStorage readStaticAssetStorageByStaticAssetId(final Long id) {
        final StaticAssetStorage[] storage = new StaticAssetStorage[1];
        transUtil.runTransactionalOperation(new StreamCapableTransactionalOperationAdapter() {
            @Override
            public void execute() {
                storage[0] = staticAssetStorageDao.readStaticAssetStorageByStaticAssetId(id);
            }
        }, RuntimeException.class);
        return storage[0];
    }

    @Override
    public StaticAssetStorage save(final StaticAssetStorage assetStorage) {
        final StaticAssetStorage[] storage = new StaticAssetStorage[1];
        transUtil.runTransactionalOperation(new StreamCapableTransactionalOperationAdapter() {
            @Override
            public void execute() {
                storage[0] = staticAssetStorageDao.save(assetStorage);
            }
        }, RuntimeException.class);
        return storage[0];
    }

    @Override
    public void delete(final StaticAssetStorage assetStorage) {
        transUtil.runTransactionalOperation(new StreamCapableTransactionalOperationAdapter() {
            @Override
            public void execute() {
                staticAssetStorageDao.delete(assetStorage);
            }
        }, RuntimeException.class);
    }

    @Override
    public Blob createBlob(final MultipartFile uploadedFile) throws IOException {
        final Blob[] blob = new Blob[1];
        transUtil.runTransactionalOperation(new StreamCapableTransactionalOperationAdapter() {
            @Override
            public void execute() {
                try {
                    blob[0] = staticAssetStorageDao.createBlob(uploadedFile);
                } catch (IOException e) {
                    LOG.error("Unable to create blob from MultipartFile.", e);
                }
            }
        }, RuntimeException.class);
        return blob[0];
    }

    @Override
    public Blob createBlob(final InputStream uploadedFileInputStream, final long fileSize) throws IOException {
        final Blob[] blob = new Blob[1];
        transUtil.runTransactionalOperation(new StreamCapableTransactionalOperationAdapter() {
            @Override
            public void execute() {
                try {
                    blob[0] = staticAssetStorageDao.createBlob(uploadedFileInputStream, fileSize);
                } catch (IOException e) {
                    LOG.error("Unable to create blob from InputStream.", e);
                }
            }
        }, RuntimeException.class);
        return blob[0];
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
        if (staticAsset instanceof Auditable) {
            Auditable auditableStaticAsset = (Auditable) staticAsset;
            sb2.append(format.format(auditableStaticAsset.getDateUpdated() == null ? auditableStaticAsset.getDateCreated() : auditableStaticAsset.getDateUpdated()));
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
        createStaticAssetStorage(file.getInputStream(), staticAsset);
    }

    @Transactional("blTransactionManagerAssetStorageInfo")
    @Override
    public void createStaticAssetStorage(InputStream fileInputStream, StaticAsset staticAsset) throws IOException {
        if (StorageType.DATABASE.equals(staticAsset.getStorageType())) {
            StaticAssetStorage storage = create();
            storage.setStaticAssetId(staticAsset.getId());
            Blob uploadBlob = createBlob(fileInputStream, staticAsset.getFileSize());
            storage.setFileData(uploadBlob);
            save(storage);
        } else if (StorageType.FILESYSTEM.equals(staticAsset.getStorageType())) {
            FileWorkArea tempWorkArea = broadleafFileService.initializeWorkArea();
            // Convert the given URL from the asset to a system-specific suitable file path
            String destFileName = FilenameUtils.normalize(tempWorkArea.getFilePathLocation() + File.separator + FilenameUtils.separatorsToSystem(staticAsset.getFullUrl()));

            InputStream input = fileInputStream;
            byte[] buffer = new byte[getFileBufferSize()];

            File destFile = new File(destFileName);
            if (!destFile.getParentFile().exists()) {
                if (!destFile.getParentFile().mkdirs()) {
                    if (!destFile.getParentFile().exists()) {
                        throw new RuntimeException("Unable to create parent directories for file: " + destFileName);
                    }
                }
            }

            OutputStream output = new FileOutputStream(destFile);
            long maxFileSize = getMaxUploadSizeForFile(destFileName);
            try {
                int bytesRead;
                int totalBytesRead = 0;
                while ((bytesRead = input.read(buffer)) != -1) {
                    totalBytesRead += bytesRead;
                    if (totalBytesRead > maxFileSize) {
                        throw new IOException("Maximum Upload File Size Exceeded");
                    }
                    output.write(buffer, 0, bytesRead);
                }

                // close the output file stream prior to moving files around
                output.close();
                broadleafFileService.addOrUpdateResource(tempWorkArea, destFile, false);
            } finally {
                IOUtils.closeQuietly(output);
                broadleafFileService.closeWorkArea(tempWorkArea);
            }
        }
    }

    protected long getMaxUploadSizeForFile(String fileName) {
        if (isImageFile(fileName)) {
            return getMaxUploadableImageSize();
        }

        return getMaxUploadableFileSize();
    }

    protected boolean isImageFile(String fileName) {
        String extension = getFileExtension(fileName);

        for (String imageFileExtension : getAdminImageFileExtensions()) {
            if (imageFileExtension.equalsIgnoreCase(extension)) {
                return true;
            }
        }

        return false;
    }

    protected String getFileExtension(String assetPath) {
        int extensionStartIndex = assetPath.lastIndexOf(".") + 1;
        return assetPath.substring(extensionStartIndex);
    }

    protected long getMaxUploadableFileSize() {
        Long blcUploadSize = env.getProperty("asset.server.max.uploadable.file.size", Long.class);
        if (blcUploadSize != null) {
            return blcUploadSize;
        }

        if (defaultMultipartSettings != null) {
            return defaultMultipartSettings.createMultipartConfig().getMaxFileSize();
        }
        return DEFAULT_ASSET_UPLOAD_SIZE;
    }

    protected long getMaxUploadableImageSize() {
        // backwards-compatibility checks, only use the image-size specific property if it is
        // not the default value. Otherwise, delegate to the old property
        Long maxImgSize = env.getProperty("asset.server.max.uploadable.image.size", Long.class);
        if (maxImgSize == null) {
            return getMaxUploadableFileSize();
        } else {
            return maxImgSize;
        }
    }

    protected int getFileBufferSize() {
        return env.getProperty("asset.server.file.buffer.size", int.class, DEFAULT_BUFFER_SIZE);
    }

    protected List<String> getAdminImageFileExtensions() {
        String extensions = env.getProperty("admin.image.file.extensions", String.class, DEFAULT_ADMIN_IMAGE_EXTENSIONS);
        return Arrays.asList(extensions.split(","));
    }

    @Override
    public void validateFileSize(MultipartFile file) throws IOException {
        long maxSize = getMaxUploadSizeForFile(file.getOriginalFilename());
        if (file.getSize() > maxSize) {
            throw new IOException("Maximum Upload File Size Exceeded");
        }
    }
}
