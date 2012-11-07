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

package org.broadleafcommerce.openadmin.client.presenter.entity;

import org.broadleafcommerce.openadmin.client.view.ViewModifier;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;

/**
 * 
 */
public abstract class ModifiedPresenterAdapter implements PresenterModifier {

    protected ViewModifier display;

    private DynamicEntityPresenter parentPresenter;

    public ModifiedPresenterAdapter() {
        super();
    }

    @Override
    public abstract void bind();

    @Override
    public abstract void changeSelection(final Record selectedRecord);

    @Override
    public abstract void setup();

    @Override
    public void postSetup(Canvas container) {

    }

    @Override
    public void saveClicked() {

    }

    @Override
    public void itemSaved() {

    }

    public void postSetup() {

    }

    @Override
    public void addClicked() {

    }

    @Override
    public void addNewItem() {

    }

    @Override
    public DynamicEntityPresenter getParentPresenter() {
        return parentPresenter;
    }

    @Override
    public void setParentPresenter(DynamicEntityPresenter presenter) {
        this.parentPresenter = presenter;

    }

    @Override
    public void setDisplay(ViewModifier display) {
        this.display = display;

    }

    @Override
    public ViewModifier getDisplay() {
        return display;
    }

}