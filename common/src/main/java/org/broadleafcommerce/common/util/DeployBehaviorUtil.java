package org.broadleafcommerce.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Jeff Fischer
 */
@Component("blDeployBehaviorUtil")
public class DeployBehaviorUtil {

    @Value("${enterprise.use.production.sandbox.mode:false}")
    protected boolean isProductionSandBoxMode;

    @Value("${mt.loaded.flag:false}")
    protected boolean isMtLoaded;

    public boolean isProductionSandBoxMode() {
        //this functionality should only be active when multitenancy is in play
        return isProductionSandBoxMode && isMtLoaded;
    }
}
