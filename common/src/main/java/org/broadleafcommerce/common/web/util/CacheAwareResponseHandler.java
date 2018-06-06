/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.common.web.util;

import org.broadleafcommerce.common.web.resource.BroadleafResourceHttpRequestHandler;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * Utility class that sets the correct response headers (especially browser cache related headers) using the existing
 * Spring request pipeline configuration by mimetype. This is useful when circumventing Spring request handling for
 * static files.
 *
 * @author Jeff Fischer
 */
public class CacheAwareResponseHandler extends BroadleafResourceHttpRequestHandler {

    public void setHeaders(HttpServletResponse response, File targetFile, String mimeType) throws IOException {
        super.prepareResponse(response);
        Resource resource = new FileSystemResource(targetFile);
        MediaType mediaType = MediaType.parseMediaType(mimeType);
        super.setHeaders(response, resource, mediaType);
    }

}
