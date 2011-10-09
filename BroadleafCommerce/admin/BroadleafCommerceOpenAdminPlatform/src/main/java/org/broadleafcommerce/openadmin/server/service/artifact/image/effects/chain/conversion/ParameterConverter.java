package org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.conversion;

public interface ParameterConverter {

	public Parameter convert(String value, Double factor, boolean applyFactor) throws ConversionException;
	
}
