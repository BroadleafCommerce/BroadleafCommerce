/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.common.persistence.transaction;

import org.broadleafcommerce.common.util.FormatUtil;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.transaction.TransactionDefinition;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
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

    public TransactionInfo(EntityManager em, TransactionDefinition definition, boolean isCompressed, boolean isAbbreviated,
                           int abbreviatedLength, boolean decompressStatementForLog, int maxQueryListLength) {
        this.entityManager = new WeakReference<EntityManager>(em);
        this.definition = new WeakReference<TransactionDefinition>(definition);
        this.isCompressed = isCompressed;
        this.isAbbreviated = isAbbreviated;
        this.abbreviatedLength = abbreviatedLength;
        this.decompressStatementForLog = decompressStatementForLog;
        this.maxQueryListLength = maxQueryListLength;
        queries = new LinkedBlockingQueue<String>(maxQueryListLength==-1?Integer.MAX_VALUE:maxQueryListLength);
        compressedQueries = new LinkedBlockingQueue<CompressedItem>(maxQueryListLength==-1?Integer.MAX_VALUE:maxQueryListLength);
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
    protected LinkedBlockingQueue<String> queries;
    protected LinkedBlockingQueue<CompressedItem> compressedQueries;
    protected Map<String, String> additionalParams = new HashMap<String, String>();
    protected String currentStackElement;
    protected Long lastLogTime;
    protected Long stuckThreadStartTime;
    protected Boolean faultStateDetected = false;
    protected Boolean isCompressed = true;
    protected String requestContext;
    protected Boolean isAbbreviated;
    protected Integer abbreviatedLength;
    protected Boolean decompressStatementForLog;
    protected Integer maxQueryListLength;
    protected Integer totalQueries = 0;

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

    public LinkedBlockingQueue<String> getQueries() {
        return queries;
    }

    public void setQueries(LinkedBlockingQueue<String> queries) {
        this.queries = queries;
    }

    public LinkedBlockingQueue<CompressedItem> getCompressedQueries() {
        return compressedQueries;
    }

    public void setCompressedQueries(LinkedBlockingQueue<CompressedItem> compressedQueries) {
        this.compressedQueries = compressedQueries;
    }

    public Boolean getDecompressStatementForLog() {
        return decompressStatementForLog;
    }

    public void setDecompressStatementForLog(Boolean decompressStatementForLog) {
        this.decompressStatementForLog = decompressStatementForLog;
    }

    public Boolean getFaultStateDetected() {
        return faultStateDetected;
    }

    public void setFaultStateDetected(Boolean faultStateDetected) {
        this.faultStateDetected = faultStateDetected;
    }

    public Boolean getAbbreviated() {
        return isAbbreviated;
    }

    public void setAbbreviated(Boolean abbreviated) {
        isAbbreviated = abbreviated;
    }

    public Integer getAbbreviatedLength() {
        return abbreviatedLength;
    }

    public void setAbbreviatedLength(Integer abbreviatedLength) {
        this.abbreviatedLength = abbreviatedLength;
    }

    public Integer getMaxQueryListLength() {
        return maxQueryListLength;
    }

    public void setMaxQueryListLength(Integer maxQueryListLength) {
        this.maxQueryListLength = maxQueryListLength;
    }

    public void clear() {
        entityManager.clear();
        thread.clear();
        definition.clear();
    }

    public void logStatement(String statement) {
        String logItem = statement;
        if (isAbbreviated && logItem.length() > abbreviatedLength) {
            logItem = logItem.substring(0, abbreviatedLength);
        }
        boolean isLogged = false;
        if (isCompressed) {
            try {
                if (getCompressedQueries().isEmpty()) {
                    getCompressedQueries().add(new CompressedItem("\n" + logItem + "\n", decompressStatementForLog));
                } else {
                    if (getCompressedQueries().remainingCapacity() == 0) {
                        getCompressedQueries().poll();
                    }
                    getCompressedQueries().add(new CompressedItem(logItem + "\n", decompressStatementForLog));
                }
                isLogged = true;
            } catch (IOException e) {
                //do nothing
            }
        }
        if (!isLogged) {
            if (getQueries().isEmpty()) {
                getQueries().add("\n" + logItem + "\n");
            } else {
                if (getQueries().remainingCapacity() == 0) {
                    getQueries().poll();
                }
                getQueries().add(logItem + "\n");
            }
        }
        lastLogTime = System.currentTimeMillis();
        totalQueries++;
    }

    protected void initialize() {
        RuntimeException e = new RuntimeException();
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        boolean isLogged = false;
        if (isCompressed) {
            try {
                compressedBeginStack = new CompressedItem(sw.toString(), true);
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
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        if (context != null) {
            requestContext = context.createLightWeightCloneJson();
        } else {
            requestContext = "none";
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TransactionInfo{");
        sb.append("threadName='").append(threadName).append('\'').append("\n");
        sb.append(", threadId=").append(threadId).append("\n");
        EntityManager em = getEntityManager();
        if (em != null) {
            sb.append(", entityManager='").append(em.hashCode()).append('\'').append("\n");
        }
        if (isCompressed) {
            if (!decompressStatementForLog) {
                try {
                    StringBuilder queryBuilder = new StringBuilder();
                    for (CompressedItem compressedItem : compressedQueries) {
                        queryBuilder.append(compressedItem.decompress());
                    }
                    CompressedItem allQueries = new CompressedItem(queryBuilder.toString(), false);
                    sb.append(", queries=").append(allQueries);
                } catch (IOException e) {
                    sb.append(", queries='Unable to build compressed representation of queries because of an exception: ").append(e.getMessage()).append('\'');
                }
            } else {
                sb.append(", queries=").append(compressedQueries);
            }
        } else {
            sb.append(", queries=").append(queries);
        }
        sb.append(", totalQueries=").append(totalQueries).append("\n");
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
            sb.append(", beginStack='").append(compressedBeginStack).append('\'').append("\n");
        } else {
            sb.append(", beginStack='").append(beginStack).append('\'').append("\n");
        }
        sb.append(", requestContext='").append(requestContext).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
