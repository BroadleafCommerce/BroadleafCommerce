package org.broadleafcommerce.email.web;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.email.service.EmailTrackingManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 
 * @author jfischer
 *
 */
public class EmailOpenTrackingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Log LOG = LogFactory.getLog(EmailOpenTrackingServlet.class);

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String url = request.getPathInfo();
        Long emailId = null;
        String imageUrl = "";

        // Parse the URL for the Email ID and Image URL
        if (url != null) {
            String[] items = url.split("/");
            emailId = Long.valueOf(items[1]);
            StringBuffer sb = new StringBuffer();
            for (int j = 2; j < items.length; j++) {
                sb.append("/");
                sb.append(items[j]);
            }
            imageUrl = sb.toString();
        }

        // Record the open
        if (emailId != null && !"null".equals(emailId)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("service() => Recording Open for Email[" + emailId + "]");
            }
            WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
            EmailTrackingManager emailTrackingManager = (EmailTrackingManager) context.getBean("emailTrackingManagerBLC");
            String userAgent = request.getHeader("USER-AGENT");
            Map<String, String> extraValues = new HashMap<String, String>();
            extraValues.put("userAgent", userAgent);
            emailTrackingManager.recordOpen(emailId, extraValues);
        }

        if ("".equals(imageUrl)) {
            response.setContentType("image/gif");
            BufferedInputStream bis = null;
            OutputStream out = response.getOutputStream();
            try {
                bis = new BufferedInputStream(EmailOpenTrackingServlet.class.getResourceAsStream("clear_dot.gif"));
                boolean eof = false;
                while (!eof) {
                    int temp = bis.read();
                    if (temp == -1) {
                        eof = true;
                    } else {
                        out.write(temp);
                    }
                }
            } finally {
                if (bis != null) {
                    try{ bis.close(); } catch (Throwable e) {}
                }
                //Don't close the output stream controlled by the container. The container will
                //handle this.
            }
        } else {
            RequestDispatcher dispatcher = request.getRequestDispatcher(imageUrl);
            dispatcher.forward(request, response);
        }
    }
}
