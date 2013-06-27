/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jfischer
 */
public class ClassTree implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String fullyQualifiedClassname;
    protected String name;
    protected String friendlyName;
    protected ClassTree[] children = new ClassTree[0];
    protected int left;
    protected int right;
    protected boolean excludeFromPolymorphism;

    public ClassTree(String fullyQualifiedClassname, String friendlyName, boolean excludeFromPolymorphism) {
        setFullyQualifiedClassname(fullyQualifiedClassname);
        this.friendlyName = friendlyName;
        this.excludeFromPolymorphism = excludeFromPolymorphism;
    }

    public ClassTree(String fullyQualifiedClassname, boolean excludeFromPolymorphism) {
        this(fullyQualifiedClassname, null, excludeFromPolymorphism);
    }

    public ClassTree(String fullyQualifiedClassname) {
        this(fullyQualifiedClassname, null, false);
    }

    public ClassTree() {
        //do nothing
    }

    public boolean hasChildren() {
        return children.length > 0;
    }

    public int finalizeStructure(int start) {
        left = start;
        start++;
        for (int i = children.length-1;i >= 0; i--) {
            start = children[i].finalizeStructure(start);
            start++;
        }
        right = start;

        return start;
    }

    public List<ClassTree> getCollapsedClassTrees() {
        List<ClassTree> list = new ArrayList<ClassTree>();
        addChildren(this, list);
        return list;
    }

    protected void addChildren(ClassTree tree, List<ClassTree> list) {
        if (!tree.isExcludeFromPolymorphism()) {
            list.add(tree);
        }

        for (ClassTree child : tree.getChildren()) {
            addChildren(child, list);
        }
    }

    public ClassTree find(String fullyQualifiedClassname) {
        if (this.fullyQualifiedClassname.equals(fullyQualifiedClassname)) {
            return this;
        }
        ClassTree result = null;
        for (ClassTree child : children) {
            result = child.find(fullyQualifiedClassname);
            if (result != null) {
                break;
            }
        }

        return result;
    }

    public String getFullyQualifiedClassname() {
        return fullyQualifiedClassname;
    }

    public void setFullyQualifiedClassname(String fullyQualifiedClassname) {
        this.fullyQualifiedClassname = fullyQualifiedClassname;
        int pos = fullyQualifiedClassname.lastIndexOf('.');
        if (pos >= 0) {
            name = fullyQualifiedClassname.substring(pos + 1, fullyQualifiedClassname.length());
        } else {
            name = fullyQualifiedClassname;
        }
    }

    public String getFriendlyName() {
        return friendlyName == null ? name : friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public ClassTree[] getChildren() {
        return children;
    }

    public void setChildren(ClassTree[] children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public boolean isExcludeFromPolymorphism() {
        return this.excludeFromPolymorphism;
    }
}
