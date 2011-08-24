package org.broadleafcommerce.cms.admin.client.view.pages;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.events.FocusEvent;
import com.smartgwt.client.widgets.form.fields.events.FocusHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import org.broadleafcommerce.cms.admin.client.ContentManagementModule;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.Location;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityTreeView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;

import java.util.LinkedHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/22/11
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class PagesView extends HLayout implements Instantiable, PagesDisplay {

    protected DynamicEntityTreeView listDisplay;
    protected DynamicFormView dynamicFormDisplay;
    protected ComboBoxItem currentLocale = new ComboBoxItem();

    public PagesView() {
		setHeight100();
		setWidth100();
	}

    public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
		VLayout leftVerticalLayout = new VLayout();
		leftVerticalLayout.setID("pagesLeftVerticalLayout");
		leftVerticalLayout.setHeight100();
		leftVerticalLayout.setWidth("50%");
		leftVerticalLayout.setShowResizeBar(true);

		listDisplay = new DynamicEntityTreeView(BLCMain.getMessageManager().getString("pagesTitle"), entityDataSource);
		listDisplay.setShowResizeBar(true);
        Canvas[] members = listDisplay.getToolBar().getMembers();

        Location[] locales = ((ContentManagementModule) BLCMain.getModule(BLCMain.currentModuleKey)).getLocales();
        currentLocale.setShowTitle(false);
        currentLocale.setWidth(120);
        LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
        for (Location locale : locales) {
            valueMap.put(locale.getLocaleCode(), locale.getLocaleName());
        }
        currentLocale.setValueMap(valueMap);
        currentLocale.setDefaultValue(locales[0].getLocaleCode());
        currentLocale.addFocusHandler(new FocusHandler() {
            public void onFocus(FocusEvent event) {
                //do something when they change the locale
            }
        });
        listDisplay.getToolBar().addFormItem(currentLocale, 6);
        listDisplay.getToolBar().addSpacer(6);

        leftVerticalLayout.addMember(listDisplay);

        dynamicFormDisplay = new DynamicFormView(BLCMain.getMessageManager().getString("detailsTitle"), entityDataSource);

        addMember(leftVerticalLayout);
        addMember(dynamicFormDisplay);
	}

    public Canvas asCanvas() {
		return this;
	}

	public DynamicEntityListDisplay getListDisplay() {
		return listDisplay;
	}

    public DynamicFormDisplay getDynamicFormDisplay() {
		return dynamicFormDisplay;
	}
}
