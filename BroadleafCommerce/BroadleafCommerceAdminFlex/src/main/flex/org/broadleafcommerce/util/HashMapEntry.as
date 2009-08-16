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
     * Provides a strongly typed implementation of a key/value pairs
     * 
     * @see IHashMapEntry
     * @see IMap
     * 
     */    
    public class HashMapEntry implements IHashMapEntry
    {
        /**
         * 
         * Defines the <code>key</code> property of the key / value
         * pair.
         * 
         */
        protected var _key: * ;
        
        /**
         * 
         * Defines the <code>value</code> property of the key / value
         * pair.
         * 
         */
        protected var _value: * ;
        
        /**
         *
         * <code>HashMapEntry</code> constructor accepts values for 
         * the <code>key</code> and <code>value</code> properties of
         * an <code>IHashMapEntry</code>
         * 
         * @param value to assign to the <code>key</code> property
         * @param value to assign to the <code>value</code> property
         * 
         */        
        public function HashMapEntry(key:*, value:*)
        {
            this._key   = key;
            this._value = value;
        }
        
        /**
         *
         * Assigns a value to the <code>key</code> property of the 
         * <code>IHashMapEntry</code> implementation.
         *  
         * @param value to assign to the <code>key</code> property
         * 
         */    
        public function set key(key:*) : void
        {
            _key = key;
        }

        /**
         *
         * Retrieves the value of the <code>key</code> property of the 
         * <code>IHashMapEntry</code> implementation.
         *  
         * @return value of the <code>key</code> property
         * 
         */    
        public function get key() : *
        {
            return _key;
        }
        
        /**
         *
         * Assignes a value to the <code>value</code> property of an
         * <code>IHashMapEntry</code> implementation.
         *  
         * @param value to assign to the <code>value</code> property
         * 
         */    
        public function set value(value:*) : void
        {
            _value = value;
        }
        
        /**
         *
         * Retrieves the value of the <code>value</code> property of an
         * <code>IHashMapEntry</code> implementation. 
         *  
         * @return value of the <code>value</code> property
         * 
         */
        public function get value() : *
        {
            return _value;
        }
    }
}