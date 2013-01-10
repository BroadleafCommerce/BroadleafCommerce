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

import org.broadleafcommerce.admin.util.domain.DirectoryFileBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * @author ebautista
 *
 */
public class MakeDirectoryController extends SimpleFormController {

    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

        DirectoryFileBean bean = (DirectoryFileBean) command;
        checkDirectory(bean.getAbsolutePath() + File.separator + bean.getName());

        response.setStatus(HttpServletResponse.SC_OK);
        return super.onSubmit(request, response, command, errors);
    }

    private void checkDirectory(String basepath) {
        FileSystemResource dirResource = new FileSystemResource(basepath);
        if(!dirResource.exists()) {
            File f = new File(basepath);
            if(!f.mkdirs()){
                throw new RuntimeException("Could not create directories "+f.getAbsolutePath());
            }

        }
    }
}
