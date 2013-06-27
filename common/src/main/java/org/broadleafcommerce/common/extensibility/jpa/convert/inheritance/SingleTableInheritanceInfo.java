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

package org.broadleafcommerce.common.extensibility.jpa.convert.inheritance;

import javax.persistence.DiscriminatorType;

/**
 * 
 * @author jfischer
 *
 */
public class SingleTableInheritanceInfo {

    protected String className;
    protected String discriminatorName;
    protected DiscriminatorType discriminatorType;
    protected int discriminatorLength;
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getDiscriminatorName() {
        return discriminatorName;
    }
    
    public void setDiscriminatorName(String discriminatorName) {
        this.discriminatorName = discriminatorName;
    }
    
    public DiscriminatorType getDiscriminatorType() {
        return discriminatorType;
    }
    
    public void setDiscriminatorType(DiscriminatorType discriminatorType) {
        this.discriminatorType = discriminatorType;
    }
    
    public int getDiscriminatorLength() {
        return discriminatorLength;
    }
    
    public void setDiscriminatorLength(int discriminatorLength) {
        this.discriminatorLength = discriminatorLength;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SingleTableInheritanceInfo other = (SingleTableInheritanceInfo) obj;
        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;
        return true;
    }
    
}
