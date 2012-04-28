/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.common.web.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author Jeff Fischer
 */
public class PrecompressedHttpServletRequest extends HttpServletRequestWrapper {

    private final String regularPath;
    private final String compressedPath;

    public PrecompressedHttpServletRequest(HttpServletRequest request, String regularPath, String compressedPath) {
        super(request);
        this.compressedPath = compressedPath;
        this.regularPath = regularPath;
    }

    private String translateString(String oldValue) {
        if (oldValue.endsWith(regularPath)) {
            return oldValue.substring(0, oldValue.indexOf(regularPath)) + compressedPath;
        }
        return oldValue;
    }

    @Override
    public String getServletPath() {
        return translateString(super.getServletPath());
    }

    @Override
    public String getPathTranslated() {
        return translateString(super.getPathTranslated());
    }

    @Override
    public String getRequestURI() {
        return translateString(super.getRequestURI());
    }

    @Override
    public StringBuffer getRequestURL() {
        String temp = super.getRequestURL().toString();
        temp = translateString(temp);
        return new StringBuffer(temp);
    }

}
