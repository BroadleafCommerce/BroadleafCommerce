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

public class Rotate extends BaseFilter {

    private RenderingHints hints;
    private double amount;

    public Rotate() {
        //do nothing
    }

    public Rotate(double amount, RenderingHints hints) {
        this.hints = hints;
        this.amount = amount;
    }

    @Override
    public Operation buildOperation(Map<String, String> parameterMap, InputStream artifactStream, String mimeType) {
        String key = FilterTypeEnum.ROTATE.toString().toLowerCase();

        if (!containsMyFilterParams(key, parameterMap)) {
            return null;
        }

        Operation operation = new Operation();
        operation.setName(key);
        String factor = parameterMap.get(key + "-factor");
        operation.setFactor(factor==null?null:Double.valueOf(factor));

        UnmarshalledParameter rotate = new UnmarshalledParameter();
        String rotateApplyFactor = parameterMap.get(key + "-rotate-apply-factor");
        rotate.setApplyFactor(rotateApplyFactor == null ? false : Boolean.valueOf(rotateApplyFactor));
        rotate.setName("rotate");
        rotate.setType(ParameterTypeEnum.DOUBLE.toString());
        rotate.setValue(parameterMap.get(key + "-rotate-amount"));

        operation.setParameters(new UnmarshalledParameter[]{rotate});
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
        
        int neww;
        int newh;
        int w = src.getWidth();
        int h = src.getHeight();
        if (dst == null) {
             double sin = Math.abs(Math.sin(Math.toRadians(amount))), cos = Math.abs(Math.cos(Math.toRadians(amount)));
             neww = (int)Math.floor(w*cos+h*sin);
             newh = (int)Math.floor(h*cos+w*sin);
             dst = createCompatibleDestImage(src, null, neww, newh);
            dstCM = srcCM;
            origDst = dst;
        } else {
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
            neww = dst.getWidth();
            newh = dst.getHeight();
        }
        
        Graphics2D g = dst.createGraphics();
        g.translate((neww-w)/2, (newh-h)/2);
        g.rotate(Math.toRadians(amount), w/2, h/2);
        g.drawRenderedImage(src, null);
        g.dispose();
        origDst = dst;

        if (needToConvert) {
            ColorConvertOp ccop = new ColorConvertOp(hints);
            ccop.filter(dst, origDst);
        }
        else if (origDst != dst) {
            Graphics2D g2 = origDst.createGraphics();
        try {
            g2.drawImage(dst, 0, 0, null);
        } finally {
            g2.dispose();
        }
        }

        return origDst;
    }

}
