package org.broadleafcommerce.common.logging;



import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import static ch.qos.logback.classic.Level.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;



/**
 * Format a LoggingEvent as a single line JSON object in order to be easily consumed and read in GCP
 *
 * Based on GKE fluentd ingestion links:
 * https://stackoverflow.com/questions/44164730/gke-stackdriver-java-logback-logging-format
 * https://stackoverflow.com/questions/37420400/how-do-i-map-my-java-app-logging-events-to-corresponding-cloud-logging-event-lev/39779646#39779646
 * https://cloud.google.com/error-reporting/docs/formatting-error-messages#json_representation
 * http://google-cloud-python.readthedocs.io/en/latest/logging-handlers-container-engine.html
 * http://google-cloud-python.readthedocs.io/en/latest/_modules/google/cloud/logging/handlers/container_engine.html#ContainerEngineHandler.format
 * https://github.com/GoogleCloudPlatform/google-cloud-python/blob/master/logging/google/cloud/logging/handlers/_helpers.py
 * https://cloud.google.com/logging/docs/reference/v2/rest/v2/LogEntry
 */
public class GCPCloudLoggingJSONLayout extends PatternLayout {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String doLayout(ILoggingEvent event) {
        String formattedMessage = super.doLayout(event);
        return doLayoutInternal(formattedMessage, event);
    }

    /**
     * For testing without having to deal wth the complexity of super.doLayout()
     * Uses formattedMessage instead of event.getMessage()
     */
    private String doLayoutInternal(String formattedMessage, ILoggingEvent event) {
        GCPCloudLoggingEvent gcpLogEvent =
                new GCPCloudLoggingEvent(formattedMessage, convertTimestampToGCPLogTimestamp(event.getTimeStamp()),
                        mapLevelToGCPLevel(event.getLevel()), event.getThreadName());

        try {
            // Add a newline so that each JSON log entry is on its own line.
            // Note that it is also important that the JSON log entry does not span multiple lines.
            return objectMapper.writeValueAsString(gcpLogEvent) + "\n";
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    private static GCPCloudLoggingEvent.GCPCloudLoggingTimestamp convertTimestampToGCPLogTimestamp(
            long millisSinceEpoch) {
        int nanos =
                ((int) (millisSinceEpoch % 1000)) * 1_000_000; // strip out just the milliseconds and convert to nanoseconds
        long seconds = millisSinceEpoch / 1000L; // remove the milliseconds
        return new GCPCloudLoggingEvent.GCPCloudLoggingTimestamp(seconds, nanos);
    }

    private static String mapLevelToGCPLevel(Level level) {
        switch (level.toInt()) {
            case TRACE_INT:
                return "TRACE";
            case DEBUG_INT:
                return "DEBUG";
            case INFO_INT:
                return "INFO";
            case WARN_INT:
                return "WARN";
            case ERROR_INT:
                return "ERROR";
            default:
                return null; /* This should map to no level in GCP Cloud Logging */
        }
    }

    /* Must be public for Jackson JSON conversion */
    public static class GCPCloudLoggingEvent {
        private String message;
        private GCPCloudLoggingTimestamp timestamp;
        private String thread;
        private String severity;

        public GCPCloudLoggingEvent(String message, GCPCloudLoggingTimestamp timestamp, String severity,
                                    String thread) {
            super();
            this.message = message;
            this.timestamp = timestamp;
            this.thread = thread;
            this.severity = severity;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public GCPCloudLoggingTimestamp getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(GCPCloudLoggingTimestamp timestamp) {
            this.timestamp = timestamp;
        }

        public String getThread() {
            return thread;
        }

        public void setThread(String thread) {
            this.thread = thread;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        /* Must be public for JSON marshalling logic */
        public static class GCPCloudLoggingTimestamp {
            private long seconds;
            private int nanos;

            public GCPCloudLoggingTimestamp(long seconds, int nanos) {
                super();
                this.seconds = seconds;
                this.nanos = nanos;
            }

            public long getSeconds() {
                return seconds;
            }

            public void setSeconds(long seconds) {
                this.seconds = seconds;
            }

            public int getNanos() {
                return nanos;
            }

            public void setNanos(int nanos) {
                this.nanos = nanos;
            }

        }
    }

    @Override
    public Map<String, String> getDefaultConverterMap() {
        return PatternLayout.defaultConverterMap;
    }
}
