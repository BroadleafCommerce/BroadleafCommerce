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

package mx.modules
{

import flash.display.DisplayObject;
import flash.display.DisplayObjectContainer;
import flash.system.ApplicationDomain;
import mx.containers.VBox;
import mx.core.IDeferredInstantiationUIComponent;
import mx.events.FlexEvent;
import mx.events.ModuleEvent;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when the ModuleLoader starts to load a URL.
 *
 *  @eventType mx.events.FlexEvent.LOADING
 */
[Event(name="loading", type="flash.events.Event")]

/**
 *  Dispatched when the ModuleLoader is given a new URL.
 *
 *  @eventType mx.events.FlexEvent.URL_CHANGED
 */
[Event(name="urlChanged", type="flash.events.Event")]

/**
 *  Dispatched when information about the module is 
 *  available (with the <code>info()</code> method), 
 *  but the module is not yet ready.
 *
 *  @eventType mx.events.ModuleEvent.SETUP
 */
[Event(name="setup", type="mx.events.ModuleEvent")]

/**
 *  Dispatched when the module is finished loading.
 *
 *  @eventType mx.events.ModuleEvent.READY
 */
[Event(name="ready", type="mx.events.ModuleEvent")]

/**
 *  Dispatched when the module throws an error.
 *
 *  @eventType mx.events.ModuleEvent.ERROR
 */
[Event(name="error", type="mx.events.ModuleEvent")]

/**
 *  Dispatched at regular intervals as the module loads.
 *
 *  @eventType mx.events.ModuleEvent.PROGRESS
 */
[Event(name="progress", type="mx.events.ModuleEvent")]

/**
 *  Dispatched when the module data is unloaded.
 *
 *  @eventType mx.events.ModuleEvent.UNLOAD
 */
[Event(name="unload", type="mx.events.ModuleEvent")]

/**
 *  ModuleLoader is a component that behaves much like a SWFLoader except
 *  that it follows a contract with the loaded content. This contract dictates that the child
 *  SWF file implements IFlexModuleFactory and that the factory
 *  implemented can be used to create multiple instances of the child class
 *  as needed.
 *
 *  <p>The ModuleLoader is connected to deferred instantiation and ensures that
 *  only a single copy of the module SWF file is transferred over the network by using the
 *  ModuleManager singleton.</p>
 *  
 *  @see mx.controls.SWFLoader
 */
public class ModuleLoader extends VBox
                          implements IDeferredInstantiationUIComponent
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function ModuleLoader()
    {
        super();
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var module:IModuleInfo;

    /**
     *  @private
     */
    private var loadRequested:Boolean = false;

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  applicationDomain
    //----------------------------------

    /**
     *  The application domain to load your module into.
     *  Application domains are used to partition classes that are in the same 
     *  security domain. They allow multiple definitions of the same class to 
     *  exist and allow children to reuse parent definitions.
     *  
     *  @see flash.system.ApplicationDomain
     *  @see flash.system.SecurityDomain
     */
    public var applicationDomain:ApplicationDomain;

    //----------------------------------
    //  child
    //----------------------------------

    /**
     *  The DisplayObject created from the module factory.
     */
    public var child:DisplayObject;

    //----------------------------------
    //  url
    //----------------------------------

    /**
     *  @private
     *  Storage for the url property.
     */
    private var _url:String = null;

    /**
     *  The location of the module, expressed as a URL.
     */
    public function get url():String
    {
        return _url;
    }

    /**
     *  @private
     */
    public function set url(value:String):void
    {
        if (value == _url)
            return;

        var wasLoaded:Boolean = false;
        
        if (module)
        {
            module.removeEventListener(ModuleEvent.PROGRESS,
                                       moduleProgressHandler);
            module.removeEventListener(ModuleEvent.SETUP, moduleSetupHandler);
            module.removeEventListener(ModuleEvent.READY, moduleReadyHandler);
            module.removeEventListener(ModuleEvent.ERROR, moduleErrorHandler);
            module.removeEventListener(ModuleEvent.UNLOAD, moduleUnloadHandler);

            module.release();
            module = null;

            if (child)
            {
                removeChild(child);
                child = null;
            }
        }

        _url = value;

        dispatchEvent(new FlexEvent(FlexEvent.URL_CHANGED));

        if (_url != null && loadRequested)
            loadModule();
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: Container
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function createComponentsFromDescriptors(
                                                recurse:Boolean = true):void
    {
        super.createComponentsFromDescriptors(recurse);

        loadRequested = true;
        loadModule();
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Loads the module. When the module is finished loading, the ModuleLoader adds
     *  it as a child with the <code>addChild()</code> method. This is normally 
     *  triggered with deferred instantiation.
     *  
     *  <p>If the module has already been loaded, this method does nothing. It does
     *  not load the module a second time.</p>
     */
    public function loadModule():void
    {
        if (url == null)
        {
            //trace("loadModule() - null url");
            return;
        }

        if (child)
        {
            //trace("loadModule() - already created the child");
            return;
        }

        if (module)
        {
            //trace("loadModule() - load already initiated");
            return;
        }

        dispatchEvent(new FlexEvent(FlexEvent.LOADING));

        module = ModuleManager.getModule(url);
        
        module.addEventListener(ModuleEvent.PROGRESS, moduleProgressHandler);
        module.addEventListener(ModuleEvent.SETUP, moduleSetupHandler);
        module.addEventListener(ModuleEvent.READY, moduleReadyHandler);
        module.addEventListener(ModuleEvent.ERROR, moduleErrorHandler);
        module.addEventListener(ModuleEvent.UNLOAD, moduleUnloadHandler);

        module.load(applicationDomain);
    }

    /**
     *  Unloads the module and sets it to <code>null</code>.
     *  If an instance of the module was previously added as a child,
     *  this method calls the <code>removeChild()</code> method on the child. 
     *  <p>If the module does not exist or has already been unloaded, this method does
     *  nothing.</p>
     */
    public function unloadModule():void
    {
        if (child)
        {
            removeChild(child);
            child = null;
        }

        if (module)
        {
            module.removeEventListener(ModuleEvent.PROGRESS,
                                       moduleProgressHandler);
            module.removeEventListener(ModuleEvent.SETUP, moduleSetupHandler);
            module.removeEventListener(ModuleEvent.READY, moduleReadyHandler);
            module.removeEventListener(ModuleEvent.ERROR, moduleErrorHandler);

            module.unload();
            module.removeEventListener(ModuleEvent.UNLOAD, moduleUnloadHandler);
            module = null;
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function moduleProgressHandler(event:ModuleEvent):void
    {
        dispatchEvent(event);
    }

    /**
     *  @private
     */
    private function moduleSetupHandler(event:ModuleEvent):void
    {
        // Not ready for creation yet, but can call factory.info().

        dispatchEvent(event);
    }

    /**
     *  @private
     */
    private function moduleReadyHandler(event:ModuleEvent):void
    {
        child = module.factory.create() as DisplayObject;
        dispatchEvent(event);

        if (child)
        {
            var p:DisplayObjectContainer = parent;
            // p.removeChild(this);
            addChild(child);
        }
    }

    /**
     *  @private
     */
    private function moduleErrorHandler(event:ModuleEvent):void
    {
        unloadModule();
        dispatchEvent(event);
    }

    /**
     *  @private
     */
    private function moduleUnloadHandler(event:ModuleEvent):void
    {
        dispatchEvent(event);
    }
}

}
