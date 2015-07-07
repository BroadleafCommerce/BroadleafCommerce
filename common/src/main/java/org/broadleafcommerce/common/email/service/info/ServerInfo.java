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
package org.broadleafcommerce.common.email.service.info;

import java.io.Serializable;

/**
 * @author jfischer
 *
 */
public class ServerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serverName;
    private Integer serverPort;
    private Integer securePort;
    private String appName;

    public String getSecureHost() {
        StringBuffer sb = new StringBuffer();
        sb.append(serverName);
        if (!securePort.equals(443)) {
            sb.append(":");
            sb.append(securePort);
        }
        return sb.toString();
    }

    public String getHost() {
        StringBuffer sb = new StringBuffer();
        sb.append(serverName);
        if (!serverPort.equals(80)) {
            sb.append(":");
            sb.append(serverPort);
        }
        return sb.toString();
    }

    /**
     * @return the serverName
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * @param serverName the serverName to set
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * @return the serverPort
     */
    public Integer getServerPort() {
        return serverPort;
    }

    /**
     * @param serverPort the serverPort to set
     */
    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * @return the securePort
     */
    public Integer getSecurePort() {
        return securePort;
    }

    /**
     * @param securePort the securePort to set
     */
    public void setSecurePort(Integer securePort) {
        this.securePort = securePort;
    }

    /**
     * @return the appName
     */
    public String getAppName() {
        return appName;
    }

    /**
     * @param appName the appName to set
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }
}
