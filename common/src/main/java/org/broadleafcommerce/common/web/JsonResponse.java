package org.broadleafcommerce.common.web;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;


/**
 * Used when a controller typically returns a String that represents a view path but would like to return a
 * JSON response in other scenarios, such as an error case.
 * 
 * Example Usage:
 * 
 * return new JsonResponse(response)
 *     .with("status", "ok")
 *     .with("shouldRefresh", true)
 *     .done();
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class JsonResponse {
    
    protected Map<String, Object> map = new HashMap<String, Object>();
    protected HttpServletResponse response;
    
    public JsonResponse(HttpServletResponse response) {
        this.response = response;
    }
    
    public JsonResponse with(String key, Object value) {
        map.put(key, value);
        return this;
    }
    
    public String done() {
        response.setHeader("Content-Type", "application/json");
        try {
            new ObjectMapper().writeValue(response.getWriter(), map);
        } catch (Exception e) {
            throw new RuntimeException("Could not serialize JSON", e);
        }
        return null;
    }

}
