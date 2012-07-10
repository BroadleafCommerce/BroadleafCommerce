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

package org.broadleafcommerce.cms.admin.client.presenter;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Command;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;

import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.admin.client.datasource.file.StaticAssetsTileGridDataSourceFactory;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.callback.TileGridItemSelected;
import org.broadleafcommerce.openadmin.client.callback.TileGridItemSelectedHandler;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.TileGridDataSource;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.AssetSearchDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.HTMLTextItem;

/**
 * Created by IntelliJ IDEA. User: jfischer Date: 10/1/11 Time: 12:39 PM To
 * change this template use File | Settings | File Templates.
 */
public abstract class HtmlEditingPresenter extends DynamicEntityPresenter {

	protected AssetSearchDialog assetSearchDialogView;

	public String getTemplatePath() {
		return null;
	}

	public void displayAssetSearchDialogFromCKEditor(
			final JavaScriptObject editor) {
		assetSearchDialogView.search("Asset Search",
				new TileGridItemSelectedHandler() {
					@Override
					public void onSearchItemSelected(TileGridItemSelected event) {
						String staticAssetFullUrl = BLCMain.assetServerUrlPrefix
								+ event.getRecord().getAttribute("fullUrl");
						String name = event.getRecord().getAttribute("name");
						String fileExtension = event.getRecord().getAttribute(
								"fileExtension");
						String richContent;

						if (fileExtension.equals("gif")
								|| fileExtension.equals("jpg")
								|| fileExtension.equals("png")) {
							richContent = "<img title='" + name + "' src='"
									+ staticAssetFullUrl + "' alt='" + name
									+ "'/>";
						} else {
							richContent = "<a href='" + staticAssetFullUrl
									+ "'>" + name + "</a>";
						}
						insertRichTextContentIntoCKEDitor(editor, richContent);
						getDisplay().getDynamicFormDisplay().getSaveButton()
								.enable();
						getDisplay().getDynamicFormDisplay().getRefreshButton()
								.enable();
					}
				});
	}

	public void displayAssetSearchDialog(final HTMLTextItem item) {
		assetSearchDialogView.search("Asset Search",
				new TileGridItemSelectedHandler() {
					@Override
					public void onSearchItemSelected(TileGridItemSelected event) {

						String staticAssetFullUrl = BLCMain.assetServerUrlPrefix
								+ event.getRecord().getAttribute("fullUrl");
						String name = event.getRecord().getAttribute("name");
						String fileExtension = event.getRecord().getAttribute(
								"fileExtension");
						String richContent;

						if (fileExtension.equals("gif")
								|| fileExtension.equals("jpg")
								|| fileExtension.equals("png")) {
							richContent = "<img title='" + name + "' src='"
									+ staticAssetFullUrl + "' alt='" + name
									+ "'/>";
						} else {
							richContent = "<a href='" + staticAssetFullUrl
									+ "'>" + name + "</a>";
						}
						LogFactory.getLog(this.getClass())
								.info("inserting from dialog...."
										+ fileExtension + " " + name + " "
										+ staticAssetFullUrl);
						item.insertAsset(fileExtension, name,
								staticAssetFullUrl);

					}
				});
	}

	private native void exposeNativeGetTemplatePath() /*-{
														var currentPagesPresenter = this;
														$wnd.getTemplatePath = function() {
														return currentPagesPresenter.@org.broadleafcommerce.cms.admin.client.presenter.HtmlEditingPresenter::getTemplatePath()();
														}
														}-*/;

	private native void exposeNativeDisplayAssetSearchDialog() /*-{
																var currentPagesPresenter = this;
																$wnd.displayAssetSearchDialogFromCKEditor = function(editor) {
																return currentPagesPresenter.@org.broadleafcommerce.cms.admin.client.presenter.HtmlEditingPresenter::displayAssetSearchDialogFromCKEditor(Lcom/google/gwt/core/client/JavaScriptObject;)(editor);
																}

																}-*/;

	private native void insertRichTextContentIntoCKEDitor(
			JavaScriptObject ckEditor, String content) /*-{
														//console.log('inserting text into ckEditor: '+content);
														//console.log('editor='+ckEditor);
														// window.parent.printMembers(ckEditor);
														if (ckEditor!=null && (content!=null)) {
														ckEditor.insertHtml(content);
														}
														//console.log('finished inserting text into ckEditor');
														}-*/;

	protected String getAdminContext() {
		return BLCMain.adminContext;
	}

	protected String getPreviewUrlPrefix() {
		return BLCMain.storeFrontWebAppPrefix;
	}

	public native void exposeNativeGetPreviewUrlPrefix() /*-{
															var currentPagesPresenter = this;
															$wnd.getPreviewUrlPrefix = function() {
															return currentPagesPresenter.@org.broadleafcommerce.cms.admin.client.presenter.HtmlEditingPresenter::getPreviewUrlPrefix()();
															}
															}-*/;

	public native void exposeNativeAdminContext() /*-{
													var currentPagesPresenter = this;
													$wnd.getAdminContext = function() {
													return currentPagesPresenter.@org.broadleafcommerce.cms.admin.client.presenter.HtmlEditingPresenter::getAdminContext()();
													}
													}-*/;

	@Override
	public void setup() {

		getPresenterSequenceSetupManager().addOrReplaceItem(
				new PresenterSetupItem("staticAssetTreeDS",
						new StaticAssetsTileGridDataSourceFactory(),
						new AsyncCallbackAdapter() {
							@Override
							public void onSetupSuccess(DataSource dataSource) {
								TileGridDataSource staticAssetTreeDS = (TileGridDataSource) dataSource;
								assetSearchDialogView = new AssetSearchDialog(
										staticAssetTreeDS);
							}
						}));
	}

	@Override
	public void bind() {
		super.bind();
		// exposeNativeGetTemplatePath();
		// exposeNativeDisplayAssetSearchDialog();
		// exposeNativeGetPreviewUrlPrefix();
		// exposeNativeAdminContext();
	}
/*
 * Add a handler to the HTMLTextItem's "add BLC asset" button so that if clicked, we show them the asset search dialog
 */
	public void addListenerToFormItem(DynamicForm form) {
		for (final FormItem formItem : form.getFields()) {
			if (formItem instanceof HTMLTextItem) {

				((HTMLTextItem) formItem).addAssetHandler(new Command() {
					@Override
					public void execute() {
						displayAssetSearchDialog(((HTMLTextItem) formItem));
					};
				});
			}
		}
	}
}
