/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

public class UnsharpMask extends BaseFilter {

    private RenderingHints hints;
    private float value;
    private int radius;

    public UnsharpMask() {
        //do nothing
    }

    public UnsharpMask(float value, int radius, RenderingHints hints) {
        this.hints = hints;
        this.radius = radius;
        this.value = value;
    }

    @Override
    public Operation buildOperation(Map<String, String> parameterMap, InputStream artifactStream, String mimeType) {
        String key = FilterTypeEnum.UNSHARPMASK.toString().toLowerCase();

        if (!containsMyFilterParams(key, parameterMap)) {
            return null;
        }

        Operation operation = new Operation();
        operation.setName(key);
        String factor = parameterMap.get(key + "-factor");
        operation.setFactor(factor==null?null:Double.valueOf(factor));

        UnmarshalledParameter amount = new UnmarshalledParameter();
        String amountApplyFactor = parameterMap.get(key + "-value-apply-factor");
        amount.setApplyFactor(amountApplyFactor == null ? false : Boolean.valueOf(amountApplyFactor));
        amount.setName("value");
        amount.setType(ParameterTypeEnum.FLOAT.toString());
        amount.setValue(parameterMap.get(key + "-value-amount"));

        UnmarshalledParameter radius = new UnmarshalledParameter();
        String radiusApplyFactor = parameterMap.get(key + "-radius-apply-factor");
        radius.setApplyFactor(radiusApplyFactor == null ? false : Boolean.valueOf(radiusApplyFactor));
        radius.setName("radius");
        radius.setType(ParameterTypeEnum.INT.toString());
        radius.setValue(parameterMap.get(key + "-radius-amount"));

        operation.setParameters(new UnmarshalledParameter[]{amount, radius});
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
        GaussianBlur blur = new GaussianBlur(radius, 1, hints);
        dst = blur.filter(src, null);
        int[] uMaskBlur = ImageConverter.getPixels(dst);
        int imageWidth = dst.getWidth();
        int imageHeight = dst.getHeight();
        
        int index = 0;
        for (int y=0;y<imageHeight;y++){
            for (int x=0;x<imageWidth;x++){
                int R1 = (originalPixels[index] >> 16) & 0xff;
                int G1 = (originalPixels[index] >> 8) & 0xff;
                int B1 = (originalPixels[index] >> 0) & 0xff;

                int R2 = (uMaskBlur[index] >> 16) & 0xff;
                int G2 = (uMaskBlur[index] >> 8) & 0xff;
                int B2 = (uMaskBlur[index] >> 0) & 0xff;

                int R3 = (int)(value *(float)R1 - (value -1F)*(float)R2);
                int G3 = (int)(value *(float)G1 - (value -1F)*(float)G2);
                int B3 = (int)(value *(float)B1- (value -1F)*(float)B2);

                // fix overflows
                if (R3 > 255) R3 = 255;
                if (R3 < 0) R3 = 0;
                if (G3 > 255) G3 = 255;
                if (G3 < 0) G3 = 0;
                if (B3 > 255) B3 = 255;
                if (B3 < 0) B3 = 0;

                originalPixels[index] = (originalPixels[index] & 0xff000000)  | (R3<<16) | (G3 << 8) | (B3 <<0);
                index++;
            }
        }
        
        dst = ImageConverter.getImage(originalPixels, imageWidth, imageHeight);
         
        if (needToConvert) {
            ColorConvertOp ccop = new ColorConvertOp(hints);
            ccop.filter(dst, origDst);
        }
        else if (origDst != dst) {
            java.awt.Graphics2D g = origDst.createGraphics();
        try {
            g.drawImage(dst, 0, 0, null);
        } finally {
            g.dispose();
        }
        }

        return origDst;
    }

}
