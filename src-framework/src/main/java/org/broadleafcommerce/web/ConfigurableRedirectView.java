package org.broadleafcommerce.web;

import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ConfigurableRedirectView extends RedirectView {

	public ConfigurableRedirectView(String url) {
		super(url);
	}

    // Default to 302
    private int responseStatus = HttpServletResponse.SC_MOVED_TEMPORARILY;

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus( int responseStatus ) {
        this.responseStatus = responseStatus;
    }

    /**
	 * Send a redirect back to the HTTP client
	 * @param request current HTTP request (allows for reacting to request method)
	 * @param response current HTTP response (for sending response headers)
	 * @param targetUrl the target URL to redirect to
	 * @param http10Compatible whether to stay compatible with HTTP 1.0 clients
	 * @throws IOException if thrown by response methods
	 */
	protected void sendRedirect(
			HttpServletRequest request, HttpServletResponse response, String targetUrl, boolean http10Compatible)
			throws IOException {
        response.setStatus( http10Compatible?getResponseStatus():303 );
        response.setHeader( "Location", response.encodeRedirectURL(targetUrl) );
	}

}
