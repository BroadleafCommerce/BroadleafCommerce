/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.admin.client.presenter.promotion;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class StateManager {

    private Map<CriteriaType, Integer> watchedItems = new HashMap<CriteriaType, Integer>();
    private StateFinishedCallback cb;

    public void setWatchedItem(CriteriaType criteriaType, Integer quantity) {
        watchedItems.put(criteriaType, quantity);
    }

    public void setStateFinishedCallback(StateFinishedCallback cb) {
        this.cb = cb;
    }

    public void clear() {
        watchedItems.clear();
    }

    public void start() {
        checkStatus();
    }

    public void finishWatchedItem(CriteriaType criteriaType) {
        Integer newValue = watchedItems.get(criteriaType) - 1;
        watchedItems.put(criteriaType, newValue);

        checkStatus();
    }

    protected void checkStatus() {
        boolean isFinished = true;
        for (Map.Entry<CriteriaType, Integer> entry : watchedItems.entrySet()) {
            if (entry.getValue() > 0) {
                isFinished = false;
                break;
            }
        }

        if (isFinished && cb != null) {
            cb.finished();
        }
    }
}
