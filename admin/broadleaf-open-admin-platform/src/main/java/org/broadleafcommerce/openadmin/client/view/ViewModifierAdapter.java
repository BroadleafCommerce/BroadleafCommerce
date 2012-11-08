
package org.broadleafcommerce.openadmin.client.view;


import com.smartgwt.client.data.DataSource;

import java.util.List;

public abstract class ViewModifierAdapter implements ViewModifier{

    private Display display;

    public ViewModifierAdapter() {
        super();
    }

    @Override
    public Display getParentView() {
        return display;
    }

    @Override
    public void setParentView(Display display) {
        this.display = display;
    }
    
    @Override
    public abstract void build(List<DataSource> dataSourcesList);
}
