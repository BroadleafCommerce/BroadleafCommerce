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

package org.broadleafcommerce.openadmin.server.service.artifact.upload;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.springframework.web.servlet.View;

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
        sb.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">");
        sb.append("<head>");
        sb.append("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
        sb.append("<META HTTP -EQUIV=\"Expires\" CONTENT=\"-1\">");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />");
        sb.append("<meta http-equiv=\"Content- Language\" content=\"en-us\" />");
        sb.append("<meta http-equiv=\"Content-Author\" content=\"Broadleaf Commerce\" />");
        sb.append("<script type=\"text/javascript\">");
        sb.append("window.top.");
        sb.append(model.get("callbackName"));
        sb.append("('(");
        if (model.get("error") != null) {
            sb.append(model.get("error"));
        } else {
            sb.append(model.get("result"));
        }
        sb.append(")');");
        sb.append("</script>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("Upload Completed");
        sb.append("</body>");
        sb.append("</html>");

        response.setContentType("text/html");
        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
        writer.write(sb.toString());
        writer.flush();
    }
}
