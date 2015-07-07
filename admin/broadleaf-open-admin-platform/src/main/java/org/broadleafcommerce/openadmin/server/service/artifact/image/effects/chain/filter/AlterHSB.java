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
import org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.UnmarshalledParameter;
import org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.conversion.ParameterTypeEnum;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.InputStream;
import java.util.Map;

public class AlterHSB extends BaseFilter {

    private RenderingHints hints;
    private float hue;
    private float saturation;
    private float brightness;

    public AlterHSB() {
        //do nothing
    }
    
    public AlterHSB(float hue, float saturation, float brightness, RenderingHints hints) {
        this.hints = hints;
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
    }

    @Override
    public Operation buildOperation(Map<String, String> parameterMap, InputStream artifactStream, String mimeType) {
        String key = FilterTypeEnum.ALTERHSB.toString().toLowerCase();
        if (!containsMyFilterParams(key, parameterMap)) {
            return null;
        }

        Operation operation = new Operation();
        operation.setName(key);
        String factor = parameterMap.get(key + "-factor");
        operation.setFactor(factor==null?null:Double.valueOf(factor));

        UnmarshalledParameter hue = new UnmarshalledParameter();
        String hueApplyFactor = parameterMap.get(key + "-hue-apply-factor");
        hue.setApplyFactor(hueApplyFactor==null?false:Boolean.valueOf(hueApplyFactor));
        hue.setName("hue");
        hue.setType(ParameterTypeEnum.FLOAT.toString());
        hue.setValue(parameterMap.get(key + "-hue-amount"));

        UnmarshalledParameter saturation = new UnmarshalledParameter();
        String saturationApplyFactor = parameterMap.get(key + "-saturation-apply-factor");
        saturation.setApplyFactor(saturationApplyFactor == null ? false : Boolean.valueOf(saturationApplyFactor));
        saturation.setName("saturation");
        saturation.setType(ParameterTypeEnum.FLOAT.toString());
        saturation.setValue(parameterMap.get(key + "-saturation-amount"));

        UnmarshalledParameter brightness = new UnmarshalledParameter();
        String brightnessApplyFactor = parameterMap.get(key + "-brightness-apply-factor");
        brightness.setApplyFactor(brightnessApplyFactor == null ? false : Boolean.valueOf(brightnessApplyFactor));
        brightness.setName("brightness");
        brightness.setType(ParameterTypeEnum.FLOAT.toString());
        brightness.setValue(parameterMap.get(key + "-brightness-amount"));

        operation.setParameters(new UnmarshalledParameter[]{hue, saturation, brightness});
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
            throw new IllegalArgumentException("src image cannot be the "+
                                               "same as the dst image");
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
        
        int r=0;
        int g=0;
        int b=0;
        
        int index=0;
        for (int y=0;y<imageHeight;y++){
            for (int x=0;x<imageWidth;x++){
                r = (originalPixels[index] >> 16) & 0xff;
                g = (originalPixels[index] >> 8) & 0xff;
                b = (originalPixels[index] >> 0) & 0xff;

                float[] hsb = Color.RGBtoHSB(r, g, b, null);
                float h = hsb[0] * hue;
                float s = hsb[1] * saturation;
                float br = hsb[2] * brightness;

                // fix overflows
                if (h > 360) h = 360;
                if (h < 0) h = 0;
                if (s > 1) s = 1;
                if (s < 0) s = 0;
                if (br > 1) br = 1;
                if (br < 0) br = 0;
                
                Color rgb = new Color(Color.HSBtoRGB(h, s, br));
                r = rgb.getRed();
                g = rgb.getGreen();
                b = rgb.getBlue();

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
