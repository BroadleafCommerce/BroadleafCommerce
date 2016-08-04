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
package org.broadleafcommerce.cms.admin.web.service;

import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.file.service.operation.StaticMapNamedOperationComponent;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.ListGridRecord;
import org.broadleafcommerce.openadmin.web.form.component.MediaField;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.broadleafcommerce.openadmin.web.service.FormBuilderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("blAssetFormBuilderService")
public class AssetFormBuilderServiceImpl implements AssetFormBuilderService {
    
    @Resource(name = "blFormBuilderService")
    protected FormBuilderService formBuilderService;
    
    @Resource(name = "blStaticAssetService")
    protected StaticAssetService staticAssetService;
    
    @Resource(name = "blStaticMapNamedOperationComponent")
    protected StaticMapNamedOperationComponent operationMap;
 
    @Override
    public void addImageThumbnailField(ListGrid listGrid, String urlField) {
        listGrid.getHeaderFields().add(new Field()
            .withName("thumbnail")
            .withFriendlyName("Asset_thumbnail")
            .withFieldType(SupportedFieldType.STRING.toString())
            .withOrder(Integer.MIN_VALUE)
            .withColumnWidth("50px")
            .withFilterSortDisabled(true));
        
        for (ListGridRecord record : listGrid.getRecords()) {
            // Get the value of the URL
            String imageUrl = record.getField(urlField).getValue();
            
            // Prepend the static asset url prefix if necessary
            String staticAssetUrlPrefix = staticAssetService.getStaticAssetUrlPrefix();
            if (staticAssetUrlPrefix != null && !staticAssetUrlPrefix.startsWith("/")) {
                staticAssetUrlPrefix = "/" + staticAssetUrlPrefix;
            }
            if (staticAssetUrlPrefix == null) {
                staticAssetUrlPrefix = "";
            } else {
                imageUrl = staticAssetUrlPrefix + imageUrl;
            }
            
            MediaField mf = (MediaField) new MediaField()
                .withName("thumbnail")
                .withFriendlyName("Asset_thumbnail")
                .withFieldType(SupportedFieldType.IMAGE.toString())
                .withOrder(Integer.MIN_VALUE)
                .withValue(imageUrl);
            
            // Add a hidden field for the large thumbnail path
            record.getHiddenFields().add(new Field()
                    .withName("cmsUrlPrefix")
                    .withValue(staticAssetUrlPrefix));
                
            record.getHiddenFields().add(new Field()
                .withName("thumbnailKey")
                .withValue("?smallAdminThumbnail"));
            
            record.getHiddenFields().add(new Field()
                .withName("servletContext")
                .withValue(BroadleafRequestContext.getBroadleafRequestContext().getRequest().getContextPath()));
            
            // Set the height value on this field
            mf.setHeight(operationMap.getNamedOperations().get("smallAdminThumbnail").get("resize-height-amount"));
            record.getFields().add(mf);
            
            // Since we've added a new field, we need to clear the cached map to ensure it will display
            record.clearFieldMap();
        }
    }
}
