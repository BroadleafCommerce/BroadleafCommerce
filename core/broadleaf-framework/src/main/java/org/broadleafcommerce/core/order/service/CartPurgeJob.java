package org.broadleafcommerce.core.order.service;

import java.util.Map;

import javax.annotation.Resource;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Jeff Fischer
 */
public class CartPurgeJob implements Job {

    protected Map<String, String> config;

    @Resource(name="blResourcePurgeService")
    protected ResourcePurgeService resourcePurgeService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        resourcePurgeService.purgeCarts(config);
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }
}
