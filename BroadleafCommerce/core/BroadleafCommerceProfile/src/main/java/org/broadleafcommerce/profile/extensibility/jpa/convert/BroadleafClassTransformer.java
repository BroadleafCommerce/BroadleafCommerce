package org.broadleafcommerce.profile.extensibility.jpa.convert;

import java.util.Properties;

import javax.persistence.spi.ClassTransformer;

public interface BroadleafClassTransformer extends ClassTransformer {

	public void compileJPAProperties(Properties props, Object key) throws Exception;
		
}
