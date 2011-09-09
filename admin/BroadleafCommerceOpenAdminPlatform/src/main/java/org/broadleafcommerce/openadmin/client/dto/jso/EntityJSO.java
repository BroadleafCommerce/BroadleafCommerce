package org.broadleafcommerce.openadmin.client.dto.jso;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/8/11
 * Time: 5:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class EntityJSO extends JavaScriptObject {

    public final native String getType() /*-{
        return this.type;
    }-*/;

    public final native String getError() /*-{
        return this.error;
    }-*/;

    public final native PropertyJSO[] getProperties() /*-{
        return this.properties;
    }-*/;

    public static final native EntityJSO buildEntity(String json) /*-{
        return eval('(' + json + ')');
    }-*/;
}
