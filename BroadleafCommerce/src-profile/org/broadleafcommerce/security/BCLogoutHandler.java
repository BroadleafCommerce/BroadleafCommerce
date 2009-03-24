package org.broadleafcommerce.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.Authentication;
import org.springframework.security.ui.logout.SecurityContextLogoutHandler;

public class BCLogoutHandler extends SecurityContextLogoutHandler {

    private final List<PreLogoutObserver> preLogoutListeners = new ArrayList<PreLogoutObserver>();

    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        notifyListeners(request, response, authentication);
        super.logout(request, response, authentication);
    }

    public void addListener(PreLogoutObserver preLogoutObserver) {
        this.preLogoutListeners.add(preLogoutObserver);
    }

    public void removeListener(PreLogoutObserver preLogoutObserver) {
        if (this.preLogoutListeners.contains(preLogoutObserver)) {
            this.preLogoutListeners.remove(preLogoutObserver);
        }
    }

    public void notifyListeners(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        for (Iterator<PreLogoutObserver> iter = preLogoutListeners.iterator(); iter.hasNext();) {
            PreLogoutObserver listener = iter.next();
            listener.process(request, response, authResult);
        }
    }
}
