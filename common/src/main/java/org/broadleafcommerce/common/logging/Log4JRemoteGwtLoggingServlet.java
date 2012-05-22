package org.broadleafcommerce.common.logging;

import java.util.logging.LogRecord;

import com.google.gwt.logging.shared.RemoteLoggingService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.util.logging.Level;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Converts a java util logging logRecord into a commmons logging event 
 */
public class Log4JRemoteGwtLoggingServlet extends RemoteServiceServlet
		implements RemoteLoggingService {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2885716139420208787L;
	private static final Log LOG = LogFactory.getLog(Log4JRemoteGwtLoggingServlet.class);

    @Override
    public String logOnServer(LogRecord record) {

        Level level = record.getLevel();
        String message;
        message=getThreadLocalRequest().getRemoteAddr()+":"+record.getMessage();
        if (Level.INFO.equals(level)) {
        	
            LOG.info(message);
        } else if (Level.SEVERE.equals(level)) {
        	LOG.error(message);
        } else if (Level.WARNING.equals(level)) {
        	LOG.warn(message);
        } else if (Level.FINE.equals(level)) {
        	LOG.debug(message);
        }else   {
        	LOG.error(message);
        }

        return null;
    }

}
