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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.field.type.StorageType;
import org.broadleafcommerce.cms.file.dao.StaticAssetDao;
import org.broadleafcommerce.cms.file.domain.ImageStaticAsset;
import org.broadleafcommerce.cms.file.domain.ImageStaticAssetImpl;
import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.file.domain.StaticAssetImpl;
import org.broadleafcommerce.common.file.service.StaticAssetPathService;
import org.broadleafcommerce.openadmin.server.service.artifact.image.ImageArtifactProcessor;
import org.broadleafcommerce.openadmin.server.service.artifact.image.ImageMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.detector.ExtensionMimeDetector;
import eu.medsea.mimeutil.detector.MagicMimeMimeDetector;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

/**
 * Created by bpolster.
 */
@Service("blStaticAssetService")
public class StaticAssetServiceImpl implements StaticAssetService {

    private static final Log LOG = LogFactory.getLog(StaticAssetServiceImpl.class);

    @Resource(name = "blImageArtifactProcessor")
    protected ImageArtifactProcessor imageArtifactProcessor;

    @Value("${asset.use.filesystem.storage}")
    protected boolean storeAssetsOnFileSystem = false;

    @Resource(name="blStaticAssetDao")
    protected StaticAssetDao staticAssetDao;

    @Resource(name="blStaticAssetStorageService")
    protected StaticAssetStorageService staticAssetStorageService;
    
    @Resource(name = "blStaticAssetPathService")
    protected StaticAssetPathService staticAssetPathService;

    private final Random random = new Random();
    private final String FILE_NAME_CHARS = "0123456789abcdef";

    @Override
    public StaticAsset findStaticAssetById(Long id) {
        return staticAssetDao.readStaticAssetById(id);
    }
    
    @Override
    public List<StaticAsset> readAllStaticAssets() {
        return staticAssetDao.readAllStaticAssets();
    }

    static {
        MimeUtil.registerMimeDetector(ExtensionMimeDetector.class.getName());
        MimeUtil.registerMimeDetector(MagicMimeMimeDetector.class.getName());
    }

    protected String getFileExtension(String fileName) {
        int pos = fileName.lastIndexOf(".");
        if (pos > 0) {
            return fileName.substring(pos + 1, fileName.length()).toLowerCase();
        } else {
            LOG.warn("No extension provided for asset : " + fileName);
            return null;
        }
    }

    /**
     * Generates a filename as a set of Hex digits.
     * @param size
     * @return
     */
    protected String generateFileName(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            int pos = random.nextInt(FILE_NAME_CHARS.length());
            sb = sb.append(FILE_NAME_CHARS.charAt(pos));
        }
        return sb.toString();
    }

    /**
     * Will assemble the url from the passed in properties as 
     *     /{entityType}/{fileName}
     *     /product/7001-ab12
     * 
     * If the properties above are not set, it will generate the fileName randomly.
     *     
     * @param url
     * @param asset
     * @param assetProperties
     * @return
     */
    protected String buildAssetURL(Map<String, String> assetProperties, String originalFilename) {
        StringBuilder path = new StringBuilder("/");
        
        String entityType = assetProperties.get("entityType");
        String entityId = assetProperties.get("entityId");
        String fileName = assetProperties.get("fileName");
        
        if (entityType != null) {
            path = path.append(entityType).append("/");
        }

        if (entityId != null) {
            path = path.append(entityId).append("/");
        }

        if (fileName != null) {
            int pos = fileName.indexOf(":");
            if (pos > 0) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Removing protocol from URL name" + fileName);
                }
                fileName = fileName.substring(pos + 1);
            }
        } else {
            fileName = originalFilename;
        }

        return path.append(fileName).toString();
    }

    @Override
    @Transactional("blTransactionManager")
    public StaticAsset createStaticAssetFromFile(MultipartFile file, Map<String, String> properties) {
        
        if (properties == null) {
            properties = new HashMap<String, String>();
        }

        String fullUrl = buildAssetURL(properties, file.getOriginalFilename());
        StaticAsset newAsset = staticAssetDao.readStaticAssetByFullUrl(fullUrl);
        int count = 0;
        while (newAsset != null) {
            count++;
            
            //try the new format first, then the old
            newAsset = staticAssetDao.readStaticAssetByFullUrl(getCountUrl(fullUrl, count, false));
            if (newAsset == null) {
                newAsset = staticAssetDao.readStaticAssetByFullUrl(getCountUrl(fullUrl, count, true));
            }
        }

        if (count > 0) {
            fullUrl = getCountUrl(fullUrl, count, false);
        }

        try {
            ImageMetadata metadata = imageArtifactProcessor.getImageMetadata(file.getInputStream());
            newAsset = new ImageStaticAssetImpl();
            ((ImageStaticAsset) newAsset).setWidth(metadata.getWidth());
            ((ImageStaticAsset) newAsset).setHeight(metadata.getHeight());
        } catch (Exception e) {
            //must not be an image stream
            newAsset = new StaticAssetImpl();
        }
        if (storeAssetsOnFileSystem) {
            newAsset.setStorageType(StorageType.FILESYSTEM);
        } else {
            newAsset.setStorageType(StorageType.DATABASE);
        }

        newAsset.setName(file.getOriginalFilename());
        getMimeType(file, newAsset);
        newAsset.setFileExtension(getFileExtension(file.getOriginalFilename()));
        newAsset.setFileSize(file.getSize());
        newAsset.setFullUrl(fullUrl);

        return staticAssetDao.addOrUpdateStaticAsset(newAsset, false);
    }
    
    /**
     * Gets the count URL based on the original fullUrl. If requested in legacy format this will return URLs like:
     * 
     *  /path/to/image.jpg-1
     *  /path/to/image.jpg-2
     *  
     * Whereas if this is in non-lagacy format (<b>legacy</b> == false):
     * 
     *  /path/to/image-1.jpg
     *  /path/to/image-2.jpg
     *  
     * Used to deal with duplicate URLs of uploaded assets
     *  
     */
    protected String getCountUrl(String fullUrl, int count, boolean legacyFormat) {
        String countUrl = fullUrl + '-' + count;
        int dotIndex = fullUrl.lastIndexOf('.');
        if (dotIndex != -1 && !legacyFormat) {
            countUrl = fullUrl.substring(0, dotIndex) + '-' + count + '.' + fullUrl.substring(dotIndex + 1);
        }
        
        return countUrl;
    }

    protected void getMimeType(MultipartFile file, StaticAsset newAsset) {
        Collection mimeTypes = MimeUtil.getMimeTypes(file.getOriginalFilename());
        if (!mimeTypes.isEmpty()) {
            MimeType mimeType = (MimeType) mimeTypes.iterator().next();
            newAsset.setMimeType(mimeType.toString());
        } else {
            try {
                mimeTypes = MimeUtil.getMimeTypes(file.getInputStream());
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
            if (!mimeTypes.isEmpty()) {
                MimeType mimeType = (MimeType) mimeTypes.iterator().next();
                newAsset.setMimeType(mimeType.toString());
            }
        }
    }

    @Override
    public StaticAsset findStaticAssetByFullUrl(String fullUrl) {
        try {
            fullUrl = URLDecoder.decode(fullUrl, "UTF-8");
            //strip out the jsessionid if it's there
            fullUrl = fullUrl.replaceAll(";jsessionid=.*?(?=\\?|$)", "");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding to decode fullUrl", e);
        }
        return staticAssetDao.readStaticAssetByFullUrl(fullUrl);
    }

    @Override
    public String getStaticAssetUrlPrefix() {
        return staticAssetPathService.getStaticAssetUrlPrefix();
    }

    @Override
    public String getStaticAssetEnvironmentUrlPrefix() {
        return staticAssetPathService.getStaticAssetEnvironmentUrlPrefix();
    }

    @Override
    public String getStaticAssetEnvironmentSecureUrlPrefix() {
        return staticAssetPathService.getStaticAssetEnvironmentSecureUrlPrefix();
    }

    public String convertAssetPath(String assetPath, String contextPath, boolean secureRequest) {
        return staticAssetPathService.convertAssetPath(assetPath, contextPath, secureRequest);
    }

}
