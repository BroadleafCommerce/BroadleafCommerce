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
package org.broadleafcommerce.util.test.dataprovider;

import org.broadleafcommerce.util.domain.CodeType;
import org.broadleafcommerce.util.domain.CodeTypeImpl;
import org.testng.annotations.DataProvider;

public class CodeTypeDataProvider {
    @DataProvider(name = "setupCodeType")
    public static Object[][] createCodeType() {
        CodeType codeType = new CodeTypeImpl();
        codeType.setCodeType("Test Type 1");
        codeType.setDescription("Test Description");
        codeType.setKey("codeTypeKey");
        codeType.setModifyable(true);
        return new Object[][] { new Object[] {codeType }} ;
    }
}
