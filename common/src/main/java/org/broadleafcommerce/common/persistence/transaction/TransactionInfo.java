package org.broadleafcommerce.common.persistence.transaction;

import org.broadleafcommerce.common.util.FormatUtil;
import org.springframework.transaction.TransactionDefinition;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

/**
 * POJO for in-progress transaction information. Includes thread and query information.
 *
 * @author Jeff Fischer
 */
public class TransactionInfo {

    public TransactionInfo() {
        initialize();
    }

    public TransactionInfo(EntityManager em, TransactionDefinition definition) {
        this.entityManager = new WeakReference<EntityManager>(em);
        this.definition = new WeakReference<TransactionDefinition>(definition);
        initialize();
    }

    protected WeakReference<EntityManager> entityManager;
    protected WeakReference<TransactionDefinition> definition;
    protected String beginStack;
    protected WeakReference<Thread> thread;
    protected String threadName;
    protected String threadId;
    protected Long startTime;
    protected Long expiryStartTime;
    protected List<String> queries = new ArrayList<String>();
    protected Map<String, String> additionalParams = new HashMap<String, String>();
    protected String currentStackElement;
    protected Long lastLogTime;
    protected Long stuckThreadStartTime;
    protected Boolean faultStateDetected = false;

    public EntityManager getEntityManager() {
        return entityManager.get();
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = new WeakReference<EntityManager>(entityManager);
    }

    public TransactionDefinition getDefinition() {
        return definition.get();
    }

    public void setDefinition(TransactionDefinition definition) {
        this.definition = new WeakReference<TransactionDefinition>(definition);
    }

    public String getBeginStack() {
        return beginStack;
    }

    public void setBeginStack(String beginStack) {
        this.beginStack = beginStack;
    }

    public Thread getThread() {
        return thread.get();
    }

    public void setThread(Thread thread) {
        this.thread = new WeakReference<Thread>(thread);
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getCurrentStackElement() {
        return currentStackElement;
    }

    public void setCurrentStackElement(String currentStackElement) {
        this.currentStackElement = currentStackElement;
    }

    public Long getLastLogTime() {
        return lastLogTime;
    }

    public void setLastLogTime(Long lastLogTime) {
        this.lastLogTime = lastLogTime;
    }

    public Long getStuckThreadStartTime() {
        return stuckThreadStartTime;
    }

    public void setStuckThreadStartTime(Long stuckThreadStartTime) {
        this.stuckThreadStartTime = stuckThreadStartTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getExpiryStartTime() {
        return expiryStartTime;
    }

    public void setExpiryStartTime(Long expiryStartTime) {
        this.expiryStartTime = expiryStartTime;
    }

    public Map<String, String> getAdditionalParams() {
        return additionalParams;
    }

    public void setAdditionalParams(Map<String, String> additionalParams) {
        this.additionalParams = additionalParams;
    }

    public List<String> getQueries() {
        return queries;
    }

    public void setQueries(List<String> queries) {
        this.queries = queries;
    }

    public Boolean getFaultStateDetected() {
        return faultStateDetected;
    }

    public void setFaultStateDetected(Boolean faultStateDetected) {
        this.faultStateDetected = faultStateDetected;
    }

    public void clear() {
        entityManager.clear();
        thread.clear();
        definition.clear();
    }

    public void logStatement(String statement) {
        if (getQueries().isEmpty()) {
            getQueries().add("\n" + statement + "\n");
        } else {
            getQueries().add(statement + "\n");
        }
        lastLogTime = System.currentTimeMillis();
    }

    protected void initialize() {
        RuntimeException e = new RuntimeException();
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        beginStack = sw.toString();
        thread = new WeakReference<Thread>(Thread.currentThread());
        threadName = thread.get().getName();
        threadId = String.valueOf(thread.get().getId());
        startTime = System.currentTimeMillis();
        expiryStartTime = startTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TransactionInfo{");
        sb.append("threadName='").append(threadName).append('\'').append("\n");
        sb.append(", threadId=").append(threadId).append("\n");
        sb.append(", queries=").append(queries);
        sb.append(", additionalParams=").append(additionalParams).append("\n");
        if (startTime != null) {
            Date start = new Date(getStartTime());
            sb.append(", startTime=").append(FormatUtil.formatDateUsingW3C(start)).append("\n");
            Long endTime = System.currentTimeMillis();
            Date end = new Date(endTime);
            sb.append(", endTime=").append(FormatUtil.formatDateUsingW3C(end)).append("\n");
            Long duration = endTime - startTime;
            long hours = TimeUnit.MILLISECONDS.toHours(duration);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(hours);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(minutes);
            String durationString = String.format("%d hour, %d min, %d sec",
                    hours,
                    minutes,
                    seconds
            );
            sb.append(", duration=").append(durationString).append("\n");
        }
        sb.append(", beginStack='").append(beginStack).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
