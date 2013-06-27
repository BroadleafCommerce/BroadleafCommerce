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

package org.broadleafcommerce.common.web.expression;

/**
 * Classes that implement this interface will be exposed to the Thymeleaf expression evaluation context.
 * If an implementing class defines its name as "theme" and has a method called attr(String name), that method
 * could then be invoked by ${#theme.attr('someName')}.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface BroadleafVariableExpression {
    
    public String getName();
    
}
