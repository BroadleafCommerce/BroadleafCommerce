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
