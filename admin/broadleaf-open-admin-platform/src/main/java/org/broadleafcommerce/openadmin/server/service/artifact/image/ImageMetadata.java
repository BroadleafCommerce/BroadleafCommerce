/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.service.artifact.image;

/**
 * A bean designed to hold general information about an image
 * 
 * @author jfischer
 *
 */
public class ImageMetadata {
    
    private int width;
    private int height;
    
    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }
    
    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }
    
}
