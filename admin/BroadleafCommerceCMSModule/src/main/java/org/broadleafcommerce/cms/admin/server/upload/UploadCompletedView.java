package org.broadleafcommerce.cms.admin.server.upload;


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
        sb.append("();");
        sb.append("</script></head><body></body></html>");

        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
        writer.write(sb.toString());
        writer.flush();
    }
}
