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
import java.io.InputStream;
import java.util.Map;

public class Resize extends BaseFilter {

    private RenderingHints hints;
    private int targetWidth;
    private int targetHeight;
    private boolean highQuality;
    private Object hint;
    private boolean maintainAspectRatio;
    private boolean reduceOnly;

    public Resize() {
        //do nothing
    }

    public Resize(int targetWidth, int targetHeight, boolean highQuality, boolean maintainAspectRatio, boolean reduceOnly, RenderingHints hints) {
        this.hints = hints;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        this.highQuality = highQuality;
        this.hint = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
        this.maintainAspectRatio = maintainAspectRatio;
        this.reduceOnly = reduceOnly;
    }

    @Override
    public Operation buildOperation(Map<String, String> parameterMap, InputStream artifactStream, String mimeType) {
        String key = FilterTypeEnum.RESIZE.toString().toLowerCase();

        if (!containsMyFilterParams(key, parameterMap)) {
            return null;
        }

        Operation operation = new Operation();
        operation.setName(key);
        String factor = parameterMap.get(key + "-factor");
        operation.setFactor(factor==null?null:Double.valueOf(factor));

        UnmarshalledParameter targetWidth = new UnmarshalledParameter();
        String targetWidthApplyFactor = parameterMap.get(key + "-width-apply-factor");
        targetWidth.setApplyFactor(targetWidthApplyFactor == null ? false : Boolean.valueOf(targetWidthApplyFactor));
        targetWidth.setName("target-width");
        targetWidth.setType(ParameterTypeEnum.INT.toString());
        targetWidth.setValue(parameterMap.get(key + "-width-amount"));

        UnmarshalledParameter targetHeight = new UnmarshalledParameter();
        String targetHeightApplyFactor = parameterMap.get(key + "-height-apply-factor");
        targetHeight.setApplyFactor(targetHeightApplyFactor == null ? false : Boolean.valueOf(targetHeightApplyFactor));
        targetHeight.setName("target-height");
        targetHeight.setType(ParameterTypeEnum.INT.toString());
        targetHeight.setValue(parameterMap.get(key + "-height-amount"));

        UnmarshalledParameter highQuality = new UnmarshalledParameter();
        highQuality.setName("high-quality");
        highQuality.setType(ParameterTypeEnum.BOOLEAN.toString());
        highQuality.setValue(parameterMap.get(key + "-high-quality")==null?"false":parameterMap.get(key + "-high-quality"));

        UnmarshalledParameter maintainAspectRatio = new UnmarshalledParameter();
        maintainAspectRatio.setName("maintain-aspect-ratio");
        maintainAspectRatio.setType(ParameterTypeEnum.BOOLEAN.toString());
        maintainAspectRatio.setValue(parameterMap.get(key + "-maintain-aspect-ratio") == null ? "false" : parameterMap.get(key + "-maintain-aspect-ratio"));

        UnmarshalledParameter reduceOnly = new UnmarshalledParameter();
        reduceOnly.setName("reduce-only");
        reduceOnly.setType(ParameterTypeEnum.BOOLEAN.toString());
        reduceOnly.setValue(parameterMap.get(key + "-reduce-only") == null ? "false" : parameterMap.get(key + "-reduce-only"));

        operation.setParameters(new UnmarshalledParameter[]{targetWidth, targetHeight, highQuality, maintainAspectRatio, reduceOnly});
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

        if (src.getWidth() <= targetWidth && src.getHeight() <= targetHeight && reduceOnly) {
            return src;
        }
        
        BufferedImage temp = getScaledInstance(src, targetWidth, targetHeight, hint, highQuality, maintainAspectRatio, reduceOnly);
        
        if (dst != null) {
            Graphics g = dst.createGraphics();
            g.drawImage(temp, 0, 0, temp.getWidth(), temp.getHeight(), null);
            g.dispose();
        } else {
            dst = temp;
        }
        
        return dst;
    }
    
    private BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint, boolean higherQuality, boolean maintainAspectRatio, boolean reduceOnly) {
        BufferedImage ret = (BufferedImage) img;
        int w, h, destW, destH;

        if (maintainAspectRatio) {
            int wDiff = Math.abs(img.getWidth() - targetWidth);
            int hDiff = Math.abs(img.getHeight() - targetHeight);
            if (wDiff > hDiff) {
                destH = targetHeight;
                destW = Double.valueOf((((double) img.getWidth()) * ((double) destH))/((double) img.getHeight())).intValue();
            } else {
                destW = targetWidth;
                destH = Double.valueOf((((double) img.getHeight()) * ((double) destW))/((double) img.getWidth())).intValue();
            }
        } else {
            destW = targetWidth;
            destH = targetHeight;
        }

        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            if (!maintainAspectRatio) {
                w = targetWidth;
                h = targetHeight;
            } else {
                w = destW;
                h = destH;
            }
        }

        do {
            if (higherQuality && w > destW) {
                w /= 2;
                if (w < destW) {
                    w = destW;
                }
            } else if (higherQuality && w < destW) {
                w *= 2;
                if (w > destW) {
                    w = destW;
                }
            }

            if (higherQuality && h > destH) {
                h /= 2;
                if (h < destH) {
                    h = destH;
                }
            } else if (higherQuality && h < destH) {
                h *= 2;
                if (h > destH) {
                    h = destH;
                }
            }
            int type;
            if (img.getType()!=BufferedImage.TYPE_INT_ARGB && img.getType()!=BufferedImage.TYPE_INT_RGB) {
                type = BufferedImage.TYPE_INT_ARGB;
            } else {
                type = img.getType();
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.drawImage(ret.getScaledInstance(w, h, Image.SCALE_SMOOTH), 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != destW || h != destH);

        return ret;
    }

}
