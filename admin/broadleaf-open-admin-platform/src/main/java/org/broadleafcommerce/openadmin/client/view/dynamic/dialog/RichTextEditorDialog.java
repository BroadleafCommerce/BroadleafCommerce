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

package org.broadleafcommerce.openadmin.client.view.dynamic.dialog;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RichTextArea;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.presenter.entity.HtmlEditingPresenter;
import org.broadleafcommerce.openadmin.client.view.dynamic.RichTextToolbar;
import org.broadleafcommerce.openadmin.client.view.dynamic.RichTextToolbar.DisplayType;

/**
 * 
 * @author krosenberg
 * 
 */
public class RichTextEditorDialog extends Window {

    protected IButton saveButton;
    final HtmlEditingPresenter p = HtmlEditingPresenter.getInstance();

    private HandlerRegistration handlerRegistration;
    private VLayout mainLayout = null;
    private RichTextToolbar toolBar = null;
    private RichTextArea textArea = null;

    protected void initItem(int width, int height) {
        String legacyHtml = "";
        if (toolBar != null) {
            toolBar.removeFromParent();
        }
        if (textArea != null) {
            legacyHtml = textArea.getHTML();
            textArea.removeFromParent();
        }
        if (mainLayout != null) {
            removeItem(mainLayout);
            mainLayout.destroy();
        }
        textArea = new RichTextArea();
        toolBar = new RichTextToolbar(textArea, DisplayType.DETAILED);

        saveButton = new IButton(BLCMain.getMessageManager().getString("ok"));
        saveButton.setIcon("[SKIN]/actions/ok.png");
        IButton cancelButton = new IButton(BLCMain.getMessageManager()
                .getString("cancel"));
        cancelButton.setIcon("[SKIN]/actions/undo.png");
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                handlerRegistration.removeHandler();
                hide();
            }
        });

        final HtmlEditingPresenter pp = HtmlEditingPresenter.getInstance();

        final HLayout buttonsLayout = new HLayout(10);
        buttonsLayout.setAlign(Alignment.CENTER);
        buttonsLayout.addMember(saveButton);
        buttonsLayout.addMember(cancelButton);
        buttonsLayout.setLayoutTopMargin(2);
        buttonsLayout.setLayoutBottomMargin(2);
        buttonsLayout.setWidth100();
        buttonsLayout.setHeight(25);

        mainLayout = new VLayout();
        mainLayout.setWidth100();
        mainLayout.setHeight100();

        toolBar.setHeight("60px");
        final VLayout toolbarLayout = new VLayout();
        toolbarLayout.setWidth100();
        toolbarLayout.setHeight(60);
        toolbarLayout.addMember(toolBar);

        int textHeight = height - 130;
        textArea.setHeight(textHeight + "px");
        textArea.setWidth(width + "px");
        final VLayout textAreaLayout = new VLayout();
        textAreaLayout.setWidth100();
        textAreaLayout.setHeight(textHeight);
        textAreaLayout.addMember(textArea);

        mainLayout.addMember(toolbarLayout);
        mainLayout.addMember(textAreaLayout);
        mainLayout.addMember(buttonsLayout);
        addItem(mainLayout);

        toolBar.addAssetHandler(new Command() {
            @Override
            public void execute() {
                pp.displayAssetSearchDialog(toolBar);
            }
        });

        textArea.setHTML(legacyHtml);
    }

    public RichTextEditorDialog() {
        setWidth(800);
        setHeight(600);
        setIsModal(true);
        setShowModalMask(true);
        setShowMinimizeButton(false);
        setShowMaximizeButton(true);
        setCanDragResize(true);
        setOverflow(Overflow.AUTO);

        addResizedHandler(new ResizedHandler() {

            @Override
            public void onResized(ResizedEvent event) {
                initItem(RichTextEditorDialog.this.getInnerWidth(), RichTextEditorDialog.this.getInnerHeight());
                mainLayout.redraw();
            }
        });
        
    }

    public IButton getSaveButton() {
        return saveButton;
    }

    public void setSaveButton(IButton saveButton) {
        this.saveButton = saveButton;
    }

    public void show(final CanvasItem richTextItem) {
        initItem(788, 580);
        setTitle("Edit " + richTextItem.getFieldName());
        String htmlValueToEdit = (String) richTextItem.getValue();
        toolBar.setHTML(htmlValueToEdit);
        handlerRegistration = saveButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (richTextItem != null) {
                    richTextItem.storeValue(toolBar.getHTML());
                    richTextItem.setValue(toolBar.getHTML());
                    
                }
                hide();
                handlerRegistration.removeHandler();
            }
        });
        show();
    }

}
