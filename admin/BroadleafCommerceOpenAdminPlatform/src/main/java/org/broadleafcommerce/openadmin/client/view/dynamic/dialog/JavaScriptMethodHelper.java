package org.broadleafcommerce.openadmin.client.view.dynamic.dialog;

/**
 * Created by jfischer
 */
public class JavaScriptMethodHelper {

    private static int requestCounter = 0;

    public static String registerCallbackFunction(JavaScriptMethodCallback callback) {
        String callbackName = "callback" + (requestCounter++);
        createCallbackFunction(callback, callbackName);
        return callbackName;
    }

    private native static void createCallbackFunction(JavaScriptMethodCallback obj, String callbackName )/*-{
        tmpcallback = function( j ){
            obj.@org.broadleafcommerce.openadmin.client.view.dynamic.dialog.JavaScriptMethodCallback::execute(Lcom/google/gwt/core/client/JavaScriptObject;)( j );
        };
        $wnd[callbackName]=tmpcallback;
    }-*/;

}
