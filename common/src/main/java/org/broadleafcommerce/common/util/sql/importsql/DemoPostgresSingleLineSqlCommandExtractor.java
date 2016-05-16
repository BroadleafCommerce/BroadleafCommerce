/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.util.sql.importsql;

import org.broadleafcommerce.common.logging.SupportLogManager;
import org.broadleafcommerce.common.logging.SupportLogger;
import org.hibernate.tool.hbm2ddl.SingleLineSqlCommandExtractor;

import java.io.Reader;

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
            newCommands[i] = newCommand;
            i++;
        }
        return newCommands;
    }

}
