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


import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * Created by jfischer
 */
public class UploadCompletedView implements View {

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<html><head><script type=\"text/javascript\">");
        sb.append("window.top.");
        sb.append(model.get("callbackName"));
        sb.append("('(");
        if (model.get("error") != null) {
            sb.append(model.get("error"));
        } else {
            sb.append(model.get("result"));
        }
        sb.append(")');");
        sb.append("</script></head><body>Upload Completed</body></html>");

        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
        writer.write(sb.toString());
        writer.flush();
    }
}
