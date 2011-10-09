package org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.conversion.impl;

import org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.conversion.ConversionException;
import org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.conversion.Parameter;
import org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.conversion.ParameterConverter;

import java.awt.*;
import java.util.StringTokenizer;

public class RectangleParameterConverter implements ParameterConverter {

	/* (non-Javadoc)
	 * @see com.xpressdocs.email.asset.effects.chain.conversion.ParameterConverter#convert(java.lang.String, double)
	 */
	public Parameter convert(String value, Double factor, boolean applyFactor) throws ConversionException {
		StringTokenizer tokens = new StringTokenizer(value, ",");
		Rectangle rect = new Rectangle();
		rect.x = (int) (applyFactor&&factor!=null?Integer.parseInt(tokens.nextToken())/factor:Integer.parseInt(tokens.nextToken()));
		rect.y = (int) (applyFactor&&factor!=null?Integer.parseInt(tokens.nextToken())/factor:Integer.parseInt(tokens.nextToken()));
		rect.width = (int) (applyFactor&&factor!=null?Integer.parseInt(tokens.nextToken())/factor:Integer.parseInt(tokens.nextToken()));
		rect.height = (int) (applyFactor&&factor!=null?Integer.parseInt(tokens.nextToken())/factor:Integer.parseInt(tokens.nextToken()));
		
		Parameter param = new Parameter();
		param.setParameterClass(Rectangle.class);
		param.setParameterInstance(rect);
		
		return param;
	}

}
