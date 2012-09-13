/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.admin.client.view.sandbox;

import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/22/11
 * Time: 11:46 AM
 * To change this template use File | Settings | File Templates.
 */
public interface MySandBoxDisplay extends SandBoxDisplay {

    public ListGrid getPendingGrid();

    public ToolStripButton getRevertSelectionButton();

    public void setRevertSelectionButton(ToolStripButton revertSelectionButton);

    public ToolStrip getPendingToolBar();

    public void setPendingToolBar(ToolStrip pendingToolBar);

    public ToolStripButton getReclaimSelectionButton();

    public void setReclaimSelectionButton(ToolStripButton reclaimSelectionButton);

    public ToolStripButton getReclaimAllButton();

    public void setReclaimAllButton(ToolStripButton reclaimAllButton);

    public ToolStripButton getReleaseSelectionButton();

    public void setReleaseSelectionButton(ToolStripButton releaseSelectionButton);

    public ToolStripButton getReleaseAllButton();

    public void setReleaseAllButton(ToolStripButton releaseAllButton);

    public ToolStripButton getPendingRefreshButton();

    public void setPendingRefreshButton(ToolStripButton pendingRefreshButton);

    public ToolStripButton getPendingPreviewButton();

    public void setPendingPreviewButton(ToolStripButton pendingPreviewButton);
    
}
