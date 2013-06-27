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

package org.broadleafcommerce.profile.core.domain;

import java.io.Serializable;

public interface IdGeneration extends Serializable {

    public String getType();

    public void setType(String type);

    public Long getBegin();

    public void setBegin(Long begin);

    public Long getEnd();

    public void setEnd(Long end);

    public Long getBatchStart();

    public void setBatchStart(Long batchStart);

    public Long getBatchSize();

    public void setBatchSize(Long batchSize);

    public Integer getVersion();

}
