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
    import flash.utils.Dictionary;
    
    /**
     *
     * IMap implementation which dynamically creates a HashMap
     * of key / value pairs and provides a standard API for
     * working with the map
     *
     * @example The following example demonstrates a typical 
     * use-case in a <code>Hashmap</code> instance has keys 
     * and values added and retrieved.
     * 
     * <listing version="3.0">
     *
     * import com.ericfeminella.collections.HashMap;
     * import com.ericfeminella.collections.IMap;
     *
     * private function init() : void
     * {
     *     var map:IMap = new HashMap();
     *     map.put("a", "value A");
     *     map.put("b", "value B");
     *     map.put("c", "value C");
     *     map.put("x", "value X");
     *     map.put("y", "value Y");
     *     map.put("z", "value Z");
     *
     *     trace( map.getKeys() );
     *     trace( map.getValues() );
     *     trace( map.size() );
     *
     *     // outputs the following:
     *     // b,x,z,a,c,y
     *     // value B,value X,value Z,value A,value C,value Y
     *     // 6
     * }
     *
     * </listing>
     *
     * @see http://livedocs.adobe.com/flex/3/langref/flash/utils/Dictionary.html
     * @see com.ericfeminella.collections.IMap
     *
     */
    public class HashMap implements IMap
    {
        /**
         * 
         * Defines the underlying object which contains the key / value 
         * mappings of an <code>IMap</code> implementation.
         * 
         * @see http://livedocs.adobe.com/flex/3/langref/flash/utils/Dictionary.html
         * 
         */
        protected var map:Dictionary;
        
        /**
         *
         * Creates a new HashMap instance. By default, weak key
         * references are used in order to ensure that objects are
         * eligible for Garbage Collection iimmediatly after they
         * are no longer being referenced, if the only reference to
         * an object is in the specified HashMap object, the key is
         * eligible for garbage collection and is removed from the
         * table when the object is collected
         *
         * @example
         * 
         * <listing version="3.0">
         *
         * import com.ericfeminella.collections.HashMap;
         * import com.ericfeminella.collections.IMap;
         *
         * var map:IMap = new HashMap( false );
         *
         * </listing>
         *
         * @param specifies if weak key references should be used
         *
         */
        public function HashMap(useWeakReferences:Boolean = true)
        {
            map = new Dictionary( useWeakReferences );
        }

        /**
         *
         * Adds a key and value to the HashMap instance
         *
         * @example
         * <listing version="3.0">
         *
         * import com.ericfeminella.collections.HashMap;
         * import com.ericfeminella.collections.IMap;
         *
         * var map:IMap = new HashMap();
         * map.put( "user", userVO );
         *
         * </listing>
         *
         * @param the key to add to the map
         * @param the value of the specified key
         *
         */
        public function put(key:*, value:*) : void
        {
            map[key] = value;
        }
        
        /**
         *
         * Places all name / value pairs into the current
         * <code>IMap</code> instance.
         *
         * @example
         * <listing version="3.0">
         *
         * import com.ericfeminella.collections.HashMap;
         * import com.ericfeminella.collections.IMap;
         *
         * var table:Object = {a: "foo", b: "bar"};
         * var map:IMap = new HashMap();
         * map.putAll( table );
         * 
         * trace( map.getValues() );
         * // foo, bar
         * 
         * </listing>
         * 
         * @param an <code>Object</code> of name / value pairs
         * 
         */        
        public function putAll(table:Dictionary) : void
        {
            for (var prop:String in table)
            {
                put( prop, table[prop] );
            }
        }
        
        /**
         *
         * <code>putEntry</code> is intended as a pseudo-overloaded 
         * <code>put</code> implementation whereby clients may call
         * <code>putEntry</code> to pass an <code>IHashMapEntry</code>
         * implementation.
         *  
         * @param concrete <code>IHashMapEntry</code> implementation
         * 
         */        
        public function putEntry(entry:IHashMapEntry) : void
        {
            put( entry.key, entry.value );
        }
        
        /**
         *
         * Removes a key and value from the HashMap instance
         *
         * @example
         * <listing version="3.0">
         *
         * import com.ericfeminella.collections.HashMap;
         * import com.ericfeminella.collections.IMap;
         *
         * var map:IMap = new HashMap();
         * map.put( "admin", adminVO );
         * map.remove( "admin" );
         *
         * </listing>
         *
         * @param the key to remove from the map
         *
         */
        public function remove(key:*) : void
        {
            delete map[key];
        }

        /**
         *
         * Determines if a key exists in the HashMap instance
         *
         * @example
         * <listing version="3.0">
         *
         * import com.ericfeminella.collections.HashMap;
         * import com.ericfeminella.collections.IMap;
         *
         * var map:IMap = new HashMap();
         * map.put( "admin", adminVO );
         *
         * trace( map.containsKey( "admin" ) ); //true
         *
         * </listing>
         *
         * @param  the key in which to determine existance in the map
         * @return true if the key exisits, false if not
         *
         */
        public function containsKey(key:*) : Boolean
        {
            return map.hasOwnProperty( key );
        }

        /**
         *
         * Determines if a value exists in the HashMap instance
         * 
         * <p>
         * If multiple keys exists in the map with the same value,
         * the first key located which is mapped to the specified
         * key will be returned.
         * </p>
         *
         * @example
         * 
         * <listing version="3.0">
         *
         * import com.ericfeminella.collections.HashMap;
         * import com.ericfeminella.collections.IMap;
         *
         * var map:IMap = new HashMap();
         * map.put( "admin", adminVO );
         *
         * trace( map.containsValue( adminVO ) ); //true
         *
         * </listing>
         *
         * @param  the value in which to determine existance in the map
         * @return true if the value exisits, false if not
         *
         */
        public function containsValue(value:*) : Boolean
        {
            var result:Boolean = false;

            for ( var key:* in map )
            {
                if ( map[key] == value )
                {
                    result = true;
                    break;
                }
            }
            return result;
        }

        /**
         *
         * Returns the value of the specified key from the HashMap 
         * instance.
         * 
         * <p>
         * If multiple keys exists in the map with the same value,
         * the first key located which is mapped to the specified
         * value will be returned.
         * </p>
         * 
         * @example
         * <listing version="3.0">
         *
         * import com.ericfeminella.collections.HashMap;
         * import com.ericfeminella.collections.IMap;
         *
         * var map:IMap = new HashMap();
         * map.put( "admin", adminVO );
         *
         * trace( map.getKey( adminVO ) ); //admin
         *
         * </listing>
         *
         * @param  the key in which to retrieve the value of
         * @return the value of the specified key
         *
         */
        public function getKey(value:*) : *
        {
            var id:String = null;

            for ( var key:* in map )
            {
                if ( map[key] == value )
                {
                    id = key;
                    break;
                }
            }
            return id;
        }

        /**
         *
         * Returns each key added to the HashMap instance
         *
         * @example
         * 
         * <listing version="3.0">
         *
         * import com.ericfeminella.collections.HashMap;
         * import com.ericfeminella.collections.IMap;
         *
         * var map:IMap = new HashMap();
         * map.put( "admin", adminVO );
         * map.put( "editor", editorVO );
         *
         * trace( map.getKeys() ); //admin, editor
         *
         * </listing>
         *
         * @return Array of key identifiers
         *
         */
        public function getKeys() : Array
        {
            var keys:Array = [];

            for (var key:* in map)
            {
                keys.push( key );
            }
            return keys;
        }

        /**
         *
         * Retrieves the value of the specified key from the HashMap instance
         *
         * @example
         * <listing version="3.0">
         *
         * import com.ericfeminella.collections.HashMap;
         * import com.ericfeminella.collections.IMap;
         *
         * var map:IMap = new HashMap();
         * map.put( "admin", adminVO );
         * map.put( "editor", editorVO );
         *
         * trace( map.getValue( "editor" ) ); //[object, editorVO]
         *
         * </listing>
         *
         * @param  the key in which to retrieve the value of
         * @return the value of the specified key, otherwise returns undefined
         *
         */
        public function getValue(key:*) : *
        {
            return map[key];
        }

        /**
         *
         * Retrieves each value assigned to each key in the HashMap instance
         *
         * @example
         * <listing version="3.0">
         *
         * import com.ericfeminella.collections.HashMap;
         * import com.ericfeminella.collections.IMap;
         *
         * var map:IMap = new HashMap();
         * map.put( "admin", adminVO );
         * map.put( "editor", editorVO );
         *
         * trace( map.getValues() ); //[object, adminVO],[object, editorVO]
         *
         * </listing>
         *
         * @return Array of values assigned for all keys in the map
         *
         */
        public function getValues() : Array
        {
            var values:Array = [];

            for (var key:* in map)
            {
                values.push( map[key] );
            }
            return values;
        }

        /**
         *
         * Determines the size of the HashMap instance
         *
         * @example
         * <listing version="3.0">
         *
         * import com.ericfeminella.collections.HashMap;
         * import com.ericfeminella.collections.IMap;
         *
         * var map:IMap = new HashMap();
         * map.put( "admin", adminVO );
         * map.put( "editor", editorVO );
         *
         * trace( map.size() ); //2
         *
         * </listing>
         *
         * @return the current size of the map instance
         *
         */
        public function size() : int
        {
            var length:int = 0;

            for (var key:* in map)
            {
                length++;
            }
            return length;
        }

        /**
         *
         * Determines if the current HashMap instance is empty
         *
         * @example
         * <listing version="3.0">
         *
         * import com.ericfeminella.collections.HashMap;
         * import com.ericfeminella.collections.IMap;
         *
         * var map:IMap = new HashMap();
         * trace( map.isEmpty() ); //true
         *
         * map.put( "admin", adminVO );
         * trace( map.isEmpty() ); //false
         *
         * </listing>
         *
         * @return true if the current map is empty, false if not
         *
         */
        public function isEmpty() : Boolean
        {
            return size() <= 0;
        }

        /**
         *
         * Resets all key value assignments in the HashMap instance to null
         *
         * @example
         * <listing version="3.0">
         *
         * import com.ericfeminella.collections.HashMap;
         * import com.ericfeminella.collections.IMap;
         *
         * var map:IMap = new HashMap();
         * map.put( "admin", adminVO );
         * map.put( "editor", editorVO );
         * map.reset();
         *
         * trace( map.getValues() ); //null, null
         *
         * </listing>
         *
         */
        public function reset() : void
        {
            for ( var key:* in map )
            {
                map[key] = undefined;
            }
        }

        /**
         *
         * Resets all key / values defined in the HashMap instance to null
         * with the exception of the specified key
         *
         * @example
         * <listing version="3.0">
         *
         * import com.ericfeminella.collections.HashMap;
         * import com.ericfeminella.collections.IMap;
         *
         * var map:IMap = new HashMap();
         * map.put( "admin", adminVO );
         * map.put( "editor", editorVO );
         *
         * trace( map.getValues() ); //[object, adminVO],[object, editorVO]
         *
         * map.resetAllExcept( "editor", editorVO );
         * trace( map.getValues() ); //null,[object, editorVO]
         *
         * </listing>
         *
         * @param the key which is not to be cleared from the map
         *
         */
        public function resetAllExcept(keyId:*) : void
        {
            for ( var key:* in map )
            {
                if ( key != keyId )
                {
                    map[key] = undefined;
                }
            }
        }

        /**
         *
         * Resets all key / values in the HashMap instance to null
         *
         * @example
         * <listing version="3.0">
         *
         * import com.ericfeminella.collections.HashMap;
         * import com.ericfeminella.collections.IMap;
         *
         * var map:IMap = new HashMap();
         * map.put( "admin", adminVO );
         * map.put( "editor", editorVO );
         * trace( map.size() ); //2
         *
         * map.clear();
         * trace( map.size() ); //0
         *
         * </listing>
         *
         */
        public function clear() : void
        {
            for ( var key:* in map )
            {
                remove( key );
            }
        }

        /**
         *
         * Clears all key / values defined in the HashMap instance
         * with the exception of the specified key
         *
         * @example
         * <listing version="3.0">
         *
         * import com.ericfeminella.collections.HashMap;
         * import com.ericfeminella.collections.IMap;
         *
         * var map:IMap = new HashMap();
         * map.put( "admin", adminVO );
         * map.put( "editor", editorVO );
         * trace( map.size() ); //2
         *
         * map.clearAllExcept( "editor", editorVO );
         * trace( map.getValues() ); //[object, editorVO]
         * trace( map.size() ); //1
         *
         * </listing>
         *
         * @param the key which is not to be cleared from the map
         *
         */
        public function clearAllExcept(keyId:*) : void
        {
            for ( var key:* in map )
            {
                if ( key != keyId )
                {
                    remove( key );
                }
            }
        }
        
        /**
         *
         * Returns an <code>Array</code> of <code>IHashMapEntry</code> 
         * objects based on the underlying internal map.
         *  
         * @param <code>Array</code> of <code>IHashMapEntry</code> objects
         * 
         */        
        public function getEntries() : Array
        {
            var list:Array = new Array();

            for ( var key:* in map )
            {
                list.push( new HashMapEntry( key, map[key] ) );
            }
            return list
        }
    }
}