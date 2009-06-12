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

import flash.events.Event;
import flash.events.EventDispatcher;

import mx.rpc.AsyncToken;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.ResultEvent;
import mx.rpc.events.SchemaLoadEvent;
import mx.rpc.http.HTTPService;
import mx.utils.URLUtil;


[Event(name="fault", type="mx.rpc.events.FaultEvent")]
[Event(name="schemaLoad", type="mx.rpc.events.SchemaLoadEvent")]

[ExcludeClass]

/**
 * Manages the loading of an XML Schema at runtime, including all imports and
 * includes.
 * 
 * @private
 */
public class SchemaLoader extends XMLLoader
{
    public function SchemaLoader(httpService:HTTPService = null)
    {
        super(httpService);

        locationMap = {};
    }

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------
    
    /**
     * Asynchronously loads an XSD Schema for a given URL including any
     * XSD imports and includes.
     */
    override public function load(url:String):void
    {
        // Resolve any relative URLs to the loader root URL
        url = getQualifiedLocation(url);
        loadSchema(url);
    }

    public function schemaImports(schema:Schema, parentLocation:String, schemaManager:SchemaManager = null):void
    {
        var importQName:QName = schema.schemaConstants.importQName;
        var schemaXML:XML = schema.xml;
        var imports:XMLList = schemaXML.elements(importQName);
        for each (var importNode:XML in imports)
        {
            var location:String = importNode.attribute("schemaLocation").toString();
            var importURI:String = importNode.attribute("namespace").toString();

            // FIXME: Check location and importNS are valid
            var importNS:Namespace = new Namespace(importURI);
            var nsSchema:Namespace = new Namespace(schema.schemaConstants.xsdURI);

            if (location == "")
            {
                // If we have a schemaManager, look to see whether this
                // namespace is recognized by some managing context like a WSDL
                if (schemaManager != null)
                {
                    var importedSchemas:Array = schemaManager.getResourcesForURI(importURI);
                    if (importedSchemas != null)
                    {
                        for each (var importedSchema:Schema in importedSchemas)
                        {
                            schema.addImport(importNS, importedSchema);
                        }

                        // Delete import now that we have resolved the namespace
                        // to a schema
                        delete schemaXML[importQName].(@namespace == importURI)[0];
                        schema.xml  = schemaXML;
                    }
                }
            }
            else
            {
                // Delete import tag with a concrete locations to avoid
                // re-loading the same schema import
                delete schemaXML[importQName].(@namespace == importURI)[0];
                schema.xml  = schemaXML;

                // Resolve any relative locations to a fully qualified path
                location = getQualifiedLocation(location, parentLocation);

                var existing:Schema = locationMap[location];
                if (existing == null)
                {
                    loadSchema(location, schema, importNS, LOAD_IMPORT);
                }
                else
                {
                    schema.addImport(importNS, existing);
                }
            }
        }
    }
    
    public function schemaIncludes(schema:Schema, parentLocation:String):void
    {
        var schemaINCList:XMLList = schema.xml.elements(schema.schemaConstants.includeQName);
        for each (var schemaINCXML:XML in schemaINCList)
        {
            var location:String = schemaINCXML.@schemaLocation;
            
            // Resolve any relative locations to a fully qualified path
            location = getQualifiedLocation(location, parentLocation);
            
            var existing:XMLList = locationMap[location] as XMLList;

            var schemaQName:QName = schemaINCXML.name();
            var schemaXML:XML = schema.xml;

            //delete the include tag inside the existing schema
            var nsSchema:Namespace = new Namespace(schema.schemaConstants.xsdURI);
            var includeQName:QName = schema.schemaConstants.includeQName;
            delete schemaXML[includeQName].(@schemaLocation==location)[0];
            schema.xml  = schemaXML;

            if (existing == null)
            {
                loadSchema(location, schema, schema.targetNamespace, LOAD_INCLUDE);
            }
            else
            {
                schema.addInclude(existing);
            }
        }
    }

    override protected function resultHandler(event:ResultEvent):void
    {
        super.resultHandler(event);

        var xml:XML = XML(event.result);
        var token:AsyncToken = event.token;
        var location:String = token == null ? null : token.location;
        
        if (token.parent != null)
        {
            var parentSchema:Schema = token.parent as Schema;

            // Handle XSD Include
            if (token.loadType == LOAD_INCLUDE)
            {
                var nsINC:Array = xml.namespaceDeclarations();
                for each (var nsSchema:Namespace in nsINC)
                {
                    parentSchema.xml.addNamespace(nsSchema);       
                }
                
                var children:XMLList = xml.children();
                
                parentSchema.addInclude(children);
                
                locationMap[location] = children;

                // Check parent again for new XML Schema includes
                schemaIncludes(parentSchema, location);

                // Check parent again for new XML Schema imports
                schemaImports(parentSchema, location);
            }
            // Handle XSD Import
            else 
            {
                var schema:Schema = new Schema(xml);
                locationMap[location] = schema;
                var ns:Namespace = token.importNamespace as Namespace;

                // FIXME: manage schemas without namespaces as they represent
                // unqualified types
                parentSchema.addImport(ns, schema);
                
                // Check imported schema for includes
                schemaIncludes(schema, location);

                // Check imported schema for imports
                schemaImports(schema, location);
            }
        }
        else
        {
            topLevelSchema = new Schema(xml);
            locationMap[location] = topLevelSchema;
        
            // Check for top level schema for includes
            schemaIncludes(topLevelSchema, location);

            // Check for top level schema for imports
            schemaImports(topLevelSchema, location);
        }

        if (loadsOutstanding <= 0)
        {
            var loadEvent:SchemaLoadEvent = SchemaLoadEvent.createEvent(topLevelSchema, location);
            dispatchEvent(loadEvent);
        }
    }

    private function loadSchema(location:String, parent:Schema = null,
        ns:Namespace = null, loadType:String = null):AsyncToken
    {
        var token:AsyncToken = internalLoad(location);

        if (token != null)
        {
            token.parent = parent;
            token.importNamespace = ns;
            token.loadType = loadType;
        }

        return token;
    }
    
    private var topLevelSchema:Schema;
    private var locationMap:Object;
    private var _schemaManager:SchemaManager;
    private static const LOAD_INCLUDE:String = "include";
    private static const LOAD_IMPORT:String = "import";
}
    
}