package org.broadleafcommerce.common.web.filter;

import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;


public class GeneralIgnoreFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        BLCRequestUtils.setIsFilteringIgnoredForUri(new ServletWebRequest((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse), Boolean.TRUE);
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
