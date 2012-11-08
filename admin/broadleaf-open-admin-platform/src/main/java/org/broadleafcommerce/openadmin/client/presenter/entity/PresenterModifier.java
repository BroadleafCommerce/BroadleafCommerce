
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
