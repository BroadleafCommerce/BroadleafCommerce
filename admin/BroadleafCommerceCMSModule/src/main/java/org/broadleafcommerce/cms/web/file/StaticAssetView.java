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

package org.broadleafcommerce.cms.web.file;


import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by jfischer
 */
public class StaticAssetView implements View {

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BufferedInputStream bis = null;
        try {
            String cacheFilePath = (String) model.get("cacheFilePath");
            String mimeType = (String) model.get("mimeType");
            response.setContentType(mimeType);
			response.setHeader("Cache-Control","no-cache");
	        response.setHeader("Pragma","no-cache");
	        response.setDateHeader ("Expires", 0);
			bis = new BufferedInputStream(new FileInputStream(cacheFilePath));
            OutputStream os = response.getOutputStream();
            boolean eof = false;
            while (!eof) {
                int temp = bis.read();
                if (temp < 0) {
                    eof = true;
                } else {
                    os.write(temp);
                }
            }
            os.flush();
		} catch (Exception e) {
			e.printStackTrace();
            throw e;
		} finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (Throwable e) {
                    //do nothing
                }
            }
        }
    }
}
