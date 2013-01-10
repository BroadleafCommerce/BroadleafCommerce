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
package org.broadleafcommerce.admin.util.controllers;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.matthaynes.xml.dirlist.XmlDirectoryListing;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * @author ebautista
 *
 */
public class ListDirectoryController extends AbstractController {

    private String rootDirectory;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        XmlDirectoryListing dirs = new XmlDirectoryListing();
        response.setContentType("text/xml");
        dirs.setDateFormat("MMM DD, yyyy");

        String basepath = request.getPathTranslated().substring(0,request.getPathTranslated().indexOf("ls"));
        dirs.generateXmlDirectoryListing(new File(basepath + rootDirectory), response.getOutputStream());

        return null;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }
}
