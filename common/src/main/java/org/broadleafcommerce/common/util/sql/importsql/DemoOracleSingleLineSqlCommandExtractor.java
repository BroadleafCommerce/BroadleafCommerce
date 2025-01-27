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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a utility class that is only meant to be used for testing the BLC demo on Oracle. In our current
 * import sql files, there are a number of value declarations that are incompatible with Oracle. This
 * custom extractor takes care of transforming those values into something Oracle understands.
 *
 * @author Jeff Fischer
 */
public class DemoOracleSingleLineSqlCommandExtractor extends SingleLineSqlScriptExtractor {

    public static final String TRUE = "1";
    public static final String FALSE = "0";
    private static final SupportLogger LOGGER = SupportLogManager.getLogger(
            "UserOverride", DemoOracleSingleLineSqlCommandExtractor.class
    );
    private static final String BOOLEANTRUEMATCH = "(?i)(true)";
    private static final String BOOLEANFALSEMATCH = "(?i)(false)";
    private static final String TIMESTAMPMATCH = "(?<!\\{ts\\s)('\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}')";
    protected boolean alreadyRun = false;

    @Override
    public List<String> extractCommands(Reader reader, Dialect dialect) {
        if (!alreadyRun) {
            alreadyRun = true;
            LOGGER.support("Converting hibernate.hbm2ddl.import_files sql statements for compatibility with Oracle");
        }

        List<String> statements = super.extractCommands(reader, dialect);
        statements = handleBooleans(statements);

        //remove Oracle incompatible - multi-row inserts
        List<String> stringList = new ArrayList<>(statements); //Arrays.asList is immutable
        int j = 0;
        for (String statement : statements) {
            if (statement.matches(".*[)]\\s*[,].*")) {
                int pos = statement.toUpperCase().indexOf("VALUES ") + "VALUES ".length();
                String prefix = statement.substring(0, pos);
                stringList.remove(j);
                String values = statement.substring(pos, statement.length());
                String[] tokens = values.split("[)]\\s*[,]\\s*[(]");
                String[] newStatements = new String[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    String suffix = tokens[i];
                    if (!suffix.startsWith("(")) {
                        suffix = "(" + suffix;
                    }
                    if (!suffix.endsWith(")")) {
                        suffix += ")";
                    }
                    newStatements[i] = prefix + suffix;
                }
                stringList.addAll(j, Arrays.asList(newStatements));
                j += tokens.length;
            } else {
                j++;
            }
        }

        //Address raw string dates, if any, for Oracle
        Pattern pattern = Pattern.compile(TIMESTAMPMATCH);
        List<String> result = new ArrayList<>(stringList.size());
        for (String statement : stringList) {
            Matcher matcher = pattern.matcher(statement);
            while (matcher.find()) {
                String date = matcher.group(1);
                statement = statement.substring(0, statement.indexOf(date)) + "{ts " + date + "}" +
                        statement.substring(statement.indexOf(date) + date.length(), statement.length());
            }

            // Any MySQL-specific newlines replace with newline character concatenation
            statement = statement.replaceAll(
                    DemoPostgresSingleLineSqlCommandExtractor.NEWLINE_REPLACEMENT_REGEX,
                    "' || CHR(13) || CHR(10) || '"
            );
            // Any MySQL CHAR functions with CHR
            Pattern charPattern = Pattern.compile("CHAR\\((\\d+)\\)");
            Matcher charMatcher = charPattern.matcher(statement);
            if (charMatcher.find()) {
                String charCode = charMatcher.group(1);
                statement = charMatcher.replaceAll("CHR(" + charCode + ")");
            }

            // replace double backslashes with single, since all strings in oracle are literal
            statement = statement.replace("\\\\", "\\");

            result.add(statement);
        }

        return result;
    }

    protected List<String> handleBooleans(List<String> statements) {
        List<String> result = new ArrayList<>(statements.size());
        for (String statement : statements) {
            //try start matches
            String fixed = statement.replaceAll(BOOLEANTRUEMATCH + "\\s*[,]", TRUE + ",");
            fixed = fixed.replaceAll(BOOLEANFALSEMATCH + "\\s*[,]", FALSE + ",");

            //try middle matches
            fixed = fixed.replaceAll("[,]\\s*" + BOOLEANTRUEMATCH + "\\s*[,]", "," + TRUE + ",");
            fixed = fixed.replaceAll("[,]\\s*" + BOOLEANFALSEMATCH + "\\s*[,]", "," + FALSE + ",");

            //try end matches
            fixed = fixed.replaceAll("[,]\\s*" + BOOLEANTRUEMATCH, "," + TRUE);
            fixed = fixed.replaceAll("[,]\\s*" + BOOLEANFALSEMATCH, "," + FALSE);

            //try matches for updates
            fixed = fixed.replaceAll("[=]\\s*" + BOOLEANTRUEMATCH, "=" + TRUE);
            fixed = fixed.replaceAll("[=]\\s*" + BOOLEANFALSEMATCH, "=" + FALSE);
            result.add(fixed);
        }
        return result;
    }

}
