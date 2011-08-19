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
package org.broadleafcommerce.cms.admin.client.asset.view;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;

/**
 * 
 * @author bpolster
 *
 */
public class FileBasedAssetView extends HLayout implements FileBasedAssetDisplay, Instantiable {
	
	protected DynamicFormView dynamicFormDisplay;
	protected DynamicEntityListView listDisplay;


	public FileBasedAssetView() {
		setHeight100();
		setWidth100();
	}
	
	public void build(DataSource dataSource, DataSource... additionalDataSources) {
		VLayout leftVerticalLayout = new VLayout();
		leftVerticalLayout.setID("orderLeftVerticalLayout");
		leftVerticalLayout.setHeight100();
		leftVerticalLayout.setWidth("50%");
		leftVerticalLayout.setShowResizeBar(true);

        final TreeGrid fileTree = new TreeGrid();
        fileTree.setLoadDataOnDemand(false);
        fileTree.setWidth(500);
        fileTree.setHeight(400);
        //fileTree.setDataSource(dataSource);
        fileTree.setData(buildTree());
        fileTree.setCanEdit(false);
        fileTree.setAutoFetchData(true);
        fileTree.draw();
        leftVerticalLayout.addMember(fileTree);

        addMember(leftVerticalLayout);

        //TODO: add a content preview panel

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


    public static final TreeNode files = new FileTreeNode("\\",
        new FileTreeNode("images",
            new FileTreeNode("dog.jpg"),
            new FileTreeNode("cat.jpg")
        ),
        new FileTreeNode("css",
            new FileTreeNode("main.css"),
            new FileTreeNode("animals.css")
        )
    );

    public static class FileTreeNode extends TreeNode {
        public FileTreeNode(String fileName, String name) {
            this(fileName, new FileTreeNode[] {});
        }

        public FileTreeNode(String name, FileTreeNode... children) {
            setAttribute("name", name);
            setAttribute("subItems", children);
            if (children != null && children.length > 0) {
                this.setIsFolder(true);
            }  else {
                this.setIsFolder(false);
            }
        }
    }

    private Tree buildTree() {
        Tree tree = new Tree();
        tree.setModelType(TreeModelType.CHILDREN);
        tree.setNameProperty("name");
        tree.setChildrenProperty("subItems");
        tree.setRoot(files);
        return tree;
    }
}
