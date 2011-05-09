package org.broadleafcommerce.gwt.client.service;

import org.broadleafcommerce.gwt.client.BLCMain;

import com.google.gwt.core.client.GWT;
import com.gwtincubator.security.client.SecuredAsyncCallback;
import com.gwtincubator.security.exception.ApplicationSecurityException;
import com.smartgwt.client.util.SC;

/**
 * @param <T>
 */
public abstract class AbstractCallback<T> extends SecuredAsyncCallback<T> {

    public void onSuccess(T result) {
        if (BLCMain.DEBUG && result != null) {
            GWT.log("Service call success:\n" + result.toString());
        }
        if (BLCMain.NON_MODAL_PROGRESS.isActive()) {
        	BLCMain.NON_MODAL_PROGRESS.stopProgress();
        }
    }

    @Override
    protected void onOtherException(Throwable exception) {
        final String msg = "Service Exception";
        reportException(msg, exception);
        String errorMsg = exception.getMessage();
        SC.warn(errorMsg);
    }

    @Override
    protected void onSecurityException(ApplicationSecurityException exception) {
        final String msg = "Security Exception";
        reportException(msg, exception);
        SC.warn("Your Profile doesn't have the Capability necessary to perform this task.");
    }

    private void reportException(String msg, Throwable exception) {
        //if (BLCMain.DEBUG) MessageBox.alert(msg, exception.getMessage(), null);
        GWT.log(msg, exception);
        if (BLCMain.MODAL_PROGRESS.isActive()) {
        	BLCMain.MODAL_PROGRESS.stopProgress();
        }
        if (BLCMain.NON_MODAL_PROGRESS.isActive()) {
        	BLCMain.NON_MODAL_PROGRESS.stopProgress();
        }
    }
}
