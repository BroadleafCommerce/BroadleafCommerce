/*
 * #%L
 * BroadleafCommerce Export Module
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package com.broadleafcommerce.export.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

/**
 * Helper utility used to get different types of writers for creating exports
 *  
 * @author Jay Aisenbrey (cja769)
 *
 */
public class ExportWriterUtil {
    
    public static ObjectWriter getCsvWriter(boolean useHeaders, Class<?> schemaClass) {
        CsvMapper mapper = new CsvMapper();
        // This has to be configured to false so that the output stream isn't closed after writing the first batch to it
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        CsvSchema schema = mapper.schemaFor(schemaClass);
        return mapper.writer(schema.withUseHeader(useHeaders).withoutQuoteChar());
    }
}
