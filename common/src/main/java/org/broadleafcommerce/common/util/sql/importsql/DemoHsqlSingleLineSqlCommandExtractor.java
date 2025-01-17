/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.schema.internal.script.SingleLineSqlScriptExtractor;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a utility class that is only meant to be used for testing the BLC demo on HSQLDB. This replaces any of the demo
 * insert SQL statements with HSQLDB-compatible syntax.
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public class DemoHsqlSingleLineSqlCommandExtractor extends SingleLineSqlScriptExtractor {

    @Override
    public List<String> extractCommands(Reader reader, Dialect dialect) {
        List<String> commands = super.extractCommands(reader, dialect);
        List<String> newCommands = new ArrayList<>(commands.size());
        for (String command : commands) {
            String newCommand = command;

            // Any MySQL-specific newlines replace with special character newlines
            newCommand = newCommand.replaceAll(
                    DemoPostgresSingleLineSqlCommandExtractor.NEWLINE_REPLACEMENT_REGEX,
                    "' || CHAR(13) || CHAR(10) || '"
            );

            //remove the double backslashes - hsql does not honor backslash as an escape character
            newCommand = newCommand.replaceAll(DemoSqlServerSingleLineSqlCommandExtractor.DOUBLEBACKSLASHMATCH, "\\\\");

            //replace escaped double quotes (\") with encoded double quote
            newCommand = newCommand.replaceAll("\\\\\"", "' || CHAR(34) || '");

            newCommands.add(newCommand);
        }
        return newCommands;
    }

}
