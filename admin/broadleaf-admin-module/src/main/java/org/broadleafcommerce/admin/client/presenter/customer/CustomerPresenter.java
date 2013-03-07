/*
 * Copyright 2008-2012 the original author or authors.
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

package org.broadleafcommerce.admin.client.presenter.customer;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import org.broadleafcommerce.admin.client.datasource.customer.ChallengeQuestionListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.customer.CustomerListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.locale.LocaleListDataSourceFactory;
import org.broadleafcommerce.admin.client.view.customer.CustomerDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.service.AbstractCallback;
import org.broadleafcommerce.openadmin.client.service.AppServices;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;

import java.util.HashMap;

/**
 * @author jfischer
 */
public class CustomerPresenter extends DynamicEntityPresenter implements Instantiable {

    protected HashMap<String, Object> library = new HashMap<String, Object>(10);

    @Override
    protected void changeSelection(final Record selectedRecord) {
        getDisplay().getUpdateLoginButton().enable();
    }

    @Override
    protected void addClicked() {
        initialValues.put("username", BLCMain.getMessageManager().getString("usernameDefault"));
        super.addClicked(BLCMain.getMessageManager().getString("newCustomerTitle"));
    }

    @Override
    public void bind() {
        super.bind();
        getDisplay().getUpdateLoginButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    SC.confirm(BLCMain.getMessageManager().getString("confirmResetPassword"), new BooleanCallback() {
                        @Override
                        public void execute(Boolean value) {
                            if (value != null && value) {
                                BLCMain.NON_MODAL_PROGRESS.startProgress();

                                PersistencePerspective tempPerspective = new PersistencePerspective();
                                OperationTypes opTypes = new OperationTypes();
                                tempPerspective.setOperationTypes(opTypes);

                                final Entity entity = new Entity();
                                Property prop = new Property();
                                prop.setName("username");
                                prop.setValue(display.getListDisplay().getGrid().getSelectedRecord().getAttribute("username"));
                                entity.setProperties(new Property[]{prop});
                                entity.setType(new String[]{"org.broadleafcommerce.profile.core.domain.Customer"});

                                AppServices.DYNAMIC_ENTITY.update(new PersistencePackage("org.broadleafcommerce.profile.core.domain.Customer", entity, tempPerspective, new String[]{"passwordUpdate"}, BLCMain.csrfToken), new AbstractCallback<Entity>() {
                                    @Override
                                    public void onSuccess(Entity arg0) {
                                        BLCMain.NON_MODAL_PROGRESS.stopProgress();
                                        SC.say(BLCMain.getMessageManager().getString("resetPasswordSuccessful"));
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void setup() {
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("customerDS", new CustomerListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource top) {
                setupDisplayItems(top);
                ((ListGridDataSource) top).setupGridFields(new String[]{"username", "firstName", "lastName", "emailAddress"}, new Boolean[]{true, true, true, true});
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("challengeQuestionDS", new ChallengeQuestionListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                ((ListGridDataSource) result).resetPermanentFieldVisibility("question");
                final EntitySearchDialog challengeQuestionSearchView = new EntitySearchDialog((ListGridDataSource) result, true);
                getPresenterSequenceSetupManager().getDataSource("customerDS").
                        getFormItemCallbackHandlerManager().addSearchFormItemCallback(
                        "challengeQuestion",
                        challengeQuestionSearchView,
                        BLCMain.getMessageManager().getString("challengeQuestionSearchPrompt"),
                        display.getDynamicFormDisplay()
                );
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("localeDS", new LocaleListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                ((ListGridDataSource) result).resetPermanentFieldVisibility("friendlyName");
                final EntitySearchDialog localeSearchView = new EntitySearchDialog((ListGridDataSource) result, true);
                getPresenterSequenceSetupManager().getDataSource("customerDS").
                        getFormItemCallbackHandlerManager().addSearchFormItemCallback(
                        "customerLocale",
                        localeSearchView,
                        BLCMain.getMessageManager().getString("localeSearchPrompt"),
                        display.getDynamicFormDisplay()
                );
            }
        }));
    }
    
    @Override
    public void postSetup(Canvas container) {
        super.postSetup(container);
    }
    @Override
    public CustomerDisplay getDisplay() {
        return (CustomerDisplay) display;
    }

}
