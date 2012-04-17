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

package org.broadleafcommerce.cms.admin.client.presenter.structure;

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.FilterBuilder;
import org.broadleafcommerce.cms.admin.client.view.structure.StructuredContentDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.translation.IncompatibleMVELTranslationException;
import org.broadleafcommerce.openadmin.client.translation.MVELToAdvancedCriteriaTranslator;
import org.broadleafcommerce.openadmin.client.view.dynamic.ItemBuilderDisplay;

/**
 * 
 * @author jfischer
 *
 */
public class StructuredContentPresenterInitializer {

    public static Map<FilterType, String> ATTRIBUTEMAP = new HashMap<FilterType, String>();
	static {
		ATTRIBUTEMAP.put(FilterType.PRODUCT, "productRule");
		ATTRIBUTEMAP.put(FilterType.REQUEST, "requestRule");
		ATTRIBUTEMAP.put(FilterType.CUSTOMER, "customerRule");
        ATTRIBUTEMAP.put(FilterType.TIME, "timeRule");
	}

	private static final MVELToAdvancedCriteriaTranslator TRANSLATOR = new MVELToAdvancedCriteriaTranslator();
	
	protected StructuredContentPresenter presenter;
	protected DynamicEntityDataSource offerItemCriteriaDataSource;
	protected DynamicEntityDataSource orderItemDataSource;
	
	public StructuredContentPresenterInitializer(StructuredContentPresenter presenter, DynamicEntityDataSource offerItemCriteriaDataSource, DynamicEntityDataSource orderItemDataSource) {
		this.presenter = presenter;
		this.offerItemCriteriaDataSource = offerItemCriteriaDataSource;
		this.orderItemDataSource = orderItemDataSource;
	}
	
	protected StructuredContentDisplay getDisplay() {
		return presenter.getDisplay();
	}
	
	public void initSection(Record selectedRecord, boolean disabled) {
        initFilterBuilder(getDisplay().getCustomerFilterBuilder(), selectedRecord.getAttribute(ATTRIBUTEMAP.get(FilterType.CUSTOMER)));
        initFilterBuilder(getDisplay().getProductFilterBuilder(), selectedRecord.getAttribute(ATTRIBUTEMAP.get(FilterType.PRODUCT)));
        initFilterBuilder(getDisplay().getRequestFilterBuilder(), selectedRecord.getAttribute(ATTRIBUTEMAP.get(FilterType.REQUEST)));
        initFilterBuilder(getDisplay().getTimeFilterBuilder(), selectedRecord.getAttribute(ATTRIBUTEMAP.get(FilterType.TIME)));
		initItemQualifiers(selectedRecord, disabled);
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

	public void initItemQualifiers(final Record selectedRecord, final boolean disabled) {
		Criteria relationshipCriteria = offerItemCriteriaDataSource.createRelationshipCriteria(offerItemCriteriaDataSource.getPrimaryKeyValue(selectedRecord));
		offerItemCriteriaDataSource.fetchData(relationshipCriteria, new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
            getDisplay().removeAllItemBuilders();
            for (Record record : response.getData()) {
                if (Integer.parseInt(record.getAttribute("quantity")) > 0) {
                    final ItemBuilderDisplay display = getDisplay().addItemBuilder(orderItemDataSource);
                    display.setDirty(false);
                    if (disabled) {
                        display.disable();
                    }
                    presenter.bindItemBuilderEvents(display);
                    display.getItemFilterBuilder().clearCriteria();
                    display.setRecord(record);
                    display.getItemQuantity().setValue(Integer.parseInt(record.getAttribute("quantity")));
                    try {
                        display.getItemFilterBuilder().setVisible(true);
                        display.getRawItemForm().setVisible(false);
                        AdvancedCriteria myCriteria = TRANSLATOR.createAdvancedCriteria(record.getAttribute("orderItemMatchRule"), orderItemDataSource);
                        if (myCriteria != null) {
                            display.getItemFilterBuilder().setCriteria(myCriteria);
                        }
                    } catch (IncompatibleMVELTranslationException e) {
                        throw new RuntimeException(BLCMain.getMessageManager().getString("mvelTranslationProblem"), e);
                    }
                    display.getRemoveButton().addClickHandler(new ClickHandler() {
                        public void onClick(ClickEvent event) {
                            getDisplay().removeItemBuilder(display);
                        }
                    });
                }
            }
			}
		});
	}
	
}
