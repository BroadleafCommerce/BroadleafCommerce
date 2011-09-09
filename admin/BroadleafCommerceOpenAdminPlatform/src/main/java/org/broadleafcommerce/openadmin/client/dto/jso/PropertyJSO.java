package org.broadleafcommerce.openadmin.client.dto.jso;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/8/11
 * Time: 5:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class PropertyJSO extends JavaScriptObject {

    public final native String getName() /*-{
        return this.name;
    }-*/;

    public final native String getValue() /*-{
        return this.value;
    }-*/;
    
}
