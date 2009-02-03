package org.broadleafcommerce.profile.service.addressValidation;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.UnhandledException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

public class AddressStandardAbbreviations {
	protected final Log logger = LogFactory.getLog(getClass());
    private Map<Object,Object> abbreviationMap;

    public Map<Object,Object> getAbbreviationsMap() {
        return abbreviationMap;
    }

    public void setAbbreviationPropertyFile(Resource abbreviationPropertyFile) {
        try {
            Properties props = new Properties();
            props.load(abbreviationPropertyFile.getInputStream());
            abbreviationMap = Collections.unmodifiableMap(props);
        } catch (IOException e) {
            logger.error("Error loading AddressStandardAbbreviations properties file using Resource: " + abbreviationPropertyFile, e);
            throw new UnhandledException(e);
        }
    }
}
