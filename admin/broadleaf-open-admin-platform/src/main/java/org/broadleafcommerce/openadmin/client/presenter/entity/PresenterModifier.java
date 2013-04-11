
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

package org.broadleafcommerce.openadmin.client.presenter.entity;

import org.broadleafcommerce.openadmin.client.view.ViewModifier;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;

public interface PresenterModifier {

    public void bind();

    public void saveClicked();

    public void itemSaved();

    public void postSetup(Canvas container);

    public void setup();

    public void addClicked();

    public void addNewItem();

    public DynamicEntityPresenter getParentPresenter();

    public void setParentPresenter(DynamicEntityPresenter presenter);

    public void setDisplay(ViewModifier display);

    public ViewModifier getDisplay();

    public void changeSelection(Record selectedRecord);


}
