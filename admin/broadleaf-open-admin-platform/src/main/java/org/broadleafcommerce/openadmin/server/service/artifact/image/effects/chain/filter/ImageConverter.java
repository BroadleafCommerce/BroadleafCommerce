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
package org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.filter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

public class ImageConverter {
    
    public static int[] getPixels(BufferedImage image){
        int iWidth = image.getWidth();
        int iHeight = image.getHeight();
        int numPixels = iWidth*iHeight;
        int rawPixels[] = new int[numPixels];
        if (rawPixels==null) return null;
        PixelGrabber grabber = new PixelGrabber(image,0,0,iWidth,iHeight,rawPixels,0,iWidth);
        try{
            grabber.grabPixels();
        } catch (InterruptedException e){
            //do nothing
        }
        return rawPixels;
    }

    public static BufferedImage getImage(int[] pixels, int width, int height){
        ColorModel cm = ColorModel.getRGBdefault();
        MemoryImageSource imageSource = new MemoryImageSource(width,height,cm,pixels,0,width);
        imageSource.setAnimated(true);
        Image temp = Toolkit.getDefaultToolkit().createImage(imageSource);
        BufferedImage image = convertImage(temp);
        
        return image;
    }
    
    public static BufferedImage convertImage(Image original) {
        ColorModel cm = ColorModel.getRGBdefault();
        int width = original.getWidth(null);
        int height = original.getHeight(null);
        BufferedImage image = new BufferedImage (cm,cm.createCompatibleWritableRaster(width, height),cm.isAlphaPremultiplied(), null);
        Graphics2D bg = image.createGraphics();
        bg.drawImage(original, 0, 0, width, height, null);
        bg.dispose();
        
        return image;
    }
}
