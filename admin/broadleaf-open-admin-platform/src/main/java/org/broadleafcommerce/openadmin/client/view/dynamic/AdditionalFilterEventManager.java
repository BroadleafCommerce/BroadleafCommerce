/*
 * Copyright 2008-2009 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.client.view.dynamic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.Timer;
import com.smartgwt.client.widgets.form.FilterBuilder;

/**
 * Because of a deficit in the current FilterBuilder from SmartGWT, the filter changed event does not fire when
 * a criteria is deleted from a FilterBuilder instance. Nor does it fire when the top-level operator changes.
 * This class sets up a polling mechanism with the capability to register an event handler per FilterBuilder
 * instance that will fire on these additional use cases.
 *
 * @author Jeff Fischer
 */
public class AdditionalFilterEventManager {

    protected Map<FilterBuilder, FilterBuilderCharacteristics> handlerMap = new HashMap<FilterBuilder, FilterBuilderCharacteristics>();
    protected Timer filterBuilderEventTimer;
    protected int pollingInterval = 500;

    public AdditionalFilterEventManager() {
        filterBuilderEventTimer = new Timer() {
            public void run() {
                for (Map.Entry<FilterBuilder, FilterBuilderCharacteristics> entry : handlerMap.entrySet()) {
                    CriteriaCharacteristics criteriaCharacteristics = getCharacteristics(entry.getKey().getCriteria().getValues());
                    if (entry.getValue().getCriteriaCharacteristics() == null) {
                        entry.getValue().setCriteriaCharacteristics(criteriaCharacteristics);
                    } else if (!criteriaCharacteristics.equals(entry.getValue().getCriteriaCharacteristics())) {
                        entry.getValue().setCriteriaCharacteristics(criteriaCharacteristics);
                        entry.getValue().getHandler().onAdditionalChangeEvent();
                    }
                }
                schedule(pollingInterval);
            }
        };
        filterBuilderEventTimer.schedule(pollingInterval);
    }

    protected CriteriaCharacteristics getCharacteristics(Map values) {
        CriteriaCharacteristics temp = new CriteriaCharacteristics();
        temp.setCriteriaSize(((List) values.get("criteria")).size());
        temp.setOperator((String) values.get("operator"));

        return temp;
    }

    public void addFilterBuilderAdditionalEventHandler(FilterBuilder filterBuilder, FilterBuilderAdditionalEventHandler handler) {
        FilterBuilderCharacteristics characteristics = new FilterBuilderCharacteristics();
        characteristics.setHandler(handler);
        handlerMap.put(filterBuilder, characteristics);
    }

    public void removeFilterBuilderAdditionalEventHandler(FilterBuilder filterBuilder) {
        handlerMap.remove(filterBuilder);
    }

    public void resetFilterState(FilterStateRunnable runnable) {
        filterBuilderEventTimer.cancel();
        for (Map.Entry<FilterBuilder, FilterBuilderCharacteristics> entry : handlerMap.entrySet()) {
            entry.getValue().setCriteriaCharacteristics(null);
        }
        runnable.run(new FilterRestartCallback() {
            @Override
            public void processComplete() {
                filterBuilderEventTimer.schedule(pollingInterval);
            }
        });
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
    }
}
