package org.broadleafcommerce.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public abstract class AjaxFormController extends SimpleFormController {
    
    protected abstract void populateAjax(Map<Object,Object> model, Object object);
    protected abstract void populateStandard(Map<Object,Object> model, Object object);
 
    private String ajaxView;
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command, BindException errors)
            throws Exception {
        Map<Object,Object> map = new HashMap<Object,Object>();
        String view;
        if (((AjaxFormCommandObject)command).isAjaxRequest()) {
            populateAjax(map, command);
            view = getAjaxView();
        } else {
            populateStandard(map, command);
            view = getSuccessView();
        }
        return new ModelAndView(view, map);
    }
    
    public String getAjaxView() {
        return ajaxView;
    }

    public void setAjaxView(String ajaxView) {
        this.ajaxView = ajaxView;
    }
    
    @Override
    protected boolean isFormSubmission(HttpServletRequest request) {
        return true;
    }
    
    
}
