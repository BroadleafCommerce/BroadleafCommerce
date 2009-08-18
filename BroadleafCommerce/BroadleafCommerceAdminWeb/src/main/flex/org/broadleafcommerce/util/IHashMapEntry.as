/*
 Copyright (c) 2006 - 2008  Eric J. Feminella  <eric@ericfeminella.com>
 All rights reserved.

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is 
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in 
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 THE SOFTWARE.

 @internal
 */

package org.broadleafcommerce.util
{
    /**
     * 
     * Defines the contract for classes which are to provide a specific 
     * implementation of an <code>IMap</code> entry.
     * 
     * <p>
     * An <code>Entry</code> object is essentially a sealed type which 
     * defines two properties: <code>key</code> and <code>value</code>
     * <code>IHashMapEntry</code> provides an interface into an Entry
     * object. <code>IHashMapEntry</code> implementations are intended
     * to provide a Strongly typed implementation of a key / values
     * pair.
     * </p>
     * 
     * @see IMap
     * 
     */    
    public interface IHashMapEntry
    {
        /**
         *
         * Assigns a value to the <code>key</code> property of the 
         * <code>IHashMapEntry</code> implementation.
         *  
         * @param value to assign to the <code>key</code> property
         * 
         */        
        function set key(value:*) : void;

        /**
         *
         * Retrieves the value of the <code>key</code> property of the 
         * <code>IHashMapEntry</code> implementation.
         *  
         * @return value of the <code>key</code> property
         * 
         */            
        function get key() : *;
        
        /**
         *
         * Assignes a value to the <code>value</code> property of an
         * <code>IHashMapEntry</code> implementation.
         *  
         * @param value to assign to the <code>value</code> property
         * 
         */        
        function set value(value:*) : void;
        
        /**
         *
         * Retrieves the value of the <code>value</code> property of an
         * <code>IHashMapEntry</code> implementation. 
         *  
         * @return value of the <code>value</code> property
         * 
         */        
        function get value() : *;
    }
}