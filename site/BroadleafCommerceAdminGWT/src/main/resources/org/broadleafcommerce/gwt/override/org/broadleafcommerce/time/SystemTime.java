package org.broadleafcommerce.time;

import java.io.Serializable;

public class SystemTime implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static long asMillis(boolean includeTime) {
		//ignore includeTime parameter
        return System.currentTimeMillis();
    }

}
