package org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.filter;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
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
