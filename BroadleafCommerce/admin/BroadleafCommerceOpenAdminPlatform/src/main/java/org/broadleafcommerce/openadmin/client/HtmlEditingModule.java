package org.broadleafcommerce.openadmin.client;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/30/11
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HtmlEditingModule extends Module {

    public String getHtmlEditorIFramePath();

	public void setHtmlEditorIFramePath(String htmlEditorIFramePath);

	public String getBasicHtmlEditorIFramePath();

	public void setBasicHtmlEditorIFramePath(String basicHtmlEditorIFramePath);

    public String getPreviewUrlPrefix();

    public void setPreviewUrlPrefix(String previewUrlPrefix);

}
