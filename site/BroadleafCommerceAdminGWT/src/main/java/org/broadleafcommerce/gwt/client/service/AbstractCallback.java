package org.broadleafcommerce.gwt.client.service;

import org.broadleafcommerce.gwt.client.Main;

import com.google.gwt.core.client.GWT;
import com.gwtincubator.security.client.SecuredAsyncCallback;
import com.gwtincubator.security.exception.ApplicationSecurityException;
import com.smartgwt.client.util.SC;

/**
 * @param <T>
 */
public abstract class AbstractCallback<T> extends SecuredAsyncCallback<T> {

    public void onSuccess(T result) {
        if (Main.DEBUG && result != null) {
            GWT.log("Service call success:\n" + result.toString());
        }
        if (Main.MODAL_PROGRESS.isActive()) {
        	Main.MODAL_PROGRESS.stopProgress();
        }
        if (Main.NON_MODAL_PROGRESS.isActive()) {
        	Main.NON_MODAL_PROGRESS.stopProgress();
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
        if (Main.MODAL_PROGRESS.isActive()) {
        	Main.MODAL_PROGRESS.stopProgress();
        }
        if (Main.NON_MODAL_PROGRESS.isActive()) {
        	Main.NON_MODAL_PROGRESS.stopProgress();
        }
    }
}
