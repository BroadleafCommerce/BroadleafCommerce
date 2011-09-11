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
        sb.append("(eval('(");
        if (model.get("error") != null) {
            sb.append(model.get("error"));
        } else {
            sb.append(model.get("result"));
        }
        sb.append(")'));");
        sb.append("</script></head><body>Upload Completed</body></html>");

        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
        writer.write(sb.toString());
        writer.flush();
    }
}
