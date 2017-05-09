/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.util.sql.importsql;

import org.broadleafcommerce.common.logging.SupportLogManager;
import org.broadleafcommerce.common.logging.SupportLogger;
import org.hibernate.tool.hbm2ddl.SingleLineSqlCommandExtractor;

import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Command extractor that does Postgres specific logic in order for the DemoSite load scripts to import correctly.<br/><br/>
 * 
 * Add:<br/>
 *  {@code blPU.hibernate.hbm2ddl.import_files_sql_extractor=org.broadleafcommerce.common.util.sql.importsql.DemoPostgresSingleLineSqlCommandExtractor
 *  blEventPU.hibernate.hbm2ddl.import_files_sql_extractor=org.broadleafcommerce.common.util.sql.importsql.DemoPostgresSingleLineSqlCommandExtractor}<br>
 *  
 *  in properties file to run load scripts through this extractor
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
public class DemoPostgresSingleLineSqlCommandExtractor extends SingleLineSqlCommandExtractor {

    public static final String NEWLINE_REPLACEMENT_REGEX = "\\\\r\\\\n";
    
    private static final long serialVersionUID = 1L;
    
    private static final SupportLogger LOGGER = SupportLogManager.getLogger("UserOverride", DemoPostgresSingleLineSqlCommandExtractor.class);
    
    @Override
    public String[] extractCommands(Reader reader) {
        String[] commands = super.extractCommands(reader);
        String[] newCommands = new String[commands.length];
        int i = 0;
        for (String command : commands) {
            String newCommand = command;
            
            // Replacing all double single quotes with double double quotes to simplify regex. Original regex caused
            // StackOverFlow exception by exploiting a known issue in java. See - http://bugs.java.com/view_bug.do?bug_id=5050507
            newCommand = newCommand.replaceAll("''", "\"\"");
            
            // Find all string values being set and put an 'E' outside. This has to be done in Postgres so that escapes
            // are evaluated correctly
            newCommand = newCommand.replaceAll("('.*?')", "E$1");
            newCommand = newCommand.replaceAll("\"\"", "''");
            
            // Any MySQL-specific newlines replace with special character newlines
            newCommand = newCommand.replaceAll(NEWLINE_REPLACEMENT_REGEX, "' || CHR(13) || CHR(10) || '");
            // Any MySQL CHAR functions with CHR
            Pattern charPattern = Pattern.compile("CHAR\\((\\d+)\\)");
            Matcher charMatcher = charPattern.matcher(newCommand);
            if (charMatcher.find()) {
                String charCode = charMatcher.group(1);
                newCommand = charMatcher.replaceAll("CHR(" + charCode + ")");
            }

            newCommands[i] = newCommand;
            i++;
        }
        return newCommands;
    }

}
