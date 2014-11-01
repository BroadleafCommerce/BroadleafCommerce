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

import org.broadleafcommerce.openadmin.server.service.artifact.image.Operation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

/**
 * This filter is based conceptually on the auto-levels feature of Photoshop and functions in the same way.
 * The filter automatically adjusts tonal range for problem photographs, making sure the tones are
 * equally distributed from black to white. Note, a marginal clipping does occur at the highs and lows to account
 * for aberrant pixels in those quadrants that might erroneously effect the calculation.
 * 
 * @author jfischer
 *
 */
public class AutoLevelsRGB extends BaseFilter {
    
    private static final double TOPCLIP = 0.01D;
    private static final double BOTTOMCLIP = 0.01D;
    
    public static void main(String[] args) {
        try {
            BufferedImage image = ImageIO.read(new File("C:/temp/test.jpg"));
            AutoLevelsRGB levels = new AutoLevelsRGB(null);
            BufferedImage newImage = levels.filter(image, null);
            ImageIO.write(newImage, "jpg", new File("C:/temp/test2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RenderingHints hints;

    public AutoLevelsRGB() {
        //do nothing
    }

    public AutoLevelsRGB(RenderingHints hints) {
        this.hints = hints;
    }
    
    @Override
    public Operation buildOperation(Map<String, String> parameterMap, InputStream artifactStream, String mimeType) {
        String key = FilterTypeEnum.AUTOLEVELSRGB.toString().toLowerCase();

        if (!containsMyFilterParams(key, parameterMap)) {
            return null;
        }

        Operation operation = new Operation();
        operation.setName(key);

        return operation;
    }

    /* (non-Javadoc)
     * @see java.awt.image.BufferedImageOp#filter(java.awt.image.BufferedImage, java.awt.image.BufferedImage)
     */
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (src == null) {
            throw new NullPointerException("src image is null");
        }
        if (src == dst) {
            throw new IllegalArgumentException("src image cannot be the same as the dst image");
        }
        
        boolean needToConvert = false;
        ColorModel srcCM = src.getColorModel();
        ColorModel dstCM;
        BufferedImage origDst = dst;
        
        if (srcCM instanceof IndexColorModel) {
            IndexColorModel icm = (IndexColorModel) srcCM;
            src = icm.convertToIntDiscrete(src.getRaster(), false);
            srcCM = src.getColorModel();
        }
        
        if (dst == null) {
            dst = createCompatibleDestImage(src, null);
            dstCM = srcCM;
            origDst = dst;
        }
        else {
            dstCM = dst.getColorModel();
            if (srcCM.getColorSpace().getType() !=
                dstCM.getColorSpace().getType())
            {
                needToConvert = true;
                dst = createCompatibleDestImage(src, null);
                dstCM = dst.getColorModel();
            }
            else if (dstCM instanceof IndexColorModel) {
                dst = createCompatibleDestImage(src, null);
                dstCM = dst.getColorModel();
            }
        }
        
        int[] originalPixels = ImageConverter.getPixels(src);
        int imageWidth = dst.getWidth();
        int imageHeight = dst.getHeight();
        
        /*
         * Sort all the red pixels from low to high and establish
         * the clipping regions. We also note the delta from the 
         * lowest and highest leftover pixels to black and white,
         * respectively.
         */
        int[] redPixels = new int[originalPixels.length];
        for (int j=0;j<originalPixels.length;j++){
            redPixels[j] = (originalPixels[j] >> 16) & 0xff;
        }
        Arrays.sort(redPixels);
        int redStart = redPixels[(int) (redPixels.length * BOTTOMCLIP )];
        int redEnd = redPixels[redPixels.length-(int) (redPixels.length * TOPCLIP)];
        int redEndDelta = 255 - redEnd;
        int redStartDelta = redStart;
        
        int[] greenPixels = new int[originalPixels.length];
        for (int j=0;j<originalPixels.length;j++){
            greenPixels[j] = (originalPixels[j] >> 8) & 0xff;
        }
        Arrays.sort(greenPixels);
        int greenStart = greenPixels[(int) (greenPixels.length * BOTTOMCLIP )];
        int greenEnd = greenPixels[greenPixels.length-(int) (greenPixels.length * TOPCLIP)];
        int greenEndDelta = 255 - greenEnd;
        int greenStartDelta = greenStart;
        
        int[] bluePixels = new int[originalPixels.length];
        for (int j=0;j<originalPixels.length;j++){
            bluePixels[j] = (originalPixels[j] >> 0) & 0xff;
        }
        Arrays.sort(bluePixels);
        int blueStart = bluePixels[(int) (bluePixels.length * BOTTOMCLIP )];
        int blueEnd = bluePixels[bluePixels.length-(int) (bluePixels.length * TOPCLIP)];
        int blueEndDelta = 255 - blueEnd;
        int blueStartDelta = blueStart;
        
        int r=0;
        int g=0;
        int b=0;
        int index=0;
        for (int y=0;y<imageHeight;y++){
            for (int x=0;x<imageWidth;x++){
                r = (originalPixels[index] >> 16) & 0xff;
                g = (originalPixels[index] >> 8) & 0xff;
                b = (originalPixels[index] >> 0) & 0xff;
                
                if (r > redStart && r < redEnd) {
                    if (redEndDelta > 0) {
                        if (redEnd - r == 0) {
                            r = 255;
                        } else {
                            /*
                             * If there was a white shift, distribute all the pixels proportionally up
                             */
                            r = redEnd + redEndDelta - (((redEnd - r) * (redEnd + redEndDelta)) / redEnd);
                        }
                    }
                    if (redStartDelta > 0) {
                        if (r - redStartDelta == 0) {
                            r = 0;
                        } else {
                            /*
                             * If there was a black shift, distribute all the pixels proportionally down
                             */
                            r = redEnd - (((redEnd -(redStart - redStartDelta)) * (redEnd - r))/(redEnd - redStart));
                        }
                    }
                } else if (r <= redStart) {
                    r = 0;
                } else {
                    r = 255;
                }
                
                if (g > greenStart && g < greenEnd) {
                    if (greenEndDelta > 0){
                        if (greenEnd - g == 0) {
                            g = 255;
                        } else {
                            g = greenEnd + greenEndDelta - (((greenEnd - g) * (greenEnd + greenEndDelta)) / greenEnd);
                        }
                    }
                    if (greenStartDelta > 0){
                        if (g - greenStartDelta == 0) {
                            g = 0;
                        } else {
                            g = greenEnd - (((greenEnd -(greenStart - greenStartDelta)) * (greenEnd - g))/(greenEnd - greenStart));
                        }
                    }
                } else if (g <= greenStart) {
                    g = 0;
                } else {
                    g = 255;
                }
                
                if (b > blueStart && b < blueEnd) {
                    if (blueEndDelta > 0){
                        if (blueEnd - b == 0) {
                            b = 255;
                        } else {
                            b = blueEnd + blueEndDelta - (((blueEnd - b) * (blueEnd + blueEndDelta)) / blueEnd);
                        }
                    }
                    if (blueStartDelta > 0){
                        if (b - blueStartDelta == 0) {
                            b = 0;
                        } else {
                            b = blueEnd - (((blueEnd -(blueStart - blueStartDelta)) * (blueEnd - b))/(blueEnd - blueStart));
                        }
                    }
                } else if (b <= blueStart) {
                    b = 0;
                } else {
                    b = 255;
                }

                // fix overflows
                if (r > 255) r = 255;
                if (r < 0) r = 0;
                if (g > 255) g = 255;
                if (g < 0) g = 0;
                if (b > 255) b = 255;
                if (b < 0) b = 0;

                originalPixels[index] = (originalPixels[index] & 0xff000000)  | (r << 16) | (g << 8) | (b << 0);
                index++;
            }
        }
        
        dst = ImageConverter.getImage(originalPixels, imageWidth, imageHeight);
         
        if (needToConvert) {
            ColorConvertOp ccop = new ColorConvertOp(hints);
            ccop.filter(dst, origDst);
        }
        else if (origDst != dst) {
            java.awt.Graphics2D g2 = origDst.createGraphics();
        try {
            g2.drawImage(dst, 0, 0, null);
        } finally {
            g2.dispose();
        }
        }

        return origDst;
    }

}
