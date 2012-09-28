/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.common.web.util;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.util.Collection;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.detector.ExtensionMimeDetector;
import eu.medsea.mimeutil.detector.MagicMimeMimeDetector;
import org.apache.commons.lang.StringUtils;

/**
 * @author Jeff Fischer
 */
public class PrecompressedHttpServletResponse extends HttpServletResponseWrapper {

    static {
        MimeUtil.registerMimeDetector(ExtensionMimeDetector.class.getName());
        MimeUtil.registerMimeDetector(MagicMimeMimeDetector.class.getName());
    }

    private final String originalPath;
    private final String contentType;

    public PrecompressedHttpServletResponse(HttpServletResponse response, String originalPath) {
        super(response);
        this.originalPath = originalPath;
        Collection mimeTypes = MimeUtil.getMimeTypes(originalPath);
        if (!mimeTypes.isEmpty()) {
            MimeType mimeType = (MimeType) mimeTypes.iterator().next();
            contentType = mimeType.toString();
        } else {
            contentType = null;
        }
    }

    @Override
    public String getContentType() {
        String contentType = super.getContentType();
        if (!StringUtils.isEmpty(contentType) && !StringUtils.isEmpty(this.contentType) && contentType.contains("zip")) {
            return this.contentType;
        }

        return contentType;
    }

    @Override
    public void setContentType(String type) {
        if (type.contains("zip") && this.contentType != null) {
            super.setContentType(this.contentType);
        } else {
            super.setContentType(type);
        }
    }
}
