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

package org.broadleafcommerce.common.sandbox.dao;

import java.util.List;

import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;

public interface SandBoxDao {

    public SandBox retrieve(Long id);
    
    public List<SandBox> retrieveAllSandBoxes();

    public SandBox retrieveSandBoxByType(SandBoxType sandboxType);

    public SandBox retrieveNamedSandBox(SandBoxType sandboxType, String sandboxName);

    public SandBox persist(SandBox entity);

    public SandBox createSandBox(String sandBoxName, SandBoxType sandBoxType);


}