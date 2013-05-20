package org.broadleafcommerce.common.config.service;

import org.broadleafcommerce.common.config.domain.ModuleConfiguration;

public interface ModuleProvider {

    /**
     * Indicates if, given the configuration, this module can respond to the particular request.
     * 
     * @param config
     * @return
     */
    public boolean canRespond(ModuleConfiguration config);


}
