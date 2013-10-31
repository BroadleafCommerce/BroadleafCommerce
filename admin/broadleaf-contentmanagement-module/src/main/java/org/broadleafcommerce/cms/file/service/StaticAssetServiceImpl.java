/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.file.service;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.common.AbstractContentService;
import org.broadleafcommerce.cms.field.type.StorageType;
import org.broadleafcommerce.cms.file.dao.StaticAssetDao;
import org.broadleafcommerce.cms.file.domain.ImageStaticAsset;
import org.broadleafcommerce.cms.file.domain.ImageStaticAssetImpl;
import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.file.domain.StaticAssetImpl;
import org.broadleafcommerce.common.file.service.StaticAssetPathService;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;
import org.broadleafcommerce.openadmin.server.dao.SandBoxItemDao;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItem;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItemType;
import org.broadleafcommerce.openadmin.server.domain.SandBoxOperationType;
import org.broadleafcommerce.openadmin.server.service.artifact.image.ImageArtifactProcessor;
import org.broadleafcommerce.openadmin.server.service.artifact.image.ImageMetadata;
import org.hibernate.Criteria;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by bpolster.
 */
@Service("blStaticAssetService")
public class StaticAssetServiceImpl extends AbstractContentService implements StaticAssetService {

    private static final Log LOG = LogFactory.getLog(StaticAssetServiceImpl.class);

    @Resource(name = "blImageArtifactProcessor")
    protected ImageArtifactProcessor imageArtifactProcessor;

    @Value("${asset.use.filesystem.storage}")
    protected boolean storeAssetsOnFileSystem = false;

    @Value("${automatically.approve.static.assets}")
    protected boolean automaticallyApproveAndPromoteStaticAssets=true;

    @Resource(name="blStaticAssetDao")
    protected StaticAssetDao staticAssetDao;

    @Resource(name="blSandBoxItemDao")
    protected SandBoxItemDao sandBoxItemDao;

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
        StaticAsset newAsset = staticAssetDao.readStaticAssetByFullUrl(fullUrl, null);
        int count = 0;
        while (newAsset != null) {
            count++;
            
            //try the new format first, then the old
            newAsset = staticAssetDao.readStaticAssetByFullUrl(getCountUrl(fullUrl, count, false), null);
            if (newAsset == null) {
                newAsset = staticAssetDao.readStaticAssetByFullUrl(getCountUrl(fullUrl, count, true), null);
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
    public StaticAsset findStaticAssetByFullUrl(String fullUrl, SandBox targetSandBox) {
        try {
            fullUrl = URLDecoder.decode(fullUrl, "UTF-8");
            //strip out the jsessionid if it's there
            fullUrl = fullUrl.replaceAll(";jsessionid=.*?(?=\\?|$)", "");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding to decode fullUrl", e);
        }
        return staticAssetDao.readStaticAssetByFullUrl(fullUrl, targetSandBox);
    }

    @Override
    public StaticAsset addStaticAsset(StaticAsset staticAsset, SandBox destinationSandbox) {
        
        if (automaticallyApproveAndPromoteStaticAssets) {           
            destinationSandbox = null;
        }
        
        staticAsset.setSandbox(destinationSandbox);
        staticAsset.setDeletedFlag(false);
        staticAsset.setArchivedFlag(false);
        StaticAsset newAsset = staticAssetDao.addOrUpdateStaticAsset(staticAsset, true);
        
        if (! isProductionSandBox(destinationSandbox)) {
            sandBoxItemDao.addSandBoxItem(destinationSandbox.getId(), SandBoxOperationType.ADD, SandBoxItemType.STATIC_ASSET, newAsset.getFullUrl(), newAsset.getId(), null);
        }
        return newAsset;
    }

    @Override
    public StaticAsset updateStaticAsset(StaticAsset staticAsset, SandBox destSandbox) {
        if (staticAsset.getLockedFlag()) {
            throw new IllegalArgumentException("Unable to update a locked record");
        }
        
        if (automaticallyApproveAndPromoteStaticAssets) {           
            destSandbox = null;
        }

        if (checkForSandboxMatch(staticAsset.getSandbox(), destSandbox)) {
            if (staticAsset.getDeletedFlag()) {
                SandBoxItem item = sandBoxItemDao.retrieveBySandboxAndTemporaryItemId(staticAsset.getSandbox()==null?null:staticAsset.getSandbox().getId(), SandBoxItemType.STATIC_ASSET, staticAsset.getId());
                if (staticAsset.getOriginalAssetId() == null && item != null) {
                    // This item was added in this sandbox and now needs to be deleted.
                    staticAsset.setArchivedFlag(true);
                    item.setArchivedFlag(true);
                } else if (item != null) {
                    // This item was being updated but now is being deleted - so change the
                    // sandbox operation type to deleted
                    item.setSandBoxOperationType(SandBoxOperationType.DELETE);
                    sandBoxItemDao.updateSandBoxItem(item);
                } else if (automaticallyApproveAndPromoteStaticAssets) {
                    staticAsset.setArchivedFlag(true);
                }
            }
            return staticAssetDao.addOrUpdateStaticAsset(staticAsset, true);
        } else if (isProductionSandBox(staticAsset.getSandbox())) {
            // Move from production to destSandbox
            StaticAsset clonedAsset = staticAsset.cloneEntity();
            clonedAsset.setOriginalAssetId(staticAsset.getId());
            clonedAsset.setSandbox(destSandbox);
            StaticAsset returnAsset = staticAssetDao.addOrUpdateStaticAsset(clonedAsset, true);

            StaticAsset prod = findStaticAssetById(staticAsset.getId());
            prod.setLockedFlag(true);
            staticAssetDao.addOrUpdateStaticAsset(prod, false);

            SandBoxOperationType type = SandBoxOperationType.UPDATE;
            if (clonedAsset.getDeletedFlag()) {
                type = SandBoxOperationType.DELETE;
            }

            sandBoxItemDao.addSandBoxItem(destSandbox.getId(), type, SandBoxItemType.STATIC_ASSET, returnAsset.getFullUrl(), returnAsset.getId(), returnAsset.getOriginalAssetId());
            return returnAsset;
        } else {
            // This should happen via a promote, revert, or reject in the sandbox service
            throw new IllegalArgumentException("Update called when promote or reject was expected.");
        }
    }

    // Returns true if the src and dest sandbox are the same.
    private boolean checkForSandboxMatch(SandBox src, SandBox dest) {
        if (src != null) {
            if (dest != null) {
                return src.getId().equals(dest.getId());
            }
        }
        return (src == null && dest == null);
    }

//    // Returns true if the dest sandbox is production.
//    private boolean checkForProductionSandbox(SandBox dest) {
//        boolean productionSandbox = false;
//
//        if (dest == null) {
//            productionSandbox = true;
//        } else {
//            if (dest.getSite() != null && dest.getSite().getProductionSandbox() != null && dest.getSite().getProductionSandbox().getId() != null) {
//                productionSandbox = dest.getSite().getProductionSandbox().getId().equals(dest.getId());
//            }
//        }
//
//        return productionSandbox;
//    }

    // Returns true if the dest sandbox is production.
    private boolean isProductionSandBox(SandBox dest) {
        return dest == null || SandBoxType.PRODUCTION.equals(dest.getSandBoxType());
    }

    @Override
    public void deleteStaticAsset(StaticAsset staticAsset, SandBox destinationSandbox) {
        staticAsset.setDeletedFlag(true);
        updateStaticAsset(staticAsset, destinationSandbox);
    }

    @Override
    public List<StaticAsset> findAssets(SandBox sandbox, Criteria c) {
        return findItems(sandbox, c, StaticAsset.class, StaticAssetImpl.class, "originalAssetId");
    }

    @Override
    public Long countAssets(SandBox sandbox, Criteria c) {
       return countItems(sandbox, c, StaticAssetImpl.class, "originalAssetId");
    }

    @Override
    public void itemPromoted(SandBoxItem sandBoxItem, SandBox destinationSandBox) {
        if (! SandBoxItemType.STATIC_ASSET.equals(sandBoxItem.getSandBoxItemType())) {
            return;
        }
        StaticAsset asset = staticAssetDao.readStaticAssetById(sandBoxItem.getTemporaryItemId());

        if (asset == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Asset not found " + sandBoxItem.getTemporaryItemId());
            }
        } else {
            boolean productionSandBox = isProductionSandBox(destinationSandBox);
            if (productionSandBox) {
                asset.setLockedFlag(false);
            } else {
                asset.setLockedFlag(true);
            }
            if (productionSandBox && asset.getOriginalAssetId() != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Asset promoted to production.  " + asset.getId() + ".  Archiving original asset " + asset.getOriginalAssetId());
                }
                StaticAsset originalAsset = staticAssetDao.readStaticAssetById(sandBoxItem.getTemporaryItemId());
                originalAsset.setArchivedFlag(Boolean.TRUE);
                staticAssetDao.addOrUpdateStaticAsset(originalAsset, false);
                asset.setOriginalAssetId(null);

                if (asset.getDeletedFlag()) {
                    asset.setArchivedFlag(Boolean.TRUE);
                }
            }
        }
        if (asset.getOriginalSandBox() == null) {
            asset.setOriginalSandBox(asset.getSandbox());
        }
        asset.setSandbox(destinationSandBox);
        staticAssetDao.addOrUpdateStaticAsset(asset, false);
    }

    @Override
    public void itemRejected(SandBoxItem sandBoxItem, SandBox destinationSandBox) {
        if (! SandBoxItemType.STATIC_ASSET.equals(sandBoxItem.getSandBoxItemType())) {
            return;
        }
        StaticAsset asset = staticAssetDao.readStaticAssetById(sandBoxItem.getTemporaryItemId());

        if (asset != null) {
            asset.setSandbox(destinationSandBox);
            asset.setOriginalSandBox(null);
            asset.setLockedFlag(false);
            staticAssetDao.addOrUpdateStaticAsset(asset, false);
        }
    }

    @Override
    public void itemReverted(SandBoxItem sandBoxItem) {
        if (! SandBoxItemType.STATIC_ASSET.equals(sandBoxItem.getSandBoxItemType())) {
            return;
        }
        StaticAsset asset = staticAssetDao.readStaticAssetById(sandBoxItem.getTemporaryItemId());

        if (asset != null) {
            asset.setArchivedFlag(Boolean.TRUE);
            asset.setLockedFlag(false);
            staticAssetDao.addOrUpdateStaticAsset(asset, false);

            StaticAsset originalAsset = staticAssetDao.readStaticAssetById(sandBoxItem.getOriginalItemId());
            originalAsset.setLockedFlag(false);
            staticAssetDao.addOrUpdateStaticAsset(originalAsset, false);
        }
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

    @Override
    public boolean getAutomaticallyApproveAndPromoteStaticAssets() {
        return automaticallyApproveAndPromoteStaticAssets;
    }

    @Override
    public void setAutomaticallyApproveAndPromoteStaticAssets(boolean automaticallyApproveAndPromoteStaticAssets) {
        this.automaticallyApproveAndPromoteStaticAssets = automaticallyApproveAndPromoteStaticAssets;
    }

    public String convertAssetPath(String assetPath, String contextPath, boolean secureRequest) {
        return staticAssetPathService.convertAssetPath(assetPath, contextPath, secureRequest);
    }

}
