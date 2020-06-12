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
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressions;

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
public class UrlRewriteProcessor extends AbstractAttributeModifierAttrProcessor {
    
    @Resource(name = "blStaticAssetPathService")
    protected StaticAssetPathService staticAssetPathService;

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public UrlRewriteProcessor() {
        this("src");
    }
    
    protected UrlRewriteProcessor(final String attributeName) {
        super(attributeName);
    }

    @Override
    public int getPrecedence() {
        return 1000;
    }
    
    /**
     * @return true if the current request.scheme = HTTPS or if the request.isSecure value is true.
     */
    protected boolean isRequestSecure(HttpServletRequest request) {
        return ("HTTPS".equalsIgnoreCase(request.getScheme()) || request.isSecure());
    } 

    
    @Override
    protected Map<String, String> getModifiedAttributeValues(Arguments arguments, Element element, String attributeName) {
        Map<String, String> attrs = new HashMap<String, String>();
        HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();
        
        boolean secureRequest = true;
        if (request != null) {
            secureRequest = isRequestSecure(request);
        }
        
        String elementValue = element.getAttributeValue(attributeName);

        if (elementValue.startsWith("/")) {
            elementValue = "@{ " + elementValue + " }";
        }
        Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                .parseExpression(arguments.getConfiguration(), arguments, elementValue);
        String assetPath = (String) expression.execute(arguments.getConfiguration(), arguments);

        // If this is an image tag, we want to fallback to default images for non-image files
        if ("img".equals(element.getOriginalName()) && BroadleafRequestContext.getBroadleafRequestContext().getAdmin()) {
            assetPath = getAssetImagePath(assetPath, arguments);
        }

        // We are forcing an evaluation of @{} from Thymeleaf above which will automatically add a contextPath, no need to
        // add it twice
        assetPath = staticAssetPathService.convertAssetPath(assetPath, null, secureRequest);
        
        attrs.put("src", assetPath);
        
        return attrs;
    }

    protected String getAssetImagePath(String assetPath, Arguments arguments) {
        Expression expression;
        String extension;
        int extensionStartIndex = assetPath.lastIndexOf(".") + 1;
        int queryStartIndex = assetPath.lastIndexOf("?");
        if (queryStartIndex > 0) {
            extension = assetPath.substring(extensionStartIndex, queryStartIndex);
        } else {
            extension = assetPath.substring(extensionStartIndex);
        }

        String queryString = (queryStartIndex > 0) ? assetPath.substring(queryStartIndex) : "";
        if (!isImageExtension(extension)) {
            assetPath = getDefaultFileTypeImage(extension);
        } else {
            return assetPath;
        }

        // Add the query string back to the url
        assetPath = "@{ " + assetPath + queryString + " }";

        // Process and return the new image path
        expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                .parseExpression(arguments.getConfiguration(), arguments, assetPath);
        return (String) expression.execute(arguments.getConfiguration(), arguments);
    }

    @Override
    protected ModificationType getModificationType(Arguments arguments, Element element, String attributeName, String newAttributeName) {
        return ModificationType.SUBSTITUTION;
    }

    @Override
    protected boolean removeAttributeIfEmpty(Arguments arguments, Element element, String attributeName, String newAttributeName) {
        return true;
    }

    @Override
    protected boolean recomputeProcessorsAfterExecution(Arguments arguments, Element element, String attributeName) {
        return false;
    }

    protected Boolean isImageExtension(String extension) {
        String imageExtensions = BLCSystemProperty.resolveSystemProperty("admin.image.file.extensions");
        if (imageExtensions.contains(extension)) {
            return true;
        }
        return false;
    }

    protected String getDefaultFileTypeImage(String extension) {
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
}
