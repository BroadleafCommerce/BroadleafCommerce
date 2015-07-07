/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.security.util;

import java.io.Serializable;

/**
 * 
 * @author jfischer
 *
 */
public class PasswordReset implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String email;
    private boolean passwordChangeRequired = false;
    private int passwordLength = 22;
    private boolean sendResetEmailReliableAsync = false;

    public PasswordReset() {
    }

    public PasswordReset(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getPasswordChangeRequired() {
        return passwordChangeRequired;
    }

    public void setPasswordChangeRequired(boolean passwordChangeRequired) {
        this.passwordChangeRequired = passwordChangeRequired;
    }

    public int getPasswordLength() {
        return passwordLength;
    }

    public void setPasswordLength(int passwordLength) {
        this.passwordLength = passwordLength;
    }

    public boolean isSendResetEmailReliableAsync() {
        return sendResetEmailReliableAsync;
    }

    public void setSendResetEmailReliableAsync(boolean sendResetEmailReliableAsync) {
        this.sendResetEmailReliableAsync = sendResetEmailReliableAsync;
    }
}
