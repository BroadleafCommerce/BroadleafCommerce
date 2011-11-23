/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public abstract class AjaxFormController extends SimpleFormController {
    
    protected abstract void populateAjax(Map<String,Object> model, Object object);
    protected abstract void populateStandard(Map<String,Object> model, Object object);
 
    private String ajaxView;
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command, BindException errors)
            throws Exception {
        Map<String,Object> map = new HashMap<String,Object>();
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
