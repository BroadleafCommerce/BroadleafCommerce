/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.persistence.transaction;

import org.broadleafcommerce.common.util.FormatUtil;
import org.springframework.transaction.TransactionDefinition;

import java.io.IOException;
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

    public TransactionInfo(EntityManager em, TransactionDefinition definition, boolean isCompressed) {
        this.entityManager = new WeakReference<EntityManager>(em);
        this.definition = new WeakReference<TransactionDefinition>(definition);
        this.isCompressed = isCompressed;
        initialize();
    }

    protected WeakReference<EntityManager> entityManager;
    protected WeakReference<TransactionDefinition> definition;
    protected String beginStack;
    protected CompressedItem compressedBeginStack;
    protected WeakReference<Thread> thread;
    protected String threadName;
    protected String threadId;
    protected Long startTime;
    protected List<String> queries = new ArrayList<String>();
    protected List<CompressedItem> compressedQueries = new ArrayList<CompressedItem>();
    protected Map<String, String> additionalParams = new HashMap<String, String>();
    protected String currentStackElement;
    protected Long lastLogTime;
    protected Long stuckThreadStartTime;
    protected Boolean faultStateDetected = false;
    protected Boolean isCompressed = true;

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

    public CompressedItem getCompressedBeginStack() {
        return compressedBeginStack;
    }

    public void setCompressedBeginStack(CompressedItem compressedBeginStack) {
        this.compressedBeginStack = compressedBeginStack;
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

    public List<CompressedItem> getCompressedQueries() {
        return compressedQueries;
    }

    public void setCompressedQueries(List<CompressedItem> compressedQueries) {
        this.compressedQueries = compressedQueries;
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
        boolean isLogged = false;
        if (isCompressed) {
            try {
                if (getCompressedQueries().isEmpty()) {
                    getCompressedQueries().add(new CompressedItem("\n" + statement + "\n"));
                } else {
                    getCompressedQueries().add(new CompressedItem(statement + "\n"));
                }
                isLogged = true;
            } catch (IOException e) {
                //do nothing
            }
        }
        if (!isLogged) {
            if (getQueries().isEmpty()) {
                getQueries().add("\n" + statement + "\n");
            } else {
                getQueries().add(statement + "\n");
            }
        }
        lastLogTime = System.currentTimeMillis();
    }

    protected void initialize() {
        RuntimeException e = new RuntimeException();
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        boolean isLogged = false;
        if (isCompressed) {
            try {
                compressedBeginStack = new CompressedItem(sw.toString());
                isLogged = true;
            } catch (IOException e1) {
                //do nothing
            }
        }
        if (!isLogged) {
            beginStack = sw.toString();
        }
        thread = new WeakReference<Thread>(Thread.currentThread());
        threadName = thread.get().getName();
        threadId = String.valueOf(thread.get().getId());
        startTime = System.currentTimeMillis();
        lastLogTime = startTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TransactionInfo{");
        sb.append("threadName='").append(threadName).append('\'').append("\n");
        sb.append(", threadId=").append(threadId).append("\n");
        if (isCompressed) {
            sb.append(", queries=").append(compressedQueries);
        } else {
            sb.append(", queries=").append(queries);
        }
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
        if (isCompressed) {
            sb.append(", beginStack='").append(compressedBeginStack).append('\'');
        } else {
            sb.append(", beginStack='").append(beginStack).append('\'');
        }
        sb.append('}');
        return sb.toString();
    }
}
