////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.resources
{

import flash.utils.describeType;
import flash.system.ApplicationDomain;
import mx.core.mx_internal;
import mx.managers.ISystemManager;
import mx.utils.StringUtil;

/**
 *  Provides an implementation of the IResourceBundle interface.
 *  The IResourceManager and IResourceBundle interfaces work together
 *  to provide internationalization support for Flex applications.
 *
 *  <p>A Flex application typically has multiple instances of this class,
 *  all managed by a single instance of the ResourceManager class.
 *  It is possible to have ResourceBundle instances for multiple locales,
 *  one for each locale. There can be multiple ResourceBundle instances with
 *  different bundle names.</p>
 *  
 *  @see mx.resources.IResourceBundle
 *  @see mx.resources.IResourceManager
 */
public class ResourceBundle implements IResourceBundle
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Set by SystemManager constructor in order to make the deprecated
     *  getResourceBundle() method work with the new resource scheme
     *  in the single-locale case.
     */
    mx_internal static var locale:String;

    /**
     *  @private
     *  Set by bootstrap loaders
     *  to allow for alternate search paths for resources
     */
    mx_internal static var backupApplicationDomain:ApplicationDomain;

    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    [Deprecated(replacement="ResourceManager.getInstance().getResourceBundle()", since="3.0")]
    
    /**
     *  If you compiled your application for a single locale,
     *  this method can return a ResourceBundle when provided
     *  with a resource bundle name,
     *
     *  <p>This method has been deprecated because the Flex framework
     *  now supports having resource bundles for multiple locales
     *  in the same application.
     *  You can use the <code>getResourceBundle()</code> method
     *  of IResourceManager to get a resource bundle if you know
     *  its bundle name and locale.
     *  However, you should no longer access resources
     *  directly from a ResourceBundle.
     *  All resources should now be accessed via methods
     *  of the IResourceManager interface such as <code>getString()</code>.
     *  All classes that extend UIComponent, Formatter, or Validator
     *  have a <code>resourceManager</code> property
     *  which provides a reference to an object implementing this interface.
     *  Other classes can call <code>ResourceManager.getInstance()</code>
     *  to obtain this object.</p>
     *  
     *  @param baseName The name of the resource bundle to return.
     *  
     *  @param currentDomain The ApplicationDomain that the resource bundle is in.
     * 
     *  @return The resource bundle that matches the specified name and domain.
     */
    public static function getResourceBundle(
                                baseName:String,
                                currentDomain:ApplicationDomain = null):
                                ResourceBundle
    {
        if (!currentDomain)
            currentDomain = ApplicationDomain.currentDomain;

        var className:String;
        var bundleClass:Class;

        className = mx_internal::locale + "$" + baseName + "_properties";
        bundleClass = getClassByName(className, currentDomain);

        if (!bundleClass)
        {
            className = baseName + "_properties";
            bundleClass = getClassByName(className, currentDomain);
        }

        if (!bundleClass)
        {
            className = baseName;
            bundleClass = getClassByName(className, currentDomain);
        }

        if (!bundleClass && mx_internal::backupApplicationDomain)
        {
            className = baseName + "_properties";
            bundleClass = getClassByName(className, mx_internal::backupApplicationDomain);

            if (!bundleClass)
            {
                className = baseName;
                bundleClass = getClassByName(className, mx_internal::backupApplicationDomain);
            }
        }

        if (bundleClass)
        {
            var bundleObj:Object = new bundleClass();
            if (bundleObj is ResourceBundle)
            {
                var bundle:ResourceBundle = ResourceBundle(bundleObj);
                return bundle;
            }
        }

        throw new Error("Could not find resource bundle " + baseName);
    }

    /**
     *  @private
     */
    private static function getClassByName(name:String,
                                           domain:ApplicationDomain):Class
    {
        var c:Class;

        if (domain.hasDefinition(name))
            c = domain.getDefinition(name) as Class;

        return c;
    }

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  @param locale A locale string, such as <code>"en_US"</code>.
     *
     *  @param bundleName A name that identifies this bundle,
     *  such as <code>"MyResources"</code>.
     */
    public function ResourceBundle(locale:String = null,
                                   bundleName:String = null)
    {
        // The only reason that the arguments are optional is so that
        // Flex 3 applications can link against Flex 2 resource SWCs.
        // In Flex 2, the constructor had no arguments at all
        // and the autogenerated ResourceBundle subclasses
        // therefore called super() with no arguments.
        // If, in Flex 3, the constructor has required arguments,
        // this causes a VerifyError.
        
        super();
        
        mx_internal::_locale = locale;
        mx_internal::_bundleName = bundleName;

        _content = getContent();
    }  

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------
    
    //----------------------------------
    //  bundleName
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the bundleName property.
     */
    mx_internal var _bundleName:String;

    /**
     *  @copy mx.resources.IResourceBundle#bundleName
     */     
    public function get bundleName():String
    {
        return mx_internal::_bundleName;
    }

    //----------------------------------
    //  content
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the content property.
     */
    private var _content:Object = {};

    /**
     *  @copy mx.resources.IResourceBundle#content
     */     
    public function get content():Object
    {
        return _content;
    }

    //----------------------------------
    //  locale
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the locale property.
     */
    mx_internal var _locale:String;

    /**
     *  @copy mx.resources.IResourceBundle#locale
     */     
    public function get locale():String
    {
        return mx_internal::_locale;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  When a properties file is compiled into a resource bundle,
     *  the MXML compiler autogenerates a subclass of ResourceBundle.
     *  The subclass overrides this method to return an Object
     *  that contains key-value pairs for the bundle's resources.
     *
     *  <p>If you create your own ResourceBundle instances,
     *  you can set the key-value pairs on the <code>content</code> object.</p>
     *  
     *  @return The Object that contains key-value pairs for the bundle's resources.
     */
    protected function getContent():Object
    {
        return {};
    }

    [Deprecated(replacement="ResourceManager.getInstance().getBoolean()", since="3.0")]
    
    /**
     *  Gets a Boolean from a ResourceBundle.
     *
     *  <p>If the resource specified by the <code>key</code> parameter
     *  does not exist in this bundle, this method throws an error.</p>
     *
     *  <p>This method has been deprecated because all resources
     *  should now be accessed via methods of the IResourceManager interface.
     *  You should convert your code to instead call
     *  the <code>getBoolean()</code>method of IResourceManager.
     *  All classes that extend UIComponent, Formatter, or Validator
     *  have a <code>resourceManager</code> property
     *  that provides a reference to an object implementing this interface.
     *  Other classes can call the <code>ResourceManager.getInstance()</code>
     *  method to obtain this object.</p>
     *
     *  @param key A String identifying a resource in this ResourceBundle.
     *
     *  @param defaultValue The value to return if the resource value,
     *  after being converted to lowercase, is neither the String
     *  <code>"true"</code> nor the String <code>"false"</code>.
     *  This parameter is optional; its default value is <code>true</code>.
     *
     *  @return The value of the specified resource, as a Boolean.
     */    
    public function getBoolean(key:String, defaultValue:Boolean = true):Boolean
    {
        var temp:String = _getObject(key).toLowerCase();
        
        if (temp == "false")
            return false;
        else if (temp == "true")
            return true;
        else
            return defaultValue;
    }

    [Deprecated(replacement="ResourceManager.getInstance().getNumber()", since="3.0")]
    
    /**
     *  Gets a Number from a ResourceBundle.
     *
     *  <p>If the resource specified by the <code>key</code> parameter
     *  does not exist in this bundle, this method throws an error.</p>
     *
     *  <p>This method has been deprecated because all resources
     *  should now be accessed via methods of the IResourceManager interface.
     *  You should convert your code to instead call
     *  the <code>getNumber()</code>, <code>getInt()</code>,
     *  or <code>getUint()</code> method of IResourceManager.
     *  All classes that extend UIComponent, Formatter, or Validator
     *  have a <code>resourceManager</code> property
     *  that provides a reference to an object implementing this interface.
     *  Other classes can call the <code>ResourceManager.getInstance()</code>
     *  method to obtain this object.</p>
     *
     *  @param key A String identifying a resource in this ResourceBundle.
     *
     *  @return The value of the specified resource, as a Number.
     */        
    public function getNumber(key:String):Number
    {
        return Number(_getObject(key));
    }

    [Deprecated(replacement="ResourceManager.getInstance().getString()", since="3.0")]
    
    /**
     *  Gets a String from a ResourceBundle.
     *
     *  <p>If the resource specified by the <code>key</code> parameter
     *  does not exist in this bundle, this method throws an error.</p>
     *
     *  <p>This method has been deprecated because all resources
     *  should now be accessed via methods of the IResourceManager interface.
     *  You should convert your code to instead call
     *  the <code>getString()</code> method of IResourceManager.
     *  All classes that extend UIComponent, Formatter, or Validator
     *  have a <code>resourceManager</code> property
     *  that provides a reference to an object implementing this interface.
     *  Other classes can call the <code>ResourceManager.getInstance()</code>
     *  method to obtain this object.</p>
     *
     *  @param key A String identifying a resource in this ResourceBundle.
     *
     *  @return The value of the specified resource, as a String.
     */        
    public function getString(key:String):String
    {
        return String(_getObject(key));
    }

    [Deprecated(replacement="ResourceManager.getInstance().getStringArray()", since="3.0")]
    
    /**
     *  Gets an Array of Strings from a ResourceBundle.
     *
     *  <p>The Array is produced by assuming that the actual value
     *  of the resource is a String containing comma-separated items,
     *  such as <code>"India, China, Japan"</code>.
     *  After splitting the String at the commas, any white space
     *  before or after each item is trimmed.</p>
     *
     *  <p>If the resource specified by the <code>key</code> parameter
     *  does not exist in this bundle, this method throws an error.</p>
     *
     *  <p>This method has been deprecated because all resources
     *  should now be accessed via methods of the IResourceManager interface.
     *  You should convert your code to instead call
     *  the <code>getStringArray()</code> method of IResourceManager.
     *  All classes that extend UIComponent, Formatter, or Validator
     *  have a <code>resourceManager</code> property
     *  which provides a reference to an object implementing this interface.
     *  Other classes can call <code>ResourceManager.getInstance()</code>
     *  to obtain this object.</p>
     *
     *  @param key A String identifying a resource in this ResourceBundle.
     *
     *  @return The value of the specified resource,
     *  as an Array of Strings.
     */
    public function getStringArray(key:String):Array
    {
        var array:Array = _getObject(key).split(",");
        
        var n:int = array.length;
        for (var i:int = 0; i < n; i++)
        {
             array[i] = StringUtil.trim(array[i]);
        }  
        
        return array;
    }

    [Deprecated(replacement="ResourceManager.getInstance().getObject()", since="3.0")]
    
    /**
     *  Gets an Object from a ResourceBundle.
     *
     *  <p>If the resource specified by the <code>key</code> parameter
     *  does not exist in this bundle, this method throws an error.</p>
     *
     *  <p>This method has been deprecated because all resources
     *  should now be accessed via methods of the IResourceManager interface.
     *  You should convert your code to instead call
     *  the <code>getObject()</code> or <code>getClass()</code> method
     *  of IResourceManager.
     *  All classes that extend UIComponent, Formatter, or Validator
     *  have a <code>resourceManager</code> property
     *  that provides a reference to an object implementing this interface.
     *  Other classes can call the <code>ResourceManager.getInstance()</code>
     *  method to obtain this object.</p>
     *
     *  @param key A String identifying a resource in this ResourceBundle.
     *
     *  @return An Object that is the value of the specified resource.
     */        
    public function getObject(key:String):Object
    {
        return _getObject(key);
    }

    /**
     *  @private
     */
    private function _getObject(key:String):Object
    {
        var value:Object = content[key];
        if (!value)
        {
            throw new Error("Key " + key +
                            " was not found in resource bundle " + bundleName);
        }
        return value;
    }
}

}
