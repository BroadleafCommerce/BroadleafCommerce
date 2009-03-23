package org.broadleafcommerce.email.info;

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
