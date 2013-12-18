/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.common.extensibility.context.merge.handlers;

/**
 * Convenience base class which all handler implementations extend. This class
 * provides the common properties required by all MergeHandler implemenations.
 * @author jfischer
 */
public abstract class BaseHandler implements MergeHandler, Comparable<Object> {

    protected int priority;
    protected String xpath;
    protected MergeHandler[] children = {};
    protected String name;

    public int getPriority() {
        return priority;
    }

    public String getXPath() {
        return xpath;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setXPath(String xpath) {
        this.xpath = xpath;
    }

    public int compareTo(Object arg0) {
        return new Integer(getPriority()).compareTo(new Integer(((MergeHandler) arg0).getPriority()));
    }

    public MergeHandler[] getChildren() {
        return children;
    }

    public void setChildren(MergeHandler[] children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
