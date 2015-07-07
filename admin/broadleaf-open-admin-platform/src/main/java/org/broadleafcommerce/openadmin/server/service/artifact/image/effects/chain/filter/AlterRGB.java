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

public class AlterRGB extends BaseFilter {

    private RenderingHints hints;
    private int red;
    private int green;
    private int blue;

    public AlterRGB() {
        //do nothing
    }

    public AlterRGB(int red, int green, int blue, RenderingHints hints) {
        this.hints = hints;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public Operation buildOperation(Map<String, String> parameterMap, InputStream artifactStream, String mimeType) {
        String key = FilterTypeEnum.ALTERRGB.toString().toLowerCase();

        if (!containsMyFilterParams(key, parameterMap)) {
            return null;
        }

        Operation operation = new Operation();
        operation.setName(key);
        String factor = parameterMap.get(key + "-factor");
        operation.setFactor(factor==null?null:Double.valueOf(factor));

        UnmarshalledParameter red = new UnmarshalledParameter();
        String redApplyFactor = parameterMap.get(key + "-red-apply-factor");
        red.setApplyFactor(redApplyFactor == null ? false : Boolean.valueOf(redApplyFactor));
        red.setName("red");
        red.setType(ParameterTypeEnum.INT.toString());
        red.setValue(parameterMap.get(key + "-red-amount"));

        UnmarshalledParameter green = new UnmarshalledParameter();
        String greenApplyFactor = parameterMap.get(key + "-green-apply-factor");
        green.setApplyFactor(greenApplyFactor == null ? false : Boolean.valueOf(greenApplyFactor));
        green.setName("green");
        green.setType(ParameterTypeEnum.INT.toString());
        green.setValue(parameterMap.get(key + "-green-amount"));

        UnmarshalledParameter blue = new UnmarshalledParameter();
        String blueApplyFactor = parameterMap.get(key + "-blue-apply-factor");
        blue.setApplyFactor(blueApplyFactor == null ? false : Boolean.valueOf(blueApplyFactor));
        blue.setName("blue");
        blue.setType(ParameterTypeEnum.INT.toString());
        blue.setValue(parameterMap.get(key + "-blue-amount"));

        operation.setParameters(new UnmarshalledParameter[]{red, green, blue});
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

                r+=red;
                g+=green;
                b+=blue;

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
