/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.common.util;

/**
 * This Tuple class can be used when you want to return two elements from a function in a type safe way.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class Tuple<A, B> {
    
    protected final A one;
    protected final B two;
    
    public Tuple(A one, B two) {
        this.one = one;
        this.two = two;
    }
    
    public A getFirst() {
        return one;
    }
    
    public B getSecond() {
        return two;
    }

}
