/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.cms.admin.server.persistence.provider;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.FieldPersistenceProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.FieldPersistenceProviderAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.FieldProviderResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

/**
 * @author bpolster
 */
@Component("blHTMLFieldPersistenceProvider")
@Scope("prototype")
public class HTMLFieldPersistenceProvider extends FieldPersistenceProviderAdapter {

    @Value("${asset.server.url.prefix.internal}")
    protected String staticAssetUrlPrefix;

    protected boolean canHandlePersistence(PopulateValueRequest populateValueRequest, Serializable instance) {
        return populateValueRequest.getMetadata().getFieldType() == SupportedFieldType.HTML ||
                populateValueRequest.getMetadata().getFieldType() == SupportedFieldType.HTML_BASIC;
    }

    protected boolean canHandleExtraction(ExtractValueRequest extractValueRequest, Property property) {
        return extractValueRequest.getMetadata().getFieldType() == SupportedFieldType.HTML ||
                extractValueRequest.getMetadata().getFieldType() == SupportedFieldType.HTML_BASIC;
    }

    @Override
    public FieldProviderResponse populateValue(PopulateValueRequest populateValueRequest, Serializable instance) throws PersistenceException {
        if (!canHandlePersistence(populateValueRequest, instance)) {
            return FieldProviderResponse.NOT_HANDLED;
        }

        try {
            String requestedValue = populateValueRequest.getRequestedValue();
            String fixedValue = fixAssetPathsForStorage(requestedValue);

            boolean dirty = checkDirtyState(populateValueRequest, instance, fixedValue);
            populateValueRequest.getProperty().setIsDirty(dirty);

            populateValueRequest.getFieldManager().setFieldValue(instance,
                    populateValueRequest.getProperty().getName(), fixedValue);

        } catch (Exception e) {
            throw new PersistenceException(e);
        }
        return FieldProviderResponse.HANDLED_BREAK;
    }

    @Override
    public FieldProviderResponse extractValue(ExtractValueRequest extractValueRequest, Property property) throws PersistenceException {
        if (!canHandleExtraction(extractValueRequest, property)) {
            return FieldProviderResponse.NOT_HANDLED;
        }

        if (extractValueRequest.getRequestedValue() != null) {
            String val = extractValueRequest.getRequestedValue().toString();
            if (val != null) {
                if (val.contains(staticAssetUrlPrefix)) {
                    val = fixAssetPathsForDisplay(val);
                }
            }
            property.setValue(val);
            property.setDisplayValue(extractValueRequest.getDisplayVal());
        }
        return FieldProviderResponse.HANDLED_BREAK;
    }

    /**
     * Stores the image paths at the root (e.g. no Servlet Context).   
     * @param val
     * @return
     */
    public String fixAssetPathsForStorage(String val) {
        if (staticAssetUrlPrefix != null) {
            String tmpPrefix = staticAssetUrlPrefix;
            if (tmpPrefix.startsWith("/")) {
                tmpPrefix = tmpPrefix.substring(1);
            }
            return val.replaceAll("(?<=src=\").*?(?=" + tmpPrefix + ")", "");
        }
        return val;
    }

    /**
     * 
     * @param val
     * @return
     */
    public String fixAssetPathsForDisplay(String val) {
        String contextPath = "/";
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc != null) {                        
            HttpServletRequest request = brc.getRequest();
            if (request != null) {
                contextPath = request.getContextPath();
            }
        }
        
        if (!contextPath.endsWith("/")) {
            contextPath = contextPath + "/";
        }
        
        if (staticAssetUrlPrefix != null) {
            String tmpPrefix = staticAssetUrlPrefix;
            if (tmpPrefix.startsWith("/")) {
                tmpPrefix = tmpPrefix.substring(1);
            }
            return val.replaceAll("(?<=src=\").*?(?=" + tmpPrefix + ")", contextPath);
        }
        
        return val;
    }

    @Override
    public int getOrder() {
        return FieldPersistenceProvider.HTML;
    }

}
