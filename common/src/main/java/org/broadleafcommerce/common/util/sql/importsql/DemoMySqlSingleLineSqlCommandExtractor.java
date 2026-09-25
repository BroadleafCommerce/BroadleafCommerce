/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
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

import org.hibernate.dialect.Dialect;
import org.hibernate.tool.schema.internal.script.SingleLineSqlScriptExtractor;

import java.io.Reader;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Command extractor that does MySQL specific logic in order for the DemoSite load scripts to import correctly.<br/><br/>
 * <p>
 * MySQL table names are case-sensitive when {@code lower_case_table_names=0} (the default on Linux), while the tables
 * created by Hibernate are uppercase. This extractor uppercases the table names referenced by the load scripts
 * (after {@code INTO}, {@code UPDATE}, {@code FROM} and {@code JOIN}) so that scripts using lowercase table names
 * also import correctly. String literals are left untouched.
 * <p>
 * Add:<br/>
 * {@code blPU.hibernate.hbm2ddl.import_files_sql_extractor=org.broadleafcommerce.common.util.sql.importsql.DemoMySqlSingleLineSqlCommandExtractor
 * blEventPU.hibernate.hbm2ddl.import_files_sql_extractor=org.broadleafcommerce.common.util.sql.importsql.DemoMySqlSingleLineSqlCommandExtractor}<br>
 * <p>
 * in properties file to run load scripts through this extractor
 */
public class DemoMySqlSingleLineSqlCommandExtractor extends SingleLineSqlScriptExtractor {

    @Serial
    private static final long serialVersionUID = 1L;

    protected static final Pattern TABLE_NAME_PATTERN = Pattern.compile(
            "\\b(INTO|UPDATE|FROM|JOIN)(\\s+)([A-Za-z_][A-Za-z0-9_$]*)", Pattern.CASE_INSENSITIVE
    );

    @Override
    public List<String> extractCommands(Reader reader, Dialect dialect) {
        List<String> commands = super.extractCommands(reader, dialect);
        List<String> newCommands = new ArrayList<>(commands.size());
        for (String command : commands) {
            newCommands.add(uppercaseTableNames(command));
        }
        return newCommands;
    }

    /**
     * Uppercases the table names in the given command, skipping anything inside single or double quoted literals.
     */
    protected String uppercaseTableNames(String command) {
        StringBuilder result = new StringBuilder(command.length());
        StringBuilder segment = new StringBuilder();
        char quote = 0;
        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);
            if (quote != 0) {
                result.append(c);
                if (c == '\\' && i + 1 < command.length()) {
                    // backslash escape inside a literal, e.g. \' or \\
                    result.append(command.charAt(++i));
                } else if (c == quote) {
                    if (i + 1 < command.length() && command.charAt(i + 1) == quote) {
                        // doubled quote inside a literal, e.g. 'Blair''s'
                        result.append(command.charAt(++i));
                    } else {
                        quote = 0;
                    }
                }
            } else if (c == '\'' || c == '"') {
                result.append(uppercaseTableNamesInSqlSegment(segment.toString()));
                segment.setLength(0);
                result.append(c);
                quote = c;
            } else {
                segment.append(c);
            }
        }
        result.append(uppercaseTableNamesInSqlSegment(segment.toString()));
        return result.toString();
    }

    protected String uppercaseTableNamesInSqlSegment(String segment) {
        Matcher matcher = TABLE_NAME_PATTERN.matcher(segment);
        StringBuilder result = new StringBuilder(segment.length());
        while (matcher.find()) {
            String replacement = matcher.group(1) + matcher.group(2) + matcher.group(3).toUpperCase(Locale.ROOT);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

}
