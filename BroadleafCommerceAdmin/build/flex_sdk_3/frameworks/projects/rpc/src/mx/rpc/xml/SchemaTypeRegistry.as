////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.rpc.xml
{

import flash.utils.getDefinitionByName;
import flash.utils.getQualifiedClassName;

/**
 * XMLDecoder uses this class to map an XML Schema type by QName to an
 * ActionScript Class so that it can create strongly typed objects when
 * decoding content. If the type is unqualified the QName uri may
 * be left null or set to the empty String.
 * <p>
 * It is important to note that the desired Class must be linked into the SWF
 * and possess a default constructor in order for the XMLDecoder to create a
 * new instance of the type, otherwise an anonymous Object will be used to
 * hold the decoded properties.
 * </p>
 */
public class SchemaTypeRegistry
{
    //--------------------------------------------------------------------------
    //
    // Class Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * Returns the sole instance of this singleton class, creating it if it
     * does not already exist.
     *
     * @return Returns the sole instance of this singleton class, creating it
     * if it does not already exist.
     */
    public static function getInstance():SchemaTypeRegistry
    {
        if (_instance == null)
            _instance = new SchemaTypeRegistry();

        return _instance;
    }


    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     * @private
     */
    public function SchemaTypeRegistry()
    {
        super();
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     * Looks for a registered Class for the given type.
     * @param type The QName or String representing the type name.
     * @return Returns the Class for the given type, or null of the type
     * has not been registered.
     */
    public function getClass(type:Object):Class
    {
        var c:Class;
        if (type != null)
        {
            var key:String = getKey(type);
            var definitionName:String = classMap[key] as String;

            if (definitionName != null)
                c = getDefinitionByName(definitionName) as Class;
        }
        return c;
    }

    /**
     * Returns the Class for the collection type represented by the given
     * Qname or String.
     *
     * @param type The QName or String representing the collection type name.
     *
     * @return Returns the Class for the collection type represented by 
     * the given Qname or String.
     */
    public function getCollectionClass(type:Object):Class
    {
        var c:Class;
        if (type != null)
        {
            var key:String = getKey(type);
            var definitionName:String = collectionMap[key] as String;

            if (definitionName != null)
                c = getDefinitionByName(definitionName) as Class;
        }
        return c;
    }

    /**
     * Maps a type QName to a Class definition. The definition can be a String
     * representation of the fully qualified class name or an instance of the
     * Class itself.
     * @param type The QName or String representation of the type name.
     * @param definition The Class itself or class name as a String.
     */
    public function registerClass(type:Object, definition:Object):void
    {
        register(type, definition, classMap);
    }

    /**
     * Maps a type name to a collection Class. A collection is either the 
     * top level Array type, or an implementation of <code>mx.collections.IList</code>. 
     * The definition can be a String representation of the fully qualified
     * class name or an instance of the Class itself.
     *
     * @param type The QName or String representation of the type name.
     *
     * @param definition The Class itself or class name as a String.
     */
    public function registerCollectionClass(type:Object, definition:Object):void
    {
        register(type, definition, collectionMap);
    }

    /**
     * Removes a Class from the registry for the given type.
     * @param type The QName or String representation of the type name.
     */
    public function unregisterClass(type:Object):void
    {
        if (type != null)
        {
            var key:String = getKey(type);
            delete classMap[key];
        }
    }

    /**
     * Removes a collection Class from the registry for the given type.
     * @param type The QName or String representation of the collection type
     * name.
     */
    public function unregisterCollectionClass(type:Object):void
    {
        if (type != null)
        {
            var key:String = getKey(type);
            delete collectionMap[key];
        }
    }

    /**
     * @private
     * Converts the given type name into a consistent String representation
     * that serves as the key to the type map.
     * @param type The QName or String representation of the type name.
     */
    private function getKey(type:Object):String
    {
        var key:String;
        if (type is QName)
        {
            var typeQName:QName = type as QName;
            if (typeQName.uri == null || typeQName.uri == "")
                key = typeQName.localName;
            else
                key = typeQName.toString();
        }
        else
        {
            key = type.toString();
        }
        return key;
    }

    /**
     * @private
     */
    private function register(type:Object, definition:Object, map:Object):void
    {
        var key:String = getKey(type);
        var definitionName:String;
        if (definition is String)
            definitionName = definition as String;
        else
            definitionName = getQualifiedClassName(definition);

        map[key] = definitionName;
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

    private var classMap:Object = {};
    private var collectionMap:Object = {};
    private static var _instance:SchemaTypeRegistry;
}

}
