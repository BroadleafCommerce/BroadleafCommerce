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

package org.broadleafcommerce.cms.web.processor;

import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.common.file.service.StaticAssetPathService;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafAttributeModifierProcessor;
import org.broadleafcommerce.presentation.model.BroadleafAttributeModifier;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * A Thymeleaf processor that processes the given url through the StaticAssetService's
 * {@link StaticAssetService#convertAssetPath(String, String, boolean)} method to determine
 * the appropriate URL for the asset to be served from.
 *
 * @author apazzolini
 */
@Component("blUrlRewriteProcessor")
@ConditionalOnTemplating
public class UrlRewriteProcessor extends AbstractBroadleafAttributeModifierProcessor {

    @Resource(name = "blStaticAssetPathService")
    protected StaticAssetPathService staticAssetPathService;

    @Override
    public String getName() {
        return "src";
    }

    @Override
    public int getPrecedence() {
        return 1000;
    }

    @Override
    public BroadleafAttributeModifier getModifiedAttributes(String tagName, Map<String, String> tagAttributes, String attributeName,
            String attributeValue, BroadleafTemplateContext context) {
        Map<String, String> newAttributes = new HashMap<>();
        newAttributes.put("src", getFullAssetPath(tagName, attributeValue, context));
        return new BroadleafAttributeModifier(newAttributes);
    }

    protected String getFullAssetPath(String tagName, String attributeValue, BroadleafTemplateContext context) {
        HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();
        boolean secureRequest = true;
        if (request != null) {
            secureRequest = isRequestSecure(request);
        }

        String assetPath = parsePath(attributeValue, context);

        String extension = getFileExtension(assetPath);
        if (isImageTag(tagName) && isAdminRequest() && !isImageExtension(extension)) {
            String defaultFileTypeImagePath = getDefaultFileTypeImagePath(extension);
            String queryString = getQueryString(assetPath);

            assetPath = parsePath(defaultFileTypeImagePath + queryString, context);
        }

        // We are forcing an evaluation of @{} from Thymeleaf above which will automatically add a contextPath,
        // no need to add it twice
        return staticAssetPathService.convertAssetPath(assetPath, null, secureRequest);
    }

    /**
     * @return true if the current request.scheme = HTTPS or if the request.isSecure value is true.
     */
    protected boolean isRequestSecure(HttpServletRequest request) {
        return ("HTTPS".equalsIgnoreCase(request.getScheme()) || request.isSecure());
    }

    protected boolean isImageTag(String tagName) {
        return "img".equals(tagName);
    }

    protected boolean isAdminRequest() {
        return BroadleafRequestContext.getBroadleafRequestContext().getAdmin();
    }

    protected Boolean isImageExtension(String extension) {
        String imageExtensions = BLCSystemProperty.resolveSystemProperty("admin.image.file.extensions");

        return imageExtensions.contains(extension);
    }

    protected String parsePath(String attributeValue, BroadleafTemplateContext context) {
        String newAttributeValue = attributeValue;
        if (newAttributeValue.startsWith("/")) {
            newAttributeValue = "@{ " + newAttributeValue + " }";
        }
        return (String) context.parseExpression(newAttributeValue);
    }

    protected String getFileExtension(String assetPath) {
        String extension;

        int extensionStartIndex = assetPath.lastIndexOf(".") + 1;
        int queryStartIndex = assetPath.lastIndexOf("?");
        if (queryStartIndex > 0) {
            extension = assetPath.substring(extensionStartIndex, queryStartIndex);
        } else {
            extension = assetPath.substring(extensionStartIndex);
        }

        return extension;
    }

    protected String getDefaultFileTypeImagePath(String extension) {
        String imageUrl;
        switch (extension) {
            case "txt":
                imageUrl = "/img/admin/file-txt.png";
                break;
            case "pdf":
                imageUrl = "/img/admin/file-pdf.png";
                break;
            case "doc":
            case "docx":
                imageUrl = "/img/admin/file-doc.png";
                break;
            case "xls":
            case "xlsx":
                imageUrl = "/img/admin/file-xls.png";
                break;
            case "ppt":
            case "pptx":
                imageUrl = "/img/admin/file-ppt.png";
                break;
            default:
                imageUrl = "/img/admin/file-unkn.png";
        }
        return imageUrl;
    }

    protected String getQueryString(String assetPath) {
        int queryStartIndex = assetPath.lastIndexOf("?");

        return (queryStartIndex > 0) ? assetPath.substring(queryStartIndex) : "";
    }
}
