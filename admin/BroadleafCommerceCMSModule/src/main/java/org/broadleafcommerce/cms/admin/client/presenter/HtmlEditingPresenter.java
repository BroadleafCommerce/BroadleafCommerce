package org.broadleafcommerce.cms.admin.client.presenter;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.DataSource;
import org.broadleafcommerce.cms.admin.client.datasource.file.StaticAssetsFolderTreeDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.datasource.file.StaticAssetsTileGridDataSourceFactory;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.HtmlEditingModule;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.TileGridDataSource;
import org.broadleafcommerce.openadmin.client.event.TileGridItemSelectedEvent;
import org.broadleafcommerce.openadmin.client.event.TileGridItemSelectedEventHandler;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.NullAsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.AssetSearchDialog;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 10/1/11
 * Time: 12:39 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class HtmlEditingPresenter extends DynamicEntityPresenter {

    protected AssetSearchDialog assetSearchDialogView;

    public String getTemplatePath() {
        return null;
    }

	public void displayAssetSearchDialog(final JavaScriptObject editor) {
		assetSearchDialogView.search("Asset Search", new TileGridItemSelectedEventHandler() {
			@Override
			public void onSearchItemSelected(TileGridItemSelectedEvent event) {
				String staticAssetFullUrl = BLCMain.webAppContext + "/cms/staticasset" + event.getRecord().getAttribute("fullUrl");
				String name = event.getRecord().getAttribute("name");
				String fileExtension = event.getRecord().getAttribute("fileExtension");
				String richContent;

				if (fileExtension.equals("gif") || fileExtension.equals("jpg") || fileExtension.equals("png")) {
					richContent =  "<img title='" + name + "' src='" + staticAssetFullUrl + "' alt='" + name + "'/>";
				} else {
					richContent = "<a href='" + staticAssetFullUrl + "'>" + name + "</a>";
				}
				insertRichTextContent(editor, richContent);
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
		$wnd.displayAssetSearchDialog = function(editor) {
			return currentPagesPresenter.@org.broadleafcommerce.cms.admin.client.presenter.HtmlEditingPresenter::displayAssetSearchDialog(Lcom/google/gwt/core/client/JavaScriptObject;)(editor);
		}
	}-*/;

	private native void insertRichTextContent(JavaScriptObject tinyMCEEditor, String content) /*-{
		tinyMCEEditor.selection.setContent(content);
	}-*/;

    protected String getAdminContext() {
        return BLCMain.adminContext;
    }

    protected String getPreviewUrlPrefix() {
        return ((HtmlEditingModule) BLCMain.getModule(BLCMain.currentModuleKey)).getPreviewUrlPrefix();
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
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("staticAssetFolderTreeDS", new StaticAssetsFolderTreeDataSourceFactory(), null, new Object[]{}, new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("staticAssetTreeDS", new StaticAssetsTileGridDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource dataSource) {
            	TileGridDataSource staticAssetTreeDS = (TileGridDataSource) dataSource;
            	PresentationLayerAssociatedDataSource staticAssetFolderTreeDS = (PresentationLayerAssociatedDataSource) getPresenterSequenceSetupManager().getDataSource("staticAssetFolderTreeDS");
             	assetSearchDialogView = new AssetSearchDialog(staticAssetTreeDS, staticAssetFolderTreeDS);
            }
        }));
    }

    @Override
    public void bind() {
        super.bind();
        exposeNativeGetTemplatePath();
        exposeNativeDisplayAssetSearchDialog();
        exposeNativeGetPreviewUrlPrefix();
        exposeNativeAdminContext();
    }
}
