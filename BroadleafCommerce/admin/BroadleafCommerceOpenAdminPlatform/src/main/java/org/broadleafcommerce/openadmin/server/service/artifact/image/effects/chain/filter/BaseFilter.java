package org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.filter;

import org.broadleafcommerce.openadmin.server.service.artifact.OperationBuilder;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

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

}
