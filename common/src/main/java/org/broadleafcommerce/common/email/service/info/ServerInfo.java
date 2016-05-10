/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
