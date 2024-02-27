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

package org.broadleafcommerce.common.web.dialect;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.spring5.dialect.SpringStandardDialect;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

public class BLCAdminDialect extends AbstractProcessorDialect implements IExpressionObjectDialect {

    @Resource(name = "blVariableExpressionObjectFactory")
    protected IExpressionObjectFactory expressionObjectFactory;


    private Set<IProcessor> processors = new HashSet<IProcessor>();

    public BLCAdminDialect() {
        super("Broadleaf Admin Dialect",
                "blc_admin", SpringStandardDialect.PROCESSOR_PRECEDENCE);
    }

    public void setProcessors(Set<IProcessor> processors) {
        this.processors = processors;
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        return processors;
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return expressionObjectFactory;
    }
}
