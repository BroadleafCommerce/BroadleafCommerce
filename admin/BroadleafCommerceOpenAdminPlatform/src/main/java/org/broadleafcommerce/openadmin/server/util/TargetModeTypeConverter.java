package org.broadleafcommerce.openadmin.server.util;

import org.broadleafcommerce.openadmin.server.service.persistence.TargetModeType;
import org.springframework.core.convert.converter.Converter;

public class TargetModeTypeConverter implements Converter<String, TargetModeType> {

	@Override
	public TargetModeType convert(String targetMode) {
		if (targetMode == null) {
    		return null;
    	}
    	return TargetModeType.getInstance(targetMode);
	}

}
