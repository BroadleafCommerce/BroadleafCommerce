package org.broadleafcommerce.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.Authentication;

public interface PostLoginObserver {

    public void process(HttpServletRequest request, HttpServletResponse response, Authentication authResult);
}
