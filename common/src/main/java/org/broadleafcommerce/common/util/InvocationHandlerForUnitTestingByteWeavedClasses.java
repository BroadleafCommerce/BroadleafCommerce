package org.broadleafcommerce.common.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Invocation handler for unit testing byte-weaved classes.  When Spring is unavailable to complete byte-weaving during
 * unit testing, this invocation handler will invoke a chosen method against an array of Objects that are meant to be
 * byte-weaved together.  The invocation handler will return when the first object is found that can be successfully used
 * with the chosen method.
 * 
 * @author Joshua Skorton (jskorton)
 */
public class InvocationHandlerForUnitTestingByteWeavedClasses implements InvocationHandler {

    protected List<Object> objectsForByteWeaving = new ArrayList<Object>();

    public InvocationHandlerForUnitTestingByteWeavedClasses(List<Object> objectsForByteWeaving) {
        this.objectsForByteWeaving = objectsForByteWeaving;
    }

    /**
     * When Spring is unavailable to complete byte-weaving during unit testing, this invocation handler will invoke a chosen
     * method against an array of Objects that are meant to be byte-weaved together.  The invocation handler will return when
     * the first object is found that can be successfully used with the chosen method.  If no objects are found to work with
     * the chosen method, null will be returned.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        
        for (Object object : objectsForByteWeaving) {
            try {
                return method.invoke(object, args);
            } catch (IllegalArgumentException exception) {
                continue;
            }
        }

        return null;
    }
    
    /**
     * Returns a list of Objects that are meant to be byte-weaved.  The invoke method will attempt to run against this list.
     * 
     * @return
     */
    public List<Object> getObjectsForByteWeaving() {
        return objectsForByteWeaving;
    }

    /**
     * Sets a list of Objects that are meant to be byte-weaved.  The invoke method will attempt to run against this list.
     * 
     * @param objects
     */
    public void setObjectsForByteWeaving(List<Object> objects) {
        this.objectsForByteWeaving = objects;
    }

}
