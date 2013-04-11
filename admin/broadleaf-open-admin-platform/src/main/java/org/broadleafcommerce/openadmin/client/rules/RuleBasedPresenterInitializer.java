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

package org.broadleafcommerce.openadmin.client.rules;

import org.broadleafcommerce.common.presentation.client.RuleType;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.translation.IncompatibleMVELTranslationException;
import org.broadleafcommerce.openadmin.client.translation.MVELToAdvancedCriteriaTranslator;
import org.broadleafcommerce.openadmin.client.view.dynamic.ItemBuilderDisplay;

import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.widgets.form.FilterBuilder;

import java.util.HashMap;
import java.util.Map;

public abstract class RuleBasedPresenterInitializer<U extends DynamicEntityPresenter,V extends RulesDisplay> {

    public static Map<RuleType, String> ATTRIBUTEMAP = new HashMap<RuleType, String>();


    protected static final MVELToAdvancedCriteriaTranslator TRANSLATOR = new MVELToAdvancedCriteriaTranslator();
    protected U presenter;

    public RuleBasedPresenterInitializer() {
        super();
    }

    protected V getDisplay() {
        return (V) presenter.getDisplay();
    }

    public void initFilterBuilder(FilterBuilder filterBuilder, String rule) {
        filterBuilder.clearCriteria();
        if (rule != null) {
            try {
                AdvancedCriteria myCriteria = TRANSLATOR.createAdvancedCriteria(rule, filterBuilder.getDataSource());
                if (myCriteria != null) {
                    filterBuilder.setCriteria(myCriteria);
                }
            } catch (IncompatibleMVELTranslationException e) {
                throw new RuntimeException(BLCMain.getMessageManager().getString("mvelTranslationProblem"), e);
            }
        }
    }


    protected abstract void bindItemBuilderEvents(ItemBuilderDisplay display);
    
}