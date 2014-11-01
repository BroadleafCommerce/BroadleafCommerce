/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
