/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.logging;

import com.google.gwt.logging.shared.RemoteLoggingService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
* Converts a java util logging logRecord into a commmons logging event 
*/
@Service("blLog4JRemoteGwtLoggingService")
public class Log4JRemoteGwtLoggingService implements RemoteLoggingService {

    /**
     * 
     */
    private static final long serialVersionUID = 2885716139420208787L;
    private static final Log LOG = LogFactory.getLog(Log4JRemoteGwtLoggingService.class);

    @Override
    public String logOnServer(LogRecord record) {

        Level level = record.getLevel();
        String message;
        message=record.getMessage();
        if (Level.INFO.equals(level)) {
            LOG.info(message,record.getThrown());
        } else if (Level.SEVERE.equals(level)) {
            LOG.error(message,record.getThrown());
        } else if (Level.WARNING.equals(level)) {
            LOG.warn(message,record.getThrown());
        } else if (Level.FINE.equals(level)) {
            LOG.debug(message,record.getThrown());
        }else   {
            LOG.error(message,record.getThrown());
        }

        return null;
    }

}
