package org.broadleafcommerce.openadmin.web.handler;

import org.broadleafcommerce.common.web.controller.BroadleafControllerUtility;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AdminMappingExceptionResolver extends SimpleMappingExceptionResolver {
    
    protected boolean showDebugMessage = false;
    
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, 
            Exception ex) {
        if (BroadleafControllerUtility.isAjaxRequest(request)) {
            // Set up some basic response attributes
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ModelAndView mav = new ModelAndView("utility/blcException");
            
            // Friendly message
            mav.addObject("exceptionMessage", ex.getMessage());
            
            showDebugMessage = false;
            mav.addObject("showDebugMessage", showDebugMessage);
            if (showDebugMessage) {
                StringBuilder sb2 = new StringBuilder();
                appendStackTrace(ex, sb2);
                mav.addObject("debugMessage", sb2.toString());
            }
            
            // Add the message to the model so we can render it 
            return mav;
        } else {
            return super.resolveException(request, response, handler, ex);
        }
    }
    
    /**
     * By default, appends the exception and its message followed by the file location that triggered this exception.
     * Recursively builds this out for each cause of the given exception.
     * 
     * @param throwable
     * @param sb
     */
    protected void appendStackTrace(Throwable throwable, StringBuilder sb) {
        if (throwable == null) {
            return;
        }
        
        StackTraceElement[] st = throwable.getStackTrace();
        if (st != null && st.length > 0) {
            sb.append("\r\n\r\n");
            sb.append(throwable.toString());
            sb.append("\r\n");
            sb.append(st[0].toString());
        }
        
        appendStackTrace(throwable.getCause(), sb);
    }

    public boolean isShowDebugMessage() {
        return showDebugMessage;
    }
    
    public void setShowDebugMessage(boolean showDebugMessage) {
        this.showDebugMessage = showDebugMessage;
    }

}
