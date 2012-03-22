package org.broadleafcommerce.openadmin.client.view.dynamic.grid;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Jeff Fischer
 */
public class TileGrid extends com.smartgwt.client.widgets.tile.TileGrid {

    public TileGrid(JavaScriptObject jsObj) {
        super(jsObj);
        //cause the text content associated with the tile to wrap rather than truncate
        setAttribute("wrapValues", true, true);
    }

    public TileGrid() {
        super();
        //cause the text content associated with the tile to wrap rather than truncate
        setAttribute("wrapValues", true, true);
    }


}
