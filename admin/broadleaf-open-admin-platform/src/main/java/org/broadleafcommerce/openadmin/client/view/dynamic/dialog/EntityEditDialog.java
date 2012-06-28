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

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.callback.ItemEdited;
import org.broadleafcommerce.openadmin.client.callback.ItemEditedHandler;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormBuilder;

import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class EntityEditDialog extends Window {
	
	private DynamicForm dynamicForm;
	private ItemEditedHandler handler;
    private VStack pictureStack;
    protected boolean showMedia = false;
    protected String mediaField;
    protected HStack hStack;
    protected VLayout vLayout;
    protected Boolean isHidden = true;
    protected VStack previewContainer;
    protected IButton saveButton;
    protected IButton cancelButton;

	public EntityEditDialog() {
		this.setIsModal(true);
		this.setShowModalMask(true);
		this.setShowMinimizeButton(false);
		//this.setWidth(600);
		this.setCanDragResize(true);
		this.setOverflow(Overflow.VISIBLE);

        hStack = new HStack();
        
		VStack stack = new VStack();
        //stack.setWidth("80%");
		stack.setLayoutRightMargin(20);
		dynamicForm = new DynamicForm();
        dynamicForm.setPadding(10);
        stack.addMember(dynamicForm);

        hStack.addMember(stack);

        pictureStack = new VStack();
        pictureStack.setLayoutTopMargin(20);
        previewContainer = new VStack();
        previewContainer.setWidth(60);
        previewContainer.setHeight(60);
        previewContainer.setBorder("1px solid #a6abb4");
        pictureStack.addMember(previewContainer);

        hStack.addMember(pictureStack);

        addItem(hStack);

        saveButton = new IButton("Save");
        saveButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	if (dynamicForm.validate()) {
                    saveButton.disable();
                    cancelButton.disable();
            		dynamicForm.saveData(new DSCallback() {
						public void execute(DSResponse response, Object rawData, DSRequest request) {
                            if (response.getStatus()== RPCResponse.STATUS_SUCCESS) {
                                TreeNode record = (TreeNode) response.getData()[0];
                                if (handler != null) {
                                    handler.onItemEdited(new ItemEdited(record, dynamicForm.getDataSource()));
                                }
                            }
                            if (response.getStatus() != RPCResponse.STATUS_VALIDATION_ERROR) {
                                hide();
                                isHidden = true;
                            }
                            saveButton.enable();
                            cancelButton.enable();
						}
            		});
            	}
            }
        });

        cancelButton = new IButton("Cancel");
        cancelButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	hide();
                isHidden = true;
            }
        });

        vLayout = new VLayout();
        vLayout.setHeight100();
        vLayout.setAlign(VerticalAlignment.BOTTOM);
        HLayout hLayout = new HLayout(10);
        hLayout.setAlign(Alignment.CENTER);
        hLayout.addMember(saveButton);
        hLayout.addMember(cancelButton);
        hLayout.setLayoutTopMargin(40);
        hLayout.setLayoutBottomMargin(40);
        vLayout.addMember(hLayout);
        addItem(vLayout);
	}

	@SuppressWarnings("rawtypes")
	public void editNewRecord(DynamicEntityDataSource dataSource, Map initialValues, ItemEditedHandler handler, String[] fieldNames) {
		editNewRecord(null, dataSource, initialValues, handler, fieldNames, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void editNewRecord(String title, DynamicEntityDataSource dataSource, Map initialValues, ItemEditedHandler handler, String[] fieldNames, String[] ignoreFields) {
        pictureStack.setVisible(false);
        saveButton.enable();
        cancelButton.enable();
		initialValues.put(dataSource.getPrimaryKeyFieldName(), "");
		this.handler = handler;
		if (fieldNames != null && fieldNames.length > 0) {
			dataSource.resetVisibilityOnly(fieldNames);
		} else {
			dataSource.resetPermanentFieldVisibility();
		}
		if (ignoreFields != null) {
			for (String fieldName : ignoreFields) {
				dataSource.getField(fieldName).setHidden(true);
			}
		}
		if (title != null) {
			this.setTitle(title);
		} else {
			this.setTitle("Add new entity: " + dataSource.getPolymorphicEntities().get(dataSource.getDefaultNewEntityFullyQualifiedClassname()));
		}
		buildFields(dataSource, dynamicForm, null);
        dynamicForm.editNewRecord(initialValues);
		show();
        redraw();
        saveButton.setVisible(true);
        setHeight(20);
        int formHeight = hStack.getScrollHeight() + vLayout.getScrollHeight() + 30;
        if (formHeight > 600) {
            setHeight(600);
        } else {
            setHeight(formHeight);
        }
        setWidth(20);
        int formWidth = hStack.getScrollWidth() + 30;
        if (formWidth > 800) {
            setWidth(800);
        } else if (formWidth < 400) {
            setWidth(400);
        } else {
            setWidth(formWidth);
        }
        centerInPage();
        isHidden = false;
    }

    public void updateMedia(String url) {
        pictureStack.setVisible(true);

        Canvas[] children = previewContainer.getChildren();
        if (children != null && children.length > 0) {
            children[0].destroy();
        }

        Img previewImg = new Img();
        previewImg.setImageType(ImageStyle.CENTER);
        previewImg.setVisible(true);
        previewImg.setShowDisabled(false);
        previewImg.setShowDown(false);

        url = url.toLowerCase();
        if (url.startsWith("http")) {
            previewImg.setSrc(url);
            previewImg.setImageHeight(60);
            previewImg.setImageWidth(60);
        } else if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".gif") || url.endsWith(".png")) {
            String srcPath;
            if (BLCMain.assetServerUrlPrefix != null && !url.contains(BLCMain.assetServerUrlPrefix)) {
                srcPath = BLCMain.buildStoreFrontBaseUrl(url);
                previewImg.setImageHeight(60);
                previewImg.setImageWidth(60);
            } else {
                srcPath = url + "?largeAdminThumbnail";
            }
            previewImg.setSrc(srcPath);
        } else {
            previewImg.setSrc("[ISOMORPHIC]/../admin/images/Mimetype-binary-icon-64.png");
        }

        previewContainer.addChild(previewImg);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void editRecord(String title, DynamicEntityDataSource dataSource, Record record, ItemEditedHandler handler, String[] fieldNames, String[] ignoreFields, boolean readOnly) {
        saveButton.enable();
        cancelButton.enable();
        pictureStack.setVisible(false);
        if (showMedia && mediaField != null) {
            updateMedia(record.getAttribute(mediaField));
        }
		this.handler = handler;
		if (fieldNames != null && fieldNames.length > 0) {
			dataSource.resetVisibilityOnly(fieldNames);
		} else {
			dataSource.resetPermanentFieldVisibility();
		}
		if (ignoreFields != null) {
			for (String fieldName : ignoreFields) {
				dataSource.getField(fieldName).setHidden(true);
			}
		}
		if (title != null) {
			this.setTitle(title);
		} else {
			this.setTitle("Edit entity: " + dataSource.getPolymorphicEntities().get(dataSource.getDefaultNewEntityFullyQualifiedClassname()));
		}
		buildFields(dataSource, dynamicForm, record);
        dynamicForm.editRecord(record);
        centerInPage();
		setTop(70);
		show();
        redraw();
        saveButton.setVisible(!readOnly);
        setHeight(20);
        int formHeight = hStack.getScrollHeight() + vLayout.getScrollHeight() + 30;
        if (formHeight > 600) {
            setHeight(600);
        } else {
            setHeight(formHeight);
        }
        setWidth(20);
        int formWidth = hStack.getScrollWidth() + 30;
        if (formWidth > 800) {
            setWidth(800);
        } else if (formWidth < 400) {
            setWidth(400);
        } else {
            setWidth(formWidth);
        }
        isHidden = false;
	}
	
	protected void buildFields(DataSource dataSource, DynamicForm dynamicForm, Record record) {
		FormBuilder.buildForm(dataSource, dynamicForm, false, record);
	}

    public boolean isShowMedia() {
        return showMedia;
    }

    public void setShowMedia(boolean showMedia) {
        this.showMedia = showMedia;
    }

    public String getMediaField() {
        return mediaField;
    }

    public void setMediaField(String mediaField) {
        this.mediaField = mediaField;
    }

    public Boolean getHidden() {
        return isHidden;
    }

    public void setHidden(Boolean hidden) {
        isHidden = hidden;
    }
}
