/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.common.extensibility.jpa;

/**
 * MBean registered in JMX to keep track of which persistence units are marked with auto.ddl 'create'. The scope of this MBean
 * covers the current JVM, which may span more than a single application in the same container.
 *
 * @author Jeff Fischer
 */
public interface AutoDDLCreateStatusTestBean {

    Boolean getStartedWithCreate(String pu);

    void setStartedWithCreate(String pu, Boolean val);

}
