/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.cms.admin.server.persistence.provider;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.FieldPersistenceProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.FieldPersistenceProviderAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
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
    public MetadataProviderResponse populateValue(PopulateValueRequest populateValueRequest, Serializable instance) throws PersistenceException {
        if (!canHandlePersistence(populateValueRequest, instance)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }

        try {
            String requestedValue = populateValueRequest.getRequestedValue();
            String fixedValue = fixAssetPathsForStorage(requestedValue);

            boolean dirty;
            if (populateValueRequest.getProperty().getIsDirty()) {
                dirty = true;
            } else {
                dirty = checkDirtyState(populateValueRequest, instance, fixedValue);
            }

            populateValueRequest.getProperty().setIsDirty(dirty);

            populateValueRequest.getFieldManager().setFieldValue(instance,
                    populateValueRequest.getProperty().getName(), fixedValue);

        } catch (Exception e) {
            throw new PersistenceException(e);
        }
        return MetadataProviderResponse.HANDLED_BREAK;
    }

    @Override
    public MetadataProviderResponse extractValue(ExtractValueRequest extractValueRequest, Property property) throws PersistenceException {
        if (!canHandleExtraction(extractValueRequest, property)) {
            return MetadataProviderResponse.NOT_HANDLED;
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
        return MetadataProviderResponse.HANDLED_BREAK;
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
