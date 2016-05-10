/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
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
