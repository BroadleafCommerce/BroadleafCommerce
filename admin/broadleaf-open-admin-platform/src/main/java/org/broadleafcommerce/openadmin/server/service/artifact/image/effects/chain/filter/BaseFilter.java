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

import org.broadleafcommerce.openadmin.server.service.artifact.OperationBuilder;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.util.Map;

public abstract class BaseFilter implements BufferedImageOp, OperationBuilder {
    
    protected RenderingHints hints;

    /* (non-Javadoc)
     * @see java.awt.image.BufferedImageOp#createCompatibleDestImage(java.awt.image.BufferedImage, java.awt.image.ColorModel)
     */
    public BufferedImage createCompatibleDestImage(BufferedImage src,
            ColorModel destCM) {
        BufferedImage image;
        if (destCM == null) {
            destCM = src.getColorModel();
            // Not much support for ICM
            if (destCM instanceof IndexColorModel) {
                destCM = ColorModel.getRGBdefault();
            }
        }

        int w = src.getWidth();
        int h = src.getHeight();
        image = new BufferedImage (destCM,
                                   destCM.createCompatibleWritableRaster(w, h),
                                   destCM.isAlphaPremultiplied(), null);

        return image;
    }
    
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM, int width, int height) {
        BufferedImage image;
        if (destCM == null) {
            destCM = src.getColorModel();
            // Not much support for ICM
            if (destCM instanceof IndexColorModel) {
                destCM = ColorModel.getRGBdefault();
            }
        }

        image = new BufferedImage (destCM,
                                   destCM.createCompatibleWritableRaster(width, height),
                                   destCM.isAlphaPremultiplied(), null);

        return image;
    }

    /* (non-Javadoc)
     * @see java.awt.image.BufferedImageOp#getBounds2D(java.awt.image.BufferedImage)
     */
    public Rectangle2D getBounds2D(BufferedImage src) {
        return src.getRaster().getBounds();
    }

    /* (non-Javadoc)
     * @see java.awt.image.BufferedImageOp#getPoint2D(java.awt.geom.Point2D, java.awt.geom.Point2D)
     */
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Float();
        }
        dstPt.setLocation(srcPt.getX(), srcPt.getY());

        return dstPt;
    }

    /* (non-Javadoc)
     * @see java.awt.image.BufferedImageOp#getRenderingHints()
     */
    public RenderingHints getRenderingHints() {
        return hints;
    }

    protected boolean containsMyFilterParams(String key, Map<String, String> parameterMap) {
        for (String paramKey : parameterMap.keySet()) {
            if (paramKey.startsWith(key)) {
                return true;
            }
        }
        return false;
    }
}
