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

package org.broadleafcommerce.core.util.service;

import org.broadleafcommerce.core.util.dao.CodeTypeDao;
import org.broadleafcommerce.core.util.domain.CodeType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("blCodeTypeService")
@Deprecated
public class CodeTypeServiceImpl implements CodeTypeService {

    @Resource(name="blCodeTypeDao")
    protected CodeTypeDao codeTypeDao;

    public void deleteCodeType(CodeType codeTypeId) {
        codeTypeDao.delete(codeTypeId);
    }

    public List<CodeType> findAllCodeTypes() {
        return codeTypeDao.readAllCodeTypes();
    }

    public CodeType lookupCodeTypeById(Long codeTypeId) {
        return codeTypeDao.readCodeTypeById(codeTypeId);
    }

    public List<CodeType> lookupCodeTypeByKey(String key) {
        return codeTypeDao.readCodeTypeByKey(key);
    }

    public CodeType save(CodeType codeType) {
        return codeTypeDao.save(codeType);
    }

}
