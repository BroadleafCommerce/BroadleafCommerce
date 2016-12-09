package org.broadleafcommerce.core.web.geolocation;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component("blGeolocationFilter")
public class GeolocationFilter extends OncePerRequestFilter {

    @Resource(name="blGeolocationRequestProcessor")
    protected GeolocationRequestProcessor geolocationRequestProcessor;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        ServletWebRequest request = new ServletWebRequest(httpServletRequest, httpServletResponse);
        try {
            geolocationRequestProcessor.process(request);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            geolocationRequestProcessor.postProcess(request);
        }
    }
}

