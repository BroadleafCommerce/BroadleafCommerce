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
package org.broadleafcommerce.profile.web.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

public class BroadleafLogoutHandler extends SecurityContextLogoutHandler {

    private final List<PreLogoutObserver> preLogoutListeners = new ArrayList<PreLogoutObserver>();

    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        notifyPreLogoutListeners(request, response, authentication);
        super.logout(request, response, authentication);
    }

    public void addPreLogoutListener(PreLogoutObserver preLogoutObserver) {
        this.preLogoutListeners.add(preLogoutObserver);
    }

    public void removePreLogoutListener(PreLogoutObserver preLogoutObserver) {
        if (this.preLogoutListeners.contains(preLogoutObserver)) {
            this.preLogoutListeners.remove(preLogoutObserver);
        }
    }

    public void notifyPreLogoutListeners(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        for (Iterator<PreLogoutObserver> iter = preLogoutListeners.iterator(); iter.hasNext();) {
            PreLogoutObserver listener = iter.next();
            listener.processPreLogout(request, response, authResult);
        }
    }
}
