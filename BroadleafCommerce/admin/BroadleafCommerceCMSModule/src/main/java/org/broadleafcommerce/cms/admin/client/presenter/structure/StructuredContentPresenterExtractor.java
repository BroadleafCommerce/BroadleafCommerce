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

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.FilterBuilder;
import org.broadleafcommerce.cms.admin.client.datasource.structure.StructuredContentItemCriteriaListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.view.structure.StructuredContentDisplay;
import org.broadleafcommerce.openadmin.client.translation.AdvancedCriteriaToMVELTranslator;
import org.broadleafcommerce.openadmin.client.translation.IncompatibleMVELTranslationException;
import org.broadleafcommerce.openadmin.client.view.dynamic.ItemBuilderDisplay;

/**
 * 
 * @author jfischer
 *
 */
public class StructuredContentPresenterExtractor {

    private static Map<FilterType, String> MVELKEYWORDMAP = new HashMap<FilterType, String>();
	static {
		MVELKEYWORDMAP.put(FilterType.PRODUCT, "product");
		MVELKEYWORDMAP.put(FilterType.ORDER_ITEM, "discreteOrderItem");
		MVELKEYWORDMAP.put(FilterType.REQUEST, "request");
		MVELKEYWORDMAP.put(FilterType.CUSTOMER, "customer");
        MVELKEYWORDMAP.put(FilterType.TIME, "time");
	}

	private static final AdvancedCriteriaToMVELTranslator TRANSLATOR = new AdvancedCriteriaToMVELTranslator();
	
	protected StructuredContentPresenter presenter;
	
	public StructuredContentPresenterExtractor(StructuredContentPresenter presenter) {
		this.presenter = presenter;
	}

	protected StructuredContentDisplay getDisplay() {
		return presenter.getDisplay();
	}
	
	public void removeItemQualifer(final ItemBuilderDisplay builder) {
		if (builder.getRecord() != null) {
			presenter.getPresenterSequenceSetupManager().getDataSource("scItemCriteriaDS").removeData(builder.getRecord(), new DSCallback() {
				public void execute(DSResponse response, Object rawData, DSRequest request) {
					getDisplay().removeItemBuilder(builder);
				}
			});
		} else {
			getDisplay().removeItemBuilder(builder);
		}
	}

    protected void extractData(final Record selectedRecord, Map<String, Object> dirtyValues, String property, FilterBuilder filterBuilder, String keyWord) throws IncompatibleMVELTranslationException {
		setData(selectedRecord, property, TRANSLATOR.createMVEL(keyWord, filterBuilder.getCriteria(), filterBuilder.getDataSource()), dirtyValues);
	}
	
	protected void setData(Record record, String fieldName, Object value, Map<String, Object> dirtyValues) {
        String attr = record.getAttribute(fieldName);
        String val = value==null?null:String.valueOf(value);
		if (attr != val && (attr == null || val == null || !attr.equals(val))) {
			record.setAttribute(fieldName, value);
			dirtyValues.put(fieldName, value);
		}
	}
	
	public void applyData(final Record selectedRecord) {
		try {
			final Map<String, Object> dirtyValues = new HashMap<String, Object>();

            extractData(selectedRecord, dirtyValues, StructuredContentPresenterInitializer.ATTRIBUTEMAP.get(FilterType.CUSTOMER), getDisplay().getCustomerFilterBuilder(), MVELKEYWORDMAP.get(FilterType.CUSTOMER));
            extractData(selectedRecord, dirtyValues, StructuredContentPresenterInitializer.ATTRIBUTEMAP.get(FilterType.PRODUCT), getDisplay().getProductFilterBuilder(), MVELKEYWORDMAP.get(FilterType.PRODUCT));
            extractData(selectedRecord, dirtyValues, StructuredContentPresenterInitializer.ATTRIBUTEMAP.get(FilterType.REQUEST), getDisplay().getRequestFilterBuilder(), MVELKEYWORDMAP.get(FilterType.REQUEST));
            extractData(selectedRecord, dirtyValues, StructuredContentPresenterInitializer.ATTRIBUTEMAP.get(FilterType.TIME), getDisplay().getTimeFilterBuilder(), MVELKEYWORDMAP.get(FilterType.TIME));
			
			extractQualifierData(selectedRecord, true, dirtyValues);
			
			DSRequest requestProperties = new DSRequest();
			requestProperties.setAttribute("dirtyValues", dirtyValues);
			
			getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getDataSource().updateData(selectedRecord, new DSCallback() {
				public void execute(DSResponse response, Object rawData, DSRequest request) {
					try {
						extractQualifierData(selectedRecord, false, dirtyValues);
						getDisplay().getDynamicFormDisplay().getSaveButton().disable();
					} catch (IncompatibleMVELTranslationException e) {
						SC.warn(e.getMessage());
					}
				}
			}, requestProperties);
		} catch (IncompatibleMVELTranslationException e) {
			SC.warn(e.getMessage());
		}
	}
	
	protected void extractQualifierData(final Record selectedRecord, boolean isValidation, Map<String, Object> dirtyValues) throws IncompatibleMVELTranslationException {
		for (final ItemBuilderDisplay builder : getDisplay().getItemBuilderViews()) {
            if (builder.getDirty()) {
                String temper = builder.getItemQuantity().getValue().toString();
                Integer quantity = Integer.parseInt(temper);
                String mvel = TRANSLATOR.createMVEL(MVELKEYWORDMAP.get(FilterType.ORDER_ITEM), builder.getItemFilterBuilder().getCriteria(), builder.getItemFilterBuilder().getDataSource());
                if (!isValidation) {
                    if (builder.getRecord() != null) {
                        setData(builder.getRecord(), "quantity", quantity, dirtyValues);
                        setData(builder.getRecord(), "orderItemMatchRule", mvel, dirtyValues);
                        presenter.getPresenterSequenceSetupManager().getDataSource("offerItemCriteriaDS").updateData(builder.getRecord(), new DSCallback() {
                            public void execute(DSResponse response, Object rawData, DSRequest request) {
                                builder.setDirty(false);
                            }
                        });
                    } else {
                        final Record temp = new Record();
                        temp.setAttribute("quantity", quantity);
                        temp.setAttribute("orderItemMatchRule", mvel);
                        temp.setAttribute("_type", new String[]{presenter.getPresenterSequenceSetupManager().getDataSource("scItemCriteriaDS").getDefaultNewEntityFullyQualifiedClassname()});
                        temp.setAttribute(StructuredContentItemCriteriaListDataSourceFactory.foreignKeyName, presenter.getPresenterSequenceSetupManager().getDataSource("structuredContentDS").getPrimaryKeyValue(selectedRecord));
                        temp.setAttribute("id", "");
                        presenter.getPresenterSequenceSetupManager().getDataSource("scItemCriteriaDS").addData(temp, new DSCallback() {
                            public void execute(DSResponse response, Object rawData, DSRequest request) {
                                builder.setDirty(false);
                                builder.setRecord(temp);
                            }
                        });
                    }
                }
            }
        }
	}
}
