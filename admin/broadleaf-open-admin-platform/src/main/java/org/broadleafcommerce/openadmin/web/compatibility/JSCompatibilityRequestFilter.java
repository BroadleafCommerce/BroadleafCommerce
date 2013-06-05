package org.broadleafcommerce.openadmin.web.compatibility;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Jeff Fischer
 */
@Component("blJSCompatibilityRequestFilter")
public class JSCompatibilityRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain
            filterChain) throws ServletException, IOException {
        filterChain.doFilter(new JSCompatibilityRequestWrapper(request), response);
    }

}
