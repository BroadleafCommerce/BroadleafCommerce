////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.rpc.xml
{

[ExcludeClass]

/**
 * SchemaManager manages multiple Schema definitions by target namespace.
 * 
 * @private
 */
public class SchemaManager extends QualifiedResourceManager
{
    public function SchemaManager()
    {
        super();
        initialScope = [];
        schemaStack = [];
    }

    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------

    public function get namespaces():Object
    {
        if (_namespaces == null)
            _namespaces = {};

        return _namespaces;
    }

    public function set namespaces(value:Object):void
    {
        _namespaces = value;
    }

    /**
     * The constants for the version of Schema that is to be used 
     * in the type system associated with this manager, such as a WSDL
     * types definition.
     * 
     * FIXME: Verify that it is legal for a type system to refer to two
     * different Schemas that use different version of the XML Schema
     * specification? If so, then the schemaConstants could be obtained
     * from each Schema.
     */
    public function get schemaConstants():SchemaConstants
    {
        if (_schemaConstants == null)
            _schemaConstants = SchemaConstants.getConstants();

        return _schemaConstants;
    }

    public function set schemaConstants(value:SchemaConstants):void
    {
        _schemaConstants = value;
    }

    public function get schemaMarshaller():SchemaMarshaller
    {
        if (_schemaMarshaller == null)
        {
            _schemaMarshaller = new SchemaMarshaller(schemaConstants, schemaDatatypes);
        }
        return _schemaMarshaller;
    }

    public function get schemaDatatypes():SchemaDatatypes
    {
        if (_schemaDatatypes == null)
        {
            _schemaDatatypes = SchemaDatatypes.getConstants(schemaConstants.xsdURI);
        }
        return _schemaDatatypes;
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    public function addNamespaces(map:Object):void
    {
        for (var prefix:String in map)
        {
            var ns:Namespace = map[prefix] as Namespace;
            namespaces[prefix] = ns;
        }
    }

    /**
     * Adds a Schema to the current scope. If a Schema already exists in
     * the scope then the scope is promoted to an Array of Schemas.
     */
    public function addSchema(schema:Schema, toCurrentScope:Boolean = true):void
    {
        addResource(schema.targetNamespace, schema);
        
        var schemaSet:Array;
        if (toCurrentScope == true)
            schemaSet = schemaStack.pop();

        if (schemaSet == null)
            schemaSet = [];

        schemaSet.push(schema);
        if (!schemaSet.hasOwnProperty("current"))
            schemaSet["current"] = schemaSet[0];

        schemaStack.push(schemaSet);

        // We keep track of the initial scope for reset()
        if (schemaStack.length == 1)
            initialScope = schemaStack[0];
    }

    /**
     * Returns the Schema that was last used to retrieve a definition.
     */
    public function get currentSchema():Schema
    {
        var schema:Schema;

        var schemaSet:Array = currentScope();
        if (schemaSet.hasOwnProperty("current"))
            schema = schemaSet["current"];

        return schema;
    }

    public function currentScope():Array
    {
        var current:Array = schemaStack.pop();
        if (current != null)
            schemaStack.push(current);
        else
            current = [];
        return current;
    }

    /**
     * Look for the definition of the given QName in all schemas in the current
     * scope. If the definition could not be found the function returns null.
     * 
     * @param name The name of the component defined in a schema.
     * @param componentTypes A list of structural element types that may have
     * the name provided, such as &lt;element&gt;, &lt;complexType&gt;, &lt;simpleType&gt;,
     * &lt;attribute&gt; or &lt;attributeGroup&gt;. The first one found is returned.
     */
    public function getNamedDefinition(name:QName, ...componentTypes:Array):XML
    {
        var schemas:Array = currentScope();

        for (var s:int = 0; s < schemas.length; s++)
        {
            var schema:Schema = schemas[s];
            componentTypes.unshift(name);
            var result:Object = schema.getNamedDefinition.apply(schema, componentTypes);
            if (result != null)
            {
                var definition:XML = result.definition as XML;
                pushSchemaInScope(result.schema);
                return definition;
            }
        }

        return null;
    }


    /**
     * Locate a schema for the given namespace and push it to a
     * new scope level.
     */
    public function pushNamespaceInScope(nsParam:*):Boolean
    {
        var ns:Namespace = new Namespace(nsParam);
        var schemas:Array = currentScope();

        for (var s:int = 0; s < schemas.length; s++)
        {
            var schema:Schema = schemas[s];
            if (schema.targetNamespace.uri == ns.uri)
            {
                pushSchemaInScope(schema);
                return true;
            }
        }
        return false;
    }

    /**
     * Push the given Schema to a new scope level, and set it as the
     * current schema for that scope.
     * 
     * @param schema The Schema to push to a new scope
     */
    public function pushSchemaInScope(schema:Schema):void
    {
        if (schema != null)
        {
            var newSchemaSet:Array = [ schema ];
            newSchemaSet["current"] = newSchemaSet[0];
            schemaStack.push(newSchemaSet);
        }
    }

    /**
     * @private FIXME: Find a better method name and/or document
     */
    public function getOrCreatePrefix(uri:String):String
    {
        var result:String;
        var ns:Namespace;

        // Check top level namespaces
        for each (ns in namespaces)
        {
            if  (ns.uri == uri)
            {
                return ns.prefix;
            }
        }

        // Check current schema namespaces
        if (currentSchema != null)
        {
            var schemaNamespaces:Object = currentSchema.namespaces;
            for each (ns in schemaNamespaces)
            {
                if  (ns.uri == uri)
                {
                    return ns.prefix;
                }
            }
        }

        var prefixString:String = "ns";
        var nameSpace:Namespace;
        var newPrefix:String = prefixString + namespaceCount;
        if (namespaces[newPrefix] != null)
        {
            namespaceCount++;
            newPrefix = prefixString + namespaceCount;
            nameSpace = new Namespace(newPrefix, uri);
            namespaces[newPrefix] = nameSpace;
            return newPrefix;
        }
        else
        {
            nameSpace = new Namespace(newPrefix, uri);
            namespaces[newPrefix] = nameSpace;
            return newPrefix;
        }      
        return null;
    }

    public function getQNameForAttribute(ncname:String, form:String = null):QName
    {
        var qname:QName;
        if (form == "qualified"
            || (form == null && currentSchema.attributeFormDefault == "qualified"))
        {
            qname = new QName(currentSchema.targetNamespace.uri, ncname);
        }
        else
        {
            qname = new QName("", ncname);
        }
        return qname;
    }

    public function getQNameForElement(ncname:String, form:String = null):QName
    {
        var qname:QName;
        if (form == "qualified"
            || ((form == null || form == "")
                && currentSchema.elementFormDefault == "qualified"))
        {
            qname = new QName(currentSchema.targetNamespace.uri, ncname);
        }
        else
        {
            qname = new QName("", ncname);
        }
        return qname;
    }


    /**
     * Resolves a prefixed name back into a QName based on the prefix to
     * namespace mappings.
     * 
     * @param prefixedName The name to be resolved. Can be prefixed or unqualified.
     * @param parent The XML node where prefixedName appears. Allows local xmlns
     * declarations to be examined
     * @param qualifyToTargetNamespace A switch controlling the behavior for
     * unqualified names. If false, unqualified names are assumed to be prefixed
     * by "" and a xmlns="..." declaration is looked up. If no xmlns=".."
     * declaration is in scope, and the parent node is in the default namespace,
     * the prefixedName is resolved to the default namespace. Otherwise, it is
     * resolved to the targetNamespace of the current schema. If qualifyToTargetNamespace
     * is true, unqualified names are assumed to be in the target namespace of
     * the current schema, regardless of declarations for unprefixed namespaces.
     * qualifyToTargetNamespace should be true when resolving names coming from
     * the following schema attributes: name, ref.
     */ 
    public function getQNameForPrefixedName(prefixedName:String, parent:XML=null,
                                    qualifyToTargetNamespace:Boolean=false):QName
    {
        var qname:QName;

        // Separate into prefix and local name
        var prefix:String;
        var localName:String;
        var prefixIndex:int = prefixedName.indexOf(":");
        if (prefixIndex > 0)
        {
            prefix = prefixedName.substr(0, prefixIndex);
            localName = prefixedName.substr(prefixIndex + 1);
        }
        else
        {
            localName = prefixedName;
        }

        var ns:Namespace;
        
        // First, map unqualified names to the target namespace, if the flag
        // is explicitly set. (Used when looking up unqualified names by "ref")
        if (prefix == null && qualifyToTargetNamespace == true)
        {
            ns = currentSchema.targetNamespace;
        }
        
        // Otherwise, assume that unqualified names are in the default namespace.
        if (prefix == null)
        {
            prefix = "";
        }
        
        // First, check if a parent XML has a local definition for this
        // namespace...
        if (ns == null)
        {
            if (parent != null)
            {
                var localNamespaces:Array = parent.inScopeNamespaces();
                for each (var localNS:Namespace in localNamespaces)
                {
                    if (localNS.prefix == prefix)
                    {
                        ns = localNS;
                        break;
                    }
                }
            }
        }

        // Next, check top level namespaces
        if (ns == null)
        {
            ns = namespaces[prefix];
        }

        // Next, check current schema namespaces
        if (ns == null)
        {
            ns = currentSchema.namespaces[prefix];
        }

        if (ns == null)
        {
            // Check if parent XML node is in the default namespace
            var parentNS:Namespace = (parent != null) ? parent.namespace() : null;
            if (parentNS != null && parentNS.prefix == "")
                ns = parentNS;
            // Otherwise we use the target namespace of the current definition
            else
                ns = currentSchema.targetNamespace;
        }

        if (ns != null)
            qname = new QName(ns.uri, localName);
        else
            qname = new QName("", localName);

        return qname;
    }

    /**
     * Converts ActionScript to XML based on default rules
     * established for each of the built-in XML Schema types.
     */
    public function marshall(value:*, type:QName = null, restriction:XML = null):String
    {
        return schemaMarshaller.marshall(value, type, restriction);
    }

    /**
     * Informs the SchemaManager that the current definition is no
     * longer being processed so we release the associated Schema from the
     * current scope of qualified definitions.
     */
    public function releaseScope():*
    {
        return schemaStack.pop();
    }

    /**
     * Reverts to initialScope.
     */
    public function reset():void
    {
        namespaceCount = 0;
        schemaStack = [];
        schemaStack.push(initialScope);
    }

    /**
     * Converts XML to ActionScript based on default rules
     * established for each of the built-in XML Schema types.
     */
    public function unmarshall(value:*, type:QName = null, restriction:XML = null):*
    {
        return schemaMarshaller.unmarshall(value, type, restriction);
    }


    //--------------------------------------------------------------------------
    //
    // Private Variables
    // 
    //--------------------------------------------------------------------------

    private var namespaceCount:uint = 0;

    /**
     * A Stack of Schemas which records the current scope and the last Schema
     * that was accessed to locate a definition. Multiple Schemas may be
     * placed in Scope at any level by adding them to the Stack as an Array.
     */
    private var schemaStack:Array;
    private var initialScope:*;
    private var _namespaces:Object;
    private var _schemaMarshaller:SchemaMarshaller;
    private var _schemaConstants:SchemaConstants;
    private var _schemaDatatypes:SchemaDatatypes;
}

}