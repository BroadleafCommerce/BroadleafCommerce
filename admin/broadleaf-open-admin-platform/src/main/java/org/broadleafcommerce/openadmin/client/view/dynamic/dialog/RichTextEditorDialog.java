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

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.presenter.entity.HtmlEditingPresenter;
import org.broadleafcommerce.openadmin.client.view.dynamic.RichTextToolbar;
import org.broadleafcommerce.openadmin.client.view.dynamic.RichTextToolbar.DisplayType;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
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

/**
 * 
 * @author krosenberg
 * 
 */
public class RichTextEditorDialog extends Window {

    protected IButton saveButton;
    final HtmlEditingPresenter p = HtmlEditingPresenter.getInstance();

    private final RichTextArea textArea;
    private final RichTextToolbar toolBar;
    private HandlerRegistration handlerRegistration;

    public RichTextEditorDialog() {
        setIsModal(true);

        setShowModalMask(true);
        setShowMinimizeButton(false);
        setWidth(600);
        setHeight(550);
        setCanDragResize(true);
        setOverflow(Overflow.AUTO);
        setVisible(true);
        textArea = new RichTextArea();
        // textArea.addInitializeHandler(new InitializeHandler() {
        // @Override
        // public void onInitialize(InitializeEvent ie) {
        // IFrameElement fe = (IFrameElement) textArea.getElement().cast();
        // if (fe == null) {
        // return;
        // }
        // Style s = fe.getContentDocument().getBody().getStyle();
        // s.setProperty("fontFamily", "helvetica, sans-serif");
        // s.setProperty("fontSize", "12");
        //
        // }
        // });
        toolBar = new RichTextToolbar(textArea, DisplayType.DETAILED);
        VerticalPanel vp = new VerticalPanel();
        // if(displayType == displayType.DETAILED) {
        // getTextArea().setWidth("600px");
        // getTextArea().setHeight("540px");
        // toolBar.setWidth("100%");
        // toolBar.setHeight("100%");
        // } else {
        // getTextArea().setWidth("500px");
        // getTextArea().setHeight("160px");
        // toolBar.setWidth("100%");
        // toolBar.setHeight("40px");
        // }
        // htmlItem = new HTMLTextItem();
        //
        // htmlItem.setHeight(540);
        // htmlItem.setWidth(600);
        //
        // htmlItem.addAssetHandler(new Command() {
        // @Override
        // public void execute() {
        // p.displayAssetSearchDialog(htmlItem);
        // };
        // });

        // htmlItem.setHeight100();
        // htmlItem.setWidth100();
        // staticAssetDataSource.setAssociatedGrid(tileGrid);
        // staticAssetDataSource.setupGridFields(new String[]{"pictureLarge",
        // "name"});
        // tileGrid.setDataSource(staticAssetDataSource);
        // tileGrid.addSelectionChangedHandler(new SelectionChangedHandler() {
        // @Override
        // public void onSelectionChanged(SelectionChangedEvent event) {
        // saveButton.enable();
        // }
        // });
        // tileGrid.addClickHandler(new ClickHandler() {
        // @Override
        // public void onClick(ClickEvent event) {
        // if (event.isLeftButtonDown()) {
        // if (tileGrid.anySelected()) {
        // saveButton.enable();
        // }
        // }
        // }
        // });

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
        ;

        final HLayout buttonsLayout = new HLayout(10);
        buttonsLayout.setAlign(Alignment.CENTER);
        buttonsLayout.addMember(saveButton);
        buttonsLayout.addMember(cancelButton);
        buttonsLayout.setLayoutTopMargin(2);
        buttonsLayout.setLayoutBottomMargin(2);
        buttonsLayout.setWidth100();
        buttonsLayout.setHeight(25);

        VLayout mainLayout = new VLayout();
        mainLayout.setWidth100();
        mainLayout.setHeight100();
        // textArea.setWidth("550px");
        // textArea.setHeight("355px");
        mainLayout.addMember(buttonsLayout);
        mainLayout.addMember(toolBar);
        mainLayout.addMember(textArea);
        addItem(mainLayout);

        toolBar.addAssetHandler(new Command() {
            @Override
            public void execute() {
                pp.displayAssetSearchDialog(toolBar);
            }
        });

        addResizedHandler(new ResizedHandler() {

            @Override
            public void onResized(ResizedEvent event) {
                resizeGwtWidgets(event.getX(), event.getY());
            }
        });
        
    }

    private void resizeGwtWidgets(int x, int y) {
        textArea.setWidth(Math.abs(x - 14) + "px");
        textArea.setHeight(Math.abs(y - 170) + "px");
        toolBar.setWidth(Math.abs(x - 14) + "px");
    }

    public IButton getSaveButton() {
        return saveButton;
    }

    public void setSaveButton(IButton saveButton) {
        this.saveButton = saveButton;
    }

    public void show(final CanvasItem richTextItem) {
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
        resizeTo(800, 600);
        resizeGwtWidgets(800,600);
    }

}
