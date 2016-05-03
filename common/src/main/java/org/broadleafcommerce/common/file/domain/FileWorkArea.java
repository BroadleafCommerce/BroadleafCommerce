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
package org.broadleafcommerce.common.file.domain;

import java.io.File;

/**
 * Represents a temporary location on the fileSystem.
 * 
 * Used by the file-service as a reference point when managing files for a user.
 * 
 * @author bpolster
 *
 */
public class FileWorkArea {

    protected String filePathLocation;
    
    /**
     * Gets the file path location representing this work area ending with an appropriate system-specific separator
     * @return
     */
    public String getFilePathLocation() {
        if (!filePathLocation.endsWith(File.separator)) {
            return filePathLocation + File.separator;
        } else {
            return filePathLocation;
        }
    }
    
    public void setFilePathLocation(String filePathLocation) {
        if (!filePathLocation.endsWith(File.separator)) {
            this.filePathLocation = filePathLocation + File.separator;
        } else {
            this.filePathLocation = filePathLocation;
        }
    }
    
}
