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
