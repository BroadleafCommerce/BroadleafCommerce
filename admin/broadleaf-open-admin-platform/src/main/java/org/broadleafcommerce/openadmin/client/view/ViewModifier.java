
package org.broadleafcommerce.openadmin.client.view;


import com.smartgwt.client.data.DataSource;

import java.util.List;

public interface ViewModifier {

    Display getParentView();

    void setParentView(Display display);

    void build(List<DataSource> dataSourcesList);
}
