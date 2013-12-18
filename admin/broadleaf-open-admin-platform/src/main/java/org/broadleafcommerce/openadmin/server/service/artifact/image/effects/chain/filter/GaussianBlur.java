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
import java.awt.image.ConvolveOp;
import java.awt.image.IndexColorModel;
import java.awt.image.Kernel;
import java.io.InputStream;
import java.util.Map;

public class GaussianBlur extends BaseFilter {

    public static final int NUM_KERNELS = 16;
    public static final float[][] GAUSSIAN_BLUR_KERNELS = generateGaussianBlurKernels(NUM_KERNELS);
    
    public static float[][] generateGaussianBlurKernels(int level) {
        float[][] pascalsTriangle = generatePascalsTriangle(level);
        float[][] gaussianTriangle = new float[pascalsTriangle.length][];

        for (int i = 0; i < gaussianTriangle.length; i++) {
            float total = 0.0f;
            gaussianTriangle[i] = new float[pascalsTriangle[i].length];

            for (int j = 0; j < pascalsTriangle[i].length; j++) {
                total += pascalsTriangle[i][j];
            }

            float coefficient = 1 / total;
            for (int j = 0; j < pascalsTriangle[i].length; j++) {
                gaussianTriangle[i][j] = coefficient * pascalsTriangle[i][j];
            }

            float checksum = 0.0f;
            for (int j = 0; j < gaussianTriangle[i].length; j++) {
                checksum += gaussianTriangle[i][j];

            }

            if (checksum == 1.0) {
                // hurrah
            }
        }

        return gaussianTriangle;
    }

    public static float[][] generatePascalsTriangle(int level) {
        if (level < 2) {
            level = 2;
        }

        float[][] triangle = new float[level][];
        triangle[0] = new float[1];
        triangle[1] = new float[2];
        triangle[0][0] = 1.0f;
        triangle[1][0] = 1.0f;
        triangle[1][1] = 1.0f;

        for (int i = 2; i < level; i++) {
            triangle[i] = new float[i + 1];
            triangle[i][0] = 1.0f;
            triangle[i][i] = 1.0f;
            for (int j = 1; j < triangle[i].length - 1; j++) {
                triangle[i][j] = triangle[i - 1][j - 1] + triangle[i - 1][j];
            }
        }

        return triangle;
    }
    
    private int kernelSize;
    private int numOfPasses;

    public GaussianBlur() {
        //do nothing
    }

    public GaussianBlur(int kernelSize, int numOfPasses, RenderingHints hints) {
        this.kernelSize = kernelSize;
        this.numOfPasses = numOfPasses;
    }

    @Override
    public Operation buildOperation(Map<String, String> parameterMap, InputStream artifactStream, String mimeType) {
        String key = FilterTypeEnum.GAUSSIANBLUR.toString().toLowerCase();

        if (!containsMyFilterParams(key, parameterMap)) {
            return null;
        }

        Operation operation = new Operation();
        operation.setName(key);
        String factor = parameterMap.get(key + "-factor");
        operation.setFactor(factor==null?null:Double.valueOf(factor));

        UnmarshalledParameter kernelSize = new UnmarshalledParameter();
        String kernelSizeApplyFactor = parameterMap.get(key + "-kernel-size-apply-factor");
        kernelSize.setApplyFactor(kernelSizeApplyFactor == null ? false : Boolean.valueOf(kernelSizeApplyFactor));
        kernelSize.setName("kernel-size");
        kernelSize.setType(ParameterTypeEnum.INT.toString());
        kernelSize.setValue(parameterMap.get(key + "-kernel-size-amount"));

        UnmarshalledParameter numOfPasses = new UnmarshalledParameter();
        String numOfPassesApplyFactor = parameterMap.get(key + "-num-passes-apply-factor");
        numOfPasses.setApplyFactor(numOfPassesApplyFactor == null ? false : Boolean.valueOf(numOfPassesApplyFactor));
        numOfPasses.setName("num-passes");
        numOfPasses.setType(ParameterTypeEnum.INT.toString());
        numOfPasses.setValue(parameterMap.get(key + "-num-passes-amount"));

        operation.setParameters(new UnmarshalledParameter[]{kernelSize, numOfPasses});
        return operation;
    }

    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (kernelSize < 1 || kernelSize > NUM_KERNELS) {
            return src;
        }

        if (numOfPasses < 1) {
            return src;
        }
        
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
        }
        
        float[] matrix = GAUSSIAN_BLUR_KERNELS[kernelSize - 1];

        Kernel gaussianBlur1 = new Kernel(matrix.length, 1, matrix);
        Kernel gaussianBlur2 = new Kernel(1, matrix.length, matrix);
        ConvolveOp gaussianOp1 = new ConvolveOp(gaussianBlur1, ConvolveOp.EDGE_NO_OP, null);
        ConvolveOp gaussianOp2 = new ConvolveOp(gaussianBlur2, ConvolveOp.EDGE_NO_OP, null);

        BufferedImage tempImage = new BufferedImage(src.getWidth(),
                src.getHeight(), src.getType());
        dst = new BufferedImage(src.getWidth(),
                src.getHeight(), src.getType());

        BufferedImage nextSource = src;

        for (int i = 0; i < numOfPasses; i++) {
            tempImage = gaussianOp1.filter(nextSource, tempImage);
            dst = gaussianOp2.filter(tempImage, dst);

            nextSource = dst;
        }

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
