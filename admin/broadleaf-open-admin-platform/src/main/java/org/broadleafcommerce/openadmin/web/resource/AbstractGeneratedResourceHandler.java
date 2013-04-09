package org.broadleafcommerce.openadmin.web.resource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.broadleafcommerce.common.resource.GeneratedResource;
import org.springframework.core.io.Resource;

import javax.servlet.http.HttpServletRequest;


/**
 * An abstract GeneratedResourceHandler that is capable of responding to a single specified filename and generate
 * contents for that filename. This abstract parent will handle caching of the generated resource.
 * 
 * @author Andre Azzolini (apazzolini)
 *
 */
public abstract class AbstractGeneratedResourceHandler {
    
    protected Cache generatedResourceCache;

    /**
     * Attempts to retrive the requested resource from cache. If not cached, generates the resource, caches it,
     * and then returns it
     * 
     * @param request
     * @return the generated resource
     */
    public Resource getResource(HttpServletRequest request) {
        String filename = getHandledFileName();
        
        Element e = getGeneratedResourceCache().get(filename);
        if (e == null || e.getValue() == null) {
            String contents = getFileContents();
            GeneratedResource r = new GeneratedResource(contents.getBytes(), filename);
            e = new Element(filename,  r);
            getGeneratedResourceCache().put(e);
        }
        
        return (Resource) e.getObjectValue();
    }
    
    /**
     * For example, if the application is deployed under the "test" context and you want to handle the request 
     * for http://localhost/test/js/myFile.js, this method should return the String "/js/myFile.js".
     * 
     * @return the servlet-context-based file name to handle. 
     */
    public abstract String getHandledFileName();
    
    /**
     * @return the String representation of the contents of this generated file
     */
    public abstract String getFileContents();
    
    protected Cache getGeneratedResourceCache() {
        if (generatedResourceCache == null) {
            generatedResourceCache = CacheManager.getInstance().getCache("generatedResourceCache");
        }
        return generatedResourceCache;
    }
    
}
