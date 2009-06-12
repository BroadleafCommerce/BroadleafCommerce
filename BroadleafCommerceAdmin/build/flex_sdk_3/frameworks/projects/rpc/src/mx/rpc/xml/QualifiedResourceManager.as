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
    
/**
 * QualifiedResourceManager is a helper class that simply maintains
 * the order that resources were added and maps a target namespace to
 * one or more resources.
 */
public class QualifiedResourceManager
{
    /**
     * Constructor.
     */
    public function QualifiedResourceManager()
    {
        super();
    }
    
    /**
     * Adds a resource to a potential Array of resources for a
     * given namespace.
     *
     * @param ns The namespace for the Array of resources.
     *
     * @param resource The resource to add.
     */
    public function addResource(ns:Namespace, resource:Object):void
    {
        if (resources == null)
            resources = [];

        resources.push(resource);

        if (resourcesMap == null)
            resourcesMap = {};
            
        var uri:String = ns.uri;
        if (uri == null)
            uri = "";

        var existingResources:Array = resourcesMap[uri] as Array;
        if (existingResources == null)
            existingResources = [];

        existingResources.push(resource);

        resourcesMap[uri] = existingResources;
    }

    /**
     * Returns an Array of resources for a given target namespace.
     *
     * @param The namespace for the Array of resources.
     *
     * @return An Array of resources.
     */
    public function getResourcesForNamespace(ns:Namespace):Array
    {
        return getResourcesForURI(ns.uri);
    }

    /**
     * Returns an Array of resources for a given target URI.
     *
     * @param uri The URI for the Array of resources.
     *
     * @return An Array of resources.
     */
    public function getResourcesForURI(uri:String):Array
    {
        if (resourcesMap == null)
            return null;

        if (uri == null)
            uri = "";

        var resourcesArray:Array = resourcesMap[uri];
        return resourcesArray;
    }


    /**
     * Gets an Array of all resources.
     *
     * @return An Array of resources.
     */
    public function getResources():Array
    {
        return resources;
    }

    /**
     * This Array is used to preserve order in which resources were
     * added so as to support the order in which they are searched.
     */
    protected var resources:Array;

    /**
     * Maps <code>Namespace.uri</code> to an <code>Array</code> of
     * resources.
     */
    protected var resourcesMap:Object;
}

}
