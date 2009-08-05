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
package org.broadleafcommerce.util.test;

import javax.annotation.Resource;

import org.broadleafcommerce.test.integration.BaseTest;
import org.broadleafcommerce.util.domain.CodeType;
import org.broadleafcommerce.util.service.CodeTypeService;
import org.broadleafcommerce.util.test.dataprovider.CodeTypeDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class CodeTypeTest extends BaseTest {
    @Resource
    CodeTypeService codeTypeService;

    @Test(groups = {"testCodeTypeSave"}, dataProvider = "setupCodeType", dataProviderClass = CodeTypeDataProvider.class)
    @Rollback(true)
    public void testCodeTypeSave(CodeType codeType) throws Exception {
        CodeType newCodeType = codeTypeService.save(codeType);

        CodeType codeTypeFromDB = codeTypeService.lookupCodeTypeByKey(codeType.getKey());

        assert (newCodeType.getId() == codeTypeFromDB.getId());
        assert (codeType.getKey() == codeTypeFromDB.getKey());
        assert (codeType.getCodeType().equals(codeTypeFromDB.getCodeType()));
        assert (codeType.getDescription().equals(codeTypeFromDB.getDescription()));
        assert (codeType.getModifyable() == codeTypeFromDB.getModifyable());
    }



}
