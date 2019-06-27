package org.broadleafcommerce.common.web;

import org.broadleafcommerce.common.admin.condition.ConditionalOnNotAdmin;
import org.broadleafcommerce.common.site.domain.Theme;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

@Component
@ConditionalOnNotAdmin
public class ThemeUrlEncodingFilter extends ResourceUrlEncodingFilter {

    @Resource(name = "blThemeResolver")
    protected BroadleafThemeResolver themeResolver;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest)request;
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            filterChain.doFilter(httpRequest, new ThemeUrlEncodingFilter.ResourceUrlEncodingResponseWrapper(httpResponse, themeResolver));
        } else {
            throw new ServletException("ThemeUrlEncodingFilter just supports HTTP requests");
        }
    }

    private static class ResourceUrlEncodingResponseWrapper extends HttpServletResponseWrapper {

        private final BroadleafThemeResolver themeResolver;

        public ResourceUrlEncodingResponseWrapper(HttpServletResponse wrapped, BroadleafThemeResolver themeResolver) {
            super(wrapped);
            this.themeResolver = themeResolver;
        }

        public String encodeURL(String url) {
            BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
            Theme theme = this.themeResolver.resolveTheme(brc.getWebRequest());

            if (url.contains(".js") || url.contains(".css")) {
                return url + "?themeConfigId=" + theme.getId();
            }

            return url;
        }
    }
}
