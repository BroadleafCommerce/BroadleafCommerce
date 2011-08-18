/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.openadmin.client.service;

import org.broadleafcommerce.openadmin.client.BLCMain;

import com.google.gwt.core.client.GWT;
import com.gwtincubator.security.client.SecuredAsyncCallback;
import com.gwtincubator.security.exception.ApplicationSecurityException;
import com.smartgwt.client.util.SC;

/**
 * @author jfischer
 * 
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
