package org.broadleafcommerce.openadmin.web.controller;

import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.web.BroadleafWebRequestProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminBasicErrorController extends BasicErrorController {

    @Autowired
    @Qualifier("blAdminRequestProcessor")
    protected BroadleafWebRequestProcessor requestProcessor;

    public AdminBasicErrorController(final ErrorAttributes errorAttributes, final ErrorProperties errorProperties) {
        super(errorAttributes, errorProperties);
    }

    @RequestMapping(produces = "text/html")
    public ModelAndView errorHtml(final HttpServletRequest request,
                                  final HttpServletResponse response) {
        try {
            requestProcessor.process(new ServletWebRequest(request, response));
        } catch (Exception e) {
            throw ExceptionHelper.refineException(e);
        }

        return super.errorHtml(request, response);
    }

}
