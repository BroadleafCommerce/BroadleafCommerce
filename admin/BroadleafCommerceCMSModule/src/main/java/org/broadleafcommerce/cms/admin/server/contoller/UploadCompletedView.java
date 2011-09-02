package org.broadleafcommerce.cms.admin.server.contoller;


import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        sb.append("<script type=\"text/javascript\">");
        sb.append("window.top.");
        sb.append(model.get("callbackName"));
        sb.append("();");
        sb.append("</script>");
    }
}
