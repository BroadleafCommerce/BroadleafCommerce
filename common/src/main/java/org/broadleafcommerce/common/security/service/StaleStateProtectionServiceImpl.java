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
package org.broadleafcommerce.common.security.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.security.RandomGenerator;
import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @see StaleStateProtectionService
 * @author jfischer
 */
@Service("blStaleStateProtectionService")
public class StaleStateProtectionServiceImpl implements StaleStateProtectionService {

    public static final String STATEVERSIONTOKEN = "stateVersionToken";
    public static final String STATEVERSIONTOKENPARAMETER = "stateVersionToken";
    private static final Log LOG = LogFactory.getLog(StaleStateProtectionServiceImpl.class);

    @Value("${stale.state.protection.enabled:false}")
    protected boolean staleStateProtectionEnabled = false;

    @Override
    public void compareToken(String passedToken) {
        if (staleStateProtectionEnabled) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            if (!getStateVersionToken().equals(passedToken) && request.getAttribute(getStateVersionTokenParameter()) == null) {
                throw new StaleStateServiceException("Page version token mismatch (" + passedToken + "). The request likely came from a stale page.");
            } else {
                request.setAttribute(getStateVersionTokenParameter(), "passed");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Validated page version token");
                }
            }
        }
    }

    @Override
    public String getStateVersionToken(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (BLCRequestUtils.isOKtoUseSession(new ServletWebRequest(request))) {
            HttpSession session = request.getSession();
            String token = (String) session.getAttribute(STATEVERSIONTOKEN);
            if (StringUtils.isEmpty(token)) {
                try {
                    token = RandomGenerator.generateRandomId("SHA1PRNG", 32);
                } catch (NoSuchAlgorithmException e) {
                    LOG.error("Unable to generate random number", e);
                    throw new RuntimeException("Unable to generate random number", e);
                }
                session.setAttribute(STATEVERSIONTOKEN, token);
            }
            return token;
        }
        return null;
    }

    @Override
    public void invalidateState() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (BLCRequestUtils.isOKtoUseSession(new ServletWebRequest(request))) {
            HttpSession session = request.getSession();
            session.removeAttribute(STATEVERSIONTOKEN);
        }
    }

    @Override
    public boolean isEnabled() {
        return staleStateProtectionEnabled;
    }

    @Override
    public String getStateVersionTokenParameter() {
        return STATEVERSIONTOKENPARAMETER;
    }
}
