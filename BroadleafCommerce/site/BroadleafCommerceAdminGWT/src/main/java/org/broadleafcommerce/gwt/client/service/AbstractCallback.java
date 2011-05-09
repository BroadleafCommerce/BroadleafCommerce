package org.broadleafcommerce.gwt.client.service;

import org.broadleafcommerce.gwt.client.Main;

import com.google.gwt.core.client.GWT;
import com.gwtincubator.security.client.SecuredAsyncCallback;
import com.gwtincubator.security.exception.ApplicationSecurityException;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;

/**
 * @param <T>
 */
public abstract class AbstractCallback<T> extends SecuredAsyncCallback<T> {
    
    private Window boxComponent;// BoxComponent allows for Grid and LayoutContainer masking.

    public AbstractCallback() {
        this(null, null);
    }

    public AbstractCallback(Window boxComponent) {
        this(boxComponent, null);
    }

    public AbstractCallback(Window boxComponent, String maskMessage) {
        this.boxComponent = boxComponent;
        if (this.boxComponent != null && !this.boxComponent.getShowModalMask()) {
        	this.boxComponent.setShowModalMask(true);
        	this.boxComponent.setModalMaskOpacity(100);
            //this.boxComponent.mask(new LoadingMask(maskMessage).getHtml(),"waitingMask");
        }
    }

    public void onSuccess(T result) {
        if (Main.DEBUG && result != null) {
            GWT.log("Service call success:\n" + result.toString());
        }
        if (boxComponent != null) {
            boxComponent.hide();
        }
    }

    @Override
    protected void onOtherException(Throwable exception) {
        final String msg = "Service Exception";
        reportException(msg, exception);
        if (!Main.DEBUG) {
            String errorMsg = exception.getMessage();
            SC.warn(msg, errorMsg, null, null);
        }
    }

    @Override
    protected void onSecurityException(ApplicationSecurityException exception) {
        final String msg = "Security Exception";
        reportException(msg, exception);
        if (!Main.DEBUG) {
        	SC.warn(msg, "Your Profile doesn't have the Capability necessary to perform this task.", null, null);
        }
    }

    private void reportException(String msg, Throwable exception) {
        // if (Main.DEBUG) MessageBox.alert(msg, exception.getMessage(), null);
        GWT.log(msg, exception);
        if (boxComponent != null) {
            boxComponent.hide();
        }
    }
}
