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

package mx.resources
{

/**
 *  The IResourceBundle and IResourceManager interfaces work together
 *  to provide localization support for Flex applications.
 *
 *  <p>There are three main concepts involved in localization:
 *  locales, resources, and resource bundles.</p>
 *
 *  <p>A locale specifies a language and a country
 *  for which your application has been localized.
 *  For example, the locale <code>"en_US"</code>
 *  specifies English as spoken in the United States.
 *  (See the mx.resources.Locale class for more information.)</p>
 *
 *  <p>A resource is a named value that is locale-dependent.
 *  For example, your application might have a resource
 *  whose name is <code>"OPEN"</code>
 *  and whose value for an English locale is <code>"Open"</code>
 *  but whose value for a French locale is <code>"Ouvrir"</code>.</p>
 *
 *  <p>A resource bundle is a named group of resources
 *  whose values have been localized for a particular locale.
 *  A resource bundle is identified by the combination of its
 *  <code>bundleName</code> and its <code>locale</code>,
 *  and has a <code>content</code> Object that contains
 *  the name-value pairs for the bundle's resources.</p>
 *
 *  <p>The IResourceBundle interface represents a specific resource bundle.
 *  However, most applications will only need to use IResourceManager.
 *  A single ResourceManager object implementing this interface
 *  manages multiple resource bundles, possibly for multiple locales,
 *  and provides access to the resources that they contain.
 *  For example, you can retrieve a specific resource as a String by calling
 *  <code>resourceManager.getString(bundleName, resourceName)</code>.
 *  By changing the <code>localeChain</code> property of the ResourceManager,
 *  you can change which resource bundles are searched for resource values.</p>
 *
 *  <p>Generally, you do not create resource bundles yourself;
 *  instead, they are usually compiled from ~~.properties files.
 *  A properties file named MyResources.properties
 *  produces a resource bundle with <code>"MyResources"</code>
 *  for its <code>bundleName</code>.
 *  You generally produce multiple versions of each properties file,
 *  one for each locale that your application supports.</p>
 *
 *  <p>Flex properties files are similar to Java properties files,
 *  except that they also support MXML's <code>Embed()</code>
 *  and <code>ClassReference()</code> directives.
 *  These directives work the same way in a properties file
 *  as they do in a CSS file, producing class references.
 *  Also, the encoding for Flex properties files
 *  is always assumed to be UTF-8.</p>
 *
 *  <p>The Flex framework's resources have been localized
 *  for U.S. English (the <code>"en_US"</code> locale) and
 *  for Japanese (the <code>"ja_JP"</code> locale). 
 *  The framework resources are organized into multiple bundles
 *  corresponding to framework packages; for example, the "formatters"
 *  bundle is used by classes in the mx.formatters package.
 *  (There is also a "SharedResources" bundle for resources used by
 *  multiple packages.)</p>
 *
 *  <p>The properties files for the framework resources,
 *  such as formatters.properties, can be found in the
 *  frameworks/projects/framework/bundles/{locale}/src directories
 *  of the Flex SDK.
 *  Your applications normally link against the Flex framework
 *  as a precompiled library, framework.swc,
 *  in the frameworks/libs directory.
 *  This library has no resources in it.
 *  Instead, the framework resources have been compiled into separate
 *  resource bundle libraries such as framework_rb.swc.
 *  These are located in the frameworks/locales/{locale} directories
 *  and your application must also link in one or more of these.</p>
 *
 *  <p>You are free to organize your application's own resources
 *  into whatever bundles you find convenient.
 *  If you localize your application for locales
 *  other than <code>"en_US"</code> and <code>"ja_JP"</code>,
 *  you should localize the framework's properties files for those locales
 *  as well and compile additional resource bundle libaries for them.</p>
 *
 *  <p>When your application starts, the ResourceManager is automatically
 *  populated with whatever resource bundles were compiled
 *  into the application.
 *  If you create a code module, by default the resources that its classes
 *  need are compiled into the module.
 *  When the module is loaded into an application, any bundles that the
 *  application does not already have are added to the ResourceManager.</p>
 *
 *  <p>You can compile "resource modules" that have only resources in them,
 *  and load them with the <code>loadResourceModule()</code> method
 *  of the ResourceManager.
 *  With resource modules, you can support multiple locales by loading
 *  the resources you need at run time rather than compiling them into
 *  your application.</p>
 *
 *  <p>Although the ResourceManager is normally populated with resource bundles
 *  that were compiled into your application or loaded from modules,
 *  you can also programmatically create resource bundles and add them
 *  to the ResourceManager yourself with the <code>addResourceBundle()</code>
 *  method.</p>
 *
 *  @see mx.resources.ResourceBundle
 *  @see mx.resources.IResourceManager
 *  @see mx.resources.ResourceManager
 */
public interface IResourceBundle
{
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------
    
    //----------------------------------
    //  bundleName
    //----------------------------------
    
    /**
     *  A name that identifies this resource bundle,
     *  such as <code>"MyResources"</code>.
     *
     *  <p>This read-only property is set
     *  when a resource bundle is constructed.</p>
     *
     *  <p>Resource bundles that are automatically created from compiled
     *  properties files have bundle names based on the names of those files.
     *  For example, a properties file named MyResources.properties
     *  will produce a resource bundle whose <code>bundleName</code>
     *  is <code>"MyResources"</code>.</p>
     *
     *  <p>The ResourceManager can manage multiple bundles with the same
     *  <code>bundleName</code> as long as they have different values
     *  for their <code>locale</code> property.</p>
     */     
    function get bundleName():String;

    //----------------------------------
    //  content
    //----------------------------------
    
    /**
     *  An object containing key-value pairs for the resources
     *  in this resource bundle.
     *
     *  <p>In general, you should access resources by using IResourceManager
     *  methods such as <code>getString()</code>, rather than directly
     *  accessing them in a resource bundle.
     *  However, if you are programmatically creating your own
     *  resource bundles, you can initialize them with resources,
     *  as follows:</p>
     *
     *  <pre>
     *  var rb:IResourceBundle = new ResourceBundle("fr_FR", "MyResources");
     *  rb.content["LANGUAGE"] = "Francais";
     *  rb.content["GREETING"] = "Bonjour";
     *  </pre>
     *
     *  <p>When a resource bundle is produced by compiling a properties
     *  file, its resource values are either of type String or Class.
     *  For example, if the properties file contains</p>
     *
     *  <pre>
     *  LANGUAGE=English
     *  MINIMUM_AGE=18
     *  ENABLED=true
     *  LOGO=Embed("logo.png")
     *  </pre>
     *
     *  <p>then the value of the <code>LANGUAGE</code> resource
     *  is the String <code>"English"</code>,
     *  the value of the <code>MINIMUM_AGE</code> resource
     *  is the String <code>"18"</code>,
     *  the value of the <code>ENABLED</code> resource
     *  is the String <code>"true"</code>,
     *  and the value of the <code>LOGO</code> resource
     *  is a Class that represents the embedded PNG file.</p>
     *
     *  <p>You can use IResourceManager methods such as <code>getInt()</code>
     *  and <code>getBoolean()</code> to convert resource strings like
     *  <code>"18"</code> and <code>"true"</code> into the type
     *  that your code expects.</p>
     */     
    function get content():Object;

    //----------------------------------
    //  locale
    //----------------------------------
    
    /**
     *  The locale for which this bundle's resources have been localized.
     *  This is a String such as <code>"en_US"</code> for U.S. English.
     *
     *  <p>This read-only property is set
     *  when a resource bundle is constructed.</p>
     *
     *  <p>Resource bundles that are automatically created from compiled
     *  properties files have locales based on the
     *  <code>-compiler.locale</code> option of the mxmlc or compc compilers.
     *  For example, suppose that you compile your application with the option
     *  <code>-compiler.locale=en_US,ja_JP</code> and that you have specified
     *  <code>-compiler.source-path=resources/{locale}</code> so that
     *  your application's resources, located in
     *  resources/en_US/MyResources.properties and
     *  resources/ja_JP/MyResources.properties, are found.
     *  Then your application will have two resource bundles
     *  whose <code>bundleName</code> is <code>"MyResources"</code>,
     *  one whose <code>locale</code> is <code>"en_US"</code> 
     *  and one whose <code>locale</code> is <code>"ja_JP"</code>.</p>
     */     
    function get locale():String;
}

}
