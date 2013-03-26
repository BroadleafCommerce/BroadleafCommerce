package org.broadleafcommerce.core.util;

import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

/**
 * Simple workflow activity to simulate an amount of latency introduced by communicating
 * with a third party provider (e.g. credit card processing). Useful for load testing.
 *
 * @author Jeff Fischer
 */
public class ThirdPartyInteractionLatencySimulationActivity extends BaseActivity<ProcessContext> {

    private long waitTime = 1000L;

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        try {
            Thread.sleep(waitTime);
        } catch (Throwable e) {
            //do nothing
        }

        return context;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }
}
