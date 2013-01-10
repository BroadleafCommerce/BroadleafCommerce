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

package org.broadleafcommerce.openadmin.server.service.artifact.upload;

import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/8/11
 * Time: 5:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadedFile {

    private static final ThreadLocal<Map<String, MultipartFile>> upload = new ThreadLocal<Map<String, MultipartFile>>();

    public static Map<String, MultipartFile> getUpload() {
        Map<String, MultipartFile> response = UploadedFile.upload.get();
        if (response == null) {
            return new HashMap<String, MultipartFile>();
        }
        return response;
    }

    public static void setUpload(Map<String, MultipartFile> upload) {
        UploadedFile.upload.set(upload);
    }

    public static void remove() {
        UploadedFile.upload.remove();
    }

}
