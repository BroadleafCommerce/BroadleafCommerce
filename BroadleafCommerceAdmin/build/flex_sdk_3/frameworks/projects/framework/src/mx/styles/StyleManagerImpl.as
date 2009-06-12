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

package mx.styles
{

import flash.events.IEventDispatcher;
import flash.events.TimerEvent;
import flash.system.ApplicationDomain;
import flash.system.SecurityDomain;
import flash.utils.Timer
import flash.utils.describeType;
import mx.core.FlexVersion;
import mx.core.mx_internal;
import mx.events.ModuleEvent;
import mx.events.StyleEvent;
import mx.managers.SystemManager;
import mx.managers.SystemManagerGlobals;
import mx.modules.IModuleInfo;
import mx.modules.ModuleManager
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.styles.IStyleModule;

use namespace mx_internal;

[ExcludeClass]

[ResourceBundle("styles")]
    
/**
 *  @private
 */
public class StyleManagerImpl implements IStyleManager2
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Set of inheriting non-color styles.
     *  This is not the complete set from CSS.
     *  Some of the omitted we don't support at all,
     *  others may be added later as needed.
     *  The <code>isInheritingTextFormatStyle()</code> method queries this set.
     */
    private static var inheritingTextFormatStyles:Object =
    {
        align: true,
        bold: true,
        color: true,
        font: true,
        indent: true,
        italic: true,
        size: true
    };

    /**
     *  @private
     *  Set of styles for which setStyle() causes
     *  invalidateSize() to be called on the component.
     *  The method registerSizeInvalidatingStyle() adds to this set
     *  and isSizeInvalidatingStyle() queries this set.
     */
    private static var sizeInvalidatingStyles:Object =
    {
        borderStyle: true,
        borderThickness: true,
        fontAntiAliasType: true,
        fontFamily: true,
        fontGridFitType: true,
        fontSharpness: true,
        fontSize: true,
        fontStyle: true,
        fontThickness: true,
        fontWeight: true,
        headerHeight: true,
        horizontalAlign: true,
        horizontalGap: true,
        kerning: true,
        leading: true,
        letterSpacing: true,
        paddingBottom: true,
        paddingLeft: true,
        paddingRight: true,
        paddingTop: true,
        strokeWidth: true,
        tabHeight: true,
        tabWidth: true,
        verticalAlign: true,
        verticalGap: true
    }

    /**
     *  @private
     *  Set of styles for which setStyle() causes
     *  invalidateSize() to be called on the component's parent.
     *  The method registerParentSizeInvalidatingStyle() adds to this set
     *  and isParentSizeInvalidatingStyle() queries this set.
     */
    private static var parentSizeInvalidatingStyles:Object =
    {
        bottom: true,
        horizontalCenter: true,
        left: true,
        right: true,
        top: true,
        verticalCenter: true,
        baseline: true
    }

    /**
     *  @private
     *  Set of styles for which setStyle() causes
     *  invalidateDisplayList() to be called on the component's parent.
     *  The method registerParentDisplayListInvalidatingStyle() adds to this set
     *  and isParentDisplayListInvalidatingStyle() queries this set.
     */
    private static var parentDisplayListInvalidatingStyles:Object =
    {
        bottom: true,
        horizontalCenter: true,
        left: true,
        right: true,
        top: true,
        verticalCenter: true,
        baseline: true
    }

    /**
     *  @private
     *  Set of color names.
     *  The method registerColorName() adds to this set
     *  and isColorName() queries this set.
     *  All color names in this set are lowercase in order to support
     *  case-insensitive mapping in the StyleManager methods getColorName(),
     *  getColorNames(), registerColorName(), and isColorName().
     *  We handle color names at runtime in a case-insensitive way
     *  because the MXML compiler does this at compile time,
     *  in conformance with the CSS spec.
     */
    private static var colorNames:Object =
    {
        transparent: "transparent",
        black: 0x000000,
        blue: 0x0000FF,
        green: 0x008000,
        gray: 0x808080,
        silver: 0xC0C0C0,
        lime: 0x00FF00,
        olive: 0x808000,
        white: 0xFFFFFF,
        yellow: 0xFFFF00,
        maroon: 0x800000,
        navy: 0x000080,
        red: 0xFF0000,
        purple: 0x800080,
        teal: 0x008080,
        fuchsia: 0xFF00FF,
        aqua: 0x00FFFF,
        magenta: 0xFF00FF,
        cyan: 0x00FFFF,

        // IMPORTANT: Theme colors must also be updated
        // in the Flex compiler's CSS parser
        // (in \src\java\macromedia\css\Descriptor.java)
        // and possibly other places as well. Grep for them!
        halogreen: 0x80FF4D,
        haloblue: 0x009DFF,
        haloorange: 0xFFB600,
        halosilver: 0xAECAD9
    };

    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private static var instance:IStyleManager2;

    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public static function getInstance():IStyleManager2
    {
        if (!instance)
            instance = new StyleManagerImpl();

        return instance;
    }

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public function StyleManagerImpl()
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
     *  A map of CSS selectors -- such as "global", "Button", and ".bigRed" --
     *  to CSSStyleDeclarations.
     *  This collection is accessed via getStyleDeclaration(),
     *  setStyleDeclaration(), and clearStyleDeclaration().
     */
    private var _selectors:Object = {};

    /**
     *  @private
     */
    private var styleModules:Object = {};

	/**
	 *  @private
	 *  Used for accessing localized Error messages.
	 */
	private var resourceManager:IResourceManager =
									ResourceManager.getInstance();

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  stylesRoot
    //----------------------------------

    /**
     *  @private
     */
    private var _stylesRoot:Object;

    /**
     *  @private
     *  The root of all proto chains used for looking up styles.
     *  This object is initialized once by initProtoChainRoots() and
     *  then updated by calls to setStyle() on the global CSSStyleDeclaration.
     *  It is accessed by code that needs to construct proto chains,
     *  such as the initProtoChain() method of UIComponent.
     */
    public function get stylesRoot():Object
    {
        return _stylesRoot;
    }
    public function set stylesRoot(value:Object):void
    {
        _stylesRoot = value;
    }

    //----------------------------------
    //  inheritingStyles
    //----------------------------------

    /**
     *  @private
     */
    private var _inheritingStyles:Object = {};

    /**
     *  @private
     *  Set of inheriting non-color styles.
     *  This is not the complete set from CSS.
     *  Some of the omitted we don't support at all,
     *  others may be added later as needed.
     *  The method registerInheritingStyle() adds to this set
     *  and isInheritingStyle() queries this set.
     */
    public function get inheritingStyles():Object
    {
        return _inheritingStyles;
    }
    public function set inheritingStyles(value:Object):void
    {
        _inheritingStyles = value;
    }

    //----------------------------------
    //  typeSelectorCache
    //----------------------------------

    /**
     *  @private
     */
    private var _typeSelectorCache:Object = {};

    /**
     *  @private
     */
    public function get typeSelectorCache():Object
    {
        return _typeSelectorCache;
    }
    public function set typeSelectorCache(value:Object):void
    {
        _typeSelectorCache = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  This method is called by code autogenerated by the MXML compiler,
     *  after StyleManager.styles is popuplated with CSSStyleDeclarations.
     */
    public function initProtoChainRoots():void
    {
		// Prior to version 3, textDecoration and leading were not inheriting styles
		if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
		{
			delete _inheritingStyles["textDecoration"];
			delete _inheritingStyles["leading"];
		}
		
		if (!stylesRoot)
			stylesRoot = _selectors["global"].addStyleToProtoChain({}, null);
    }
    
    /**
     *  @review
     *  Returns an array of strings of all CSS selectors registered with the StyleManager.
     *  Pass items in this array to the getStyleDeclaration function to get the corresponding CSSStyleDeclaration.
     *  Note that class selectors are prepended with a period.
     *  
     *  @return An array of all of the selectors
     */ 
    public function get selectors():Array
    {
    	var theSelectors:Array = [];
    	for (var i:String in _selectors)
    		theSelectors.push(i);
    		
    	return theSelectors;
    }

    /**
     *  Gets the CSSStyleDeclaration object that stores the rules
     *  for the specified CSS selector.
     *
     *  <p>If the <code>selector</code> parameter starts with a period (.),
     *  the returned CSSStyleDeclaration is a class selector and applies only to those instances
     *  whose <code>styleName</code> property specifies that selector
     *  (not including the period).
     *  For example, the class selector <code>".bigMargins"</code>
     *  applies to any UIComponent whose <code>styleName</code>
     *  is <code>"bigMargins"</code>.</p>
     *
     *  <p>If the <code>selector</code> parameter does not start with a period,
     *  the returned CSSStyleDeclaration is a type selector and applies to all instances
     *  of that type.
     *  For example, the type selector <code>"Button"</code>
     *  applies to all instances of Button and its subclasses.</p>
     *
     *  <p>The <code>global</code> selector is similar to a type selector
     *  and does not start with a period.</p>
     *
     *  @param selector The name of the CSS selector.
     *
     *  @return The style declaration whose name matches the <code>selector</code> property.
     */
    public function getStyleDeclaration(
                                selector:String):CSSStyleDeclaration
    {
        // If we were passed a type selector with a package name,
        // such as "mx.controls.Button", strip off the package name
        // leaving just "Button" and look for that selector.
        if (selector.charAt(0) != ".")
        {
            var index:int = selector.lastIndexOf(".");
            if (index != -1)
                selector = selector.substr(index + 1);
        }

        return _selectors[selector];
    }

    /**
     *  Sets the CSSStyleDeclaration object that stores the rules
     *  for the specified CSS selector.
     *
     *  <p>If the <code>selector</code> parameter starts with a period (.),
     *  the specified selector is a class selector and applies only to those instances
     *  whose <code>styleName</code> property specifies that selector
     *  (not including the period).
     *  For example, the class selector <code>".bigMargins"</code>
     *  applies to any UIComponent whose <code>styleName</code>
     *  is <code>"bigMargins"</code>.</p>
     *
     *  <p>If the <code>selector</code> parameter does not start with a period,
     *  the specified selector is a "type selector" and applies to all instances
     *  of that type.
     *  For example, the type selector <code>"Button"</code>
     *  applies to all instances of Button and its subclasses.</p>
     *
     *  <p>The <code>global</code> selector is similar to a type selector
     *  and does not start with a period.</p>
     *
     *  @param selector The name of the CSS selector.
     *  @param styleDeclaration The new style declaration.
     *  @param update Set to <code>true</code> to force an immediate update of the styles.
     *  Set to <code>false</code> to avoid an immediate update of the styles in the application.
     *  The styles will be updated the next time this method or the <code>clearStyleDeclaration()</code> method
     *  is called with the <code>update</code> property set to <code>true</code>.
     */
    public function setStyleDeclaration(
                                selector:String,
                                styleDeclaration:CSSStyleDeclaration,
                                update:Boolean):void
    {
        styleDeclaration.selectorRefCount++;

        _selectors[selector] = styleDeclaration;

        // Flush cache and start over.
        typeSelectorCache = {};

        if (update)
            styleDeclarationsChanged();
    }

    /**
     *  Clears the CSSStyleDeclaration object that stores the rules
     *  for the specified CSS selector.
     *
     *  <p>If the specified selector is a class selector (for example, ".bigMargins" or ".myStyle"),
     *  you must be sure to start the
     *  <code>selector</code> property with a period (.).</p>
     *
     *  <p>If the specified selector is a type selector (for example, "Button"), do not start the
     *  <code>selector</code> property with a period.</p>
     *
     *  <p>The <code>global</code> selector is similar to a type selector
     *  and does not start with a period.</p>
     *
     *  @param selector The name of the CSS selector to clear.
     *  @param update Set to <code>true</code> to force an immediate update of the styles.
     *  Set to <code>false</code> to avoid an immediate update of the styles in the application.
     *  The styles will be updated the next time this method or the <code>setStyleDeclaration()</code> method is
     *  called with the <code>update</code> property set to <code>true</code>.
     */
    public function clearStyleDeclaration(selector:String,
                                                 update:Boolean):void
    {
        var styleDeclaration:CSSStyleDeclaration =
            getStyleDeclaration(selector);

        if (styleDeclaration && styleDeclaration.selectorRefCount > 0)
            styleDeclaration.selectorRefCount--;

        delete _selectors[selector];

        if (update)
            styleDeclarationsChanged();
    }

    /**
     *  @private
     *  After an entire selector is added, replaced, or removed,
     *  this method updates all the DisplayList trees.
     */
    public function styleDeclarationsChanged():void
    {
        var sms:Array /* of SystemManager */ =
            SystemManagerGlobals.topLevelSystemManagers;
        var n:int = sms.length;
        for (var i:int = 0; i < n; i++)
        {
        	// Type as Object to avoid dependency on SystemManager or WindowedSystemManager
            var sm:Object = sms[i];
            sm.regenerateStyleCache(true);
            sm.notifyStyleChangeInChildren(null, true);
        }
    }

    /**
     *  Adds to the list of styles that can inherit values
     *  from their parents.
     *
     *  <p><b>Note:</b> Ensure that you avoid using duplicate style names, as name
     *  collisions can result in decreased performance if a style that is
     *  already used becomes inheriting.</p>
     *
     *  @param styleName The name of the style that is added to the list of styles that can inherit values.
     */
    public function registerInheritingStyle(styleName:String):void
    {
        inheritingStyles[styleName] = true;
    }

    /**
     *  Tests to see if a style is inheriting.
     *
     *  @param styleName The name of the style that you test to see if it is inheriting.
     *
     *  @return Returns <code>true</code> if the specified style is inheriting.
     */
    public function isInheritingStyle(styleName:String):Boolean
    {
        return inheritingStyles[styleName] == true;
    }

    /**
     *  Test to see if a TextFormat style is inheriting.
     *
     *  @param styleName The name of the style that you test to see if it is inheriting.
     *
     *  @return Returns <code>true</code> if the specified TextFormat style
     *  is inheriting.
     */
    public function isInheritingTextFormatStyle(styleName:String):Boolean
    {
        return inheritingTextFormatStyles[styleName] == true;
    }

    /**
     *  Adds to the list of styles which may affect the measured size
     *  of the component.
     *  When one of these styles is set with <code>setStyle()</code>,
     *  the <code>invalidateSize()</code> method is automatically called on the component
     *  to make its measured size get recalculated later.
     *
     *  @param styleName The name of the style that you add to the list.
     */
    public function registerSizeInvalidatingStyle(styleName:String):void
    {
        sizeInvalidatingStyles[styleName] = true;
    }

    /**
     *  Tests to see if a style changes the size of a component.
     *
     *  <p>When one of these styles is set with the <code>setStyle()</code> method,
     *  the <code>invalidateSize()</code> method is automatically called on the component
     *  to make its measured size get recalculated later.</p>
     *
     *  @param styleName The name of the style to test.
     *
     *  @return Returns <code>true</code> if the specified style is one
     *  which may affect the measured size of the component.
     */
    public function isSizeInvalidatingStyle(styleName:String):Boolean
    {
        return sizeInvalidatingStyles[styleName] == true;
    }

    /**
     *  Adds to the list of styles which may affect the measured size
     *  of the component's parent container.
     *  <p>When one of these styles is set with <code>setStyle()</code>,
     *  the <code>invalidateSize()</code> method is automatically called on the component's
     *  parent container to make its measured size get recalculated
     *  later.</p>
     *
     *  @param styleName The name of the style to register.
     */
    public function registerParentSizeInvalidatingStyle(styleName:String):void
    {
        parentSizeInvalidatingStyles[styleName] = true;
    }

    /**
     *  Tests to see if the style changes the size of the component's parent container.
     *
     *  <p>When one of these styles is set with <code>setStyle()</code>,
     *  the <code>invalidateSize()</code> method is automatically called on the component's
     *  parent container to make its measured size get recalculated
     *  later.</p>
     *
     *  @param styleName The name of the style to test.
     *
     *  @return Returns <code>true</code> if the specified style is one
     *  which may affect the measured size of the component's
     *  parent container.
     */
    public function isParentSizeInvalidatingStyle(styleName:String):Boolean
    {
        return parentSizeInvalidatingStyles[styleName] == true;
    }

    /**
     *  Adds to the list of styles which may affect the appearance
     *  or layout of the component's parent container.
     *  When one of these styles is set with <code>setStyle()</code>,
     *  the <code>invalidateDisplayList()</code> method is auomatically called on the component's
     *  parent container to make it redraw and/or relayout its children.
     *
     *  @param styleName The name of the style to register.
     */
    public function registerParentDisplayListInvalidatingStyle(
                                styleName:String):void
    {
        parentDisplayListInvalidatingStyles[styleName] = true;
    }

    /**
     *  Tests to see if this style affects the component's parent container in
     *  such a way as to require that the parent container redraws itself when this style changes.
     *
     *  <p>When one of these styles is set with <code>setStyle()</code>,
     *  the <code>invalidateDisplayList()</code> method is auomatically called on the component's
     *  parent container to make it redraw and/or relayout its children.</p>
     *
     *  @param styleName The name of the style to test.
     *
     *  @return Returns <code>true</code> if the specified style is one
     *  which may affect the appearance or layout of the component's
     *  parent container.
     */
    public function isParentDisplayListInvalidatingStyle(
                                styleName:String):Boolean
    {
        return parentDisplayListInvalidatingStyles[styleName] == true;
    }

    /**
     *  Adds a color name to the list of aliases for colors.
     *
     *  @param colorName The name of the color to add to the list; for example, "blue".
     *  If you later access this color name, the value is not case-sensitive.
     *
     *  @param colorValue Color value, for example, 0x0000FF.
     */
    public function registerColorName(colorName:String, colorValue:uint):void
    {
        colorNames[colorName.toLowerCase()] = colorValue;
    }

    /**
     *  Tests to see if the given String is an alias for a color value. For example,
     *  by default, the String "blue" is an alias for 0x0000FF.
     *
     *  @param colorName The color name to test. This parameter is not case-sensitive.
     *
     *  @return Returns <code>true</code> if <code>colorName</code> is an alias
     *  for a color.
     */
    public function isColorName(colorName:String):Boolean
    {
        return colorNames[colorName.toLowerCase()] !== undefined;
    }

    /**
     *  Returns the numeric RGB color value that corresponds to the
     *  specified color string.
     *  The color string can be either a case-insensitive color name
     *  such as <code>"red"</code>, <code>"Blue"</code>, or
     *  <code>"haloGreen"</code>, a hexadecimal value such as 0xFF0000, or a #-hexadecimal String
     *  such as <code>"#FF0000"</code>.
     *
     *  <p>This method returns a uint, such as 4521830, that represents a color. You can convert
     *  this uint to a hexadecimal value by passing the numeric base (in this case, 16), to
     *  the uint class's <code>toString()</code> method, as the following example shows:</p>
     *  <pre>
     *  import mx.styles.StyleManager;
     *  private function getNewColorName():void {
     *      StyleManager.registerColorName("soylentGreen",0x44FF66);
     *      trace(StyleManager.getColorName("soylentGreen").toString(16));
     *  }
     *  </pre>
     *
     *  @param colorName The color name.
     *
     *  @return Returns a uint that represents the color value or <code>NOT_A_COLOR</code>
     *  if the value of the <code>colorName</code> property is not an alias for a color.
     */
    public function getColorName(colorName:Object):uint
    {
        var n:Number;

        if (colorName is String)
        {
            if (colorName.charAt(0) == "#")
            {
                // Map "#77EE11" to 0x77EE11
                n = Number("0x" + colorName.slice(1));
                return isNaN(n) ? StyleManager.NOT_A_COLOR : uint(n);
            }

            if (colorName.charAt(1) == "x" && colorName.charAt(0) == '0')
            {
                // Map "#77EE11" to 0x77EE11
                 n = Number(colorName);
                return isNaN(n) ? StyleManager.NOT_A_COLOR : uint(n);
            }

            // Map "red" or "Red" to 0xFF0000;
            // Map "haloGreen" or "HaLoGrEeN" to 0x46FF00.
            var c:* = colorNames[colorName.toLowerCase()];
            if (c === undefined)
                return StyleManager.NOT_A_COLOR;

            return uint(c);
        }

        return uint(colorName);
    }

    /**
     *  Converts each element of the colors Array from a color name
     *  to a numeric RGB color value.
     *  Each color String can be either a case-insensitive color name
     *  such as <code>"red"</code>, <code>"Blue"</code>, or
     *  <code>"haloGreen"</code>, a hexadecimal value such as 0xFF0000, or a #-hexadecimal String
     *  such as <code>"#FF0000"</code>..
     *
     *  @param colors An Array of color names.
     */
    public function getColorNames(colors:Array /* of Number or String */):void
    {
        if (!colors)
            return;

        var n:int = colors.length;
        for (var i:int = 0; i < n; i++)
        {
            if ((colors[i] != null) && isNaN(colors[i]))
            {
                var colorNumber:uint = getColorName(colors[i]);
                if (colorNumber != StyleManager.NOT_A_COLOR)
                    colors[i] = colorNumber;
            }
        }
    }

    /**
     *  Determines if a specified parameter is a valid style property. For example:
     *
     *  <pre>
     *  trace(StyleManager.isValidStyleValue(myButton.getStyle("color")).toString());
     *  </pre>
     *
     *  <p>This can be useful because some styles can be set to values
     *  such as 0, <code>NaN</code>,
     *  the empty String (<code>""</code>), or <code>null</code>, which can
     *  cause an <code>if (value)</code> test to fail.</p>
     *
     *  @param value The style property to test.
     *
     *  @return If you pass the value returned by a <code>getStyle()</code> method call
     *  to this method, it returns <code>true</code> if the style
     *  was set and <code>false</code> if it was not set.
     *
     */
    public function isValidStyleValue(value:*):Boolean
    {
        // By convention, we don't allow style values to be undefined,
        // so we can check for this as the "not set" value.
        return value !== undefined;
    }

    /**
	 *  @private
	 */
	public function loadStyleDeclarations(
						url:String, update:Boolean = true,
                        trustContent:Boolean = false):
						IEventDispatcher
	{
		return loadStyleDeclarations2(url, update);
	}

    /**
     *  Loads a style SWF.
     *
     *  @param url Location of the style SWF.
     *
     *  @param update If true, all the DisplayList trees will be updated.
     *         The default is true.
     *
     *  @return An IEventDispatcher implementation that supports
     *          StyleEvent.PROGRESS, StyleEvent.COMPLETE, and
     *          StyleEvent.ERROR.
     *
     *  @see flash.system.SecurityDomain
     */
    public function loadStyleDeclarations2(
						url:String, update:Boolean = true,
						applicationDomain:ApplicationDomain = null,
						securityDomain:SecurityDomain = null):
						IEventDispatcher
    {
        var module:IModuleInfo = ModuleManager.getModule(url);

        var readyHandler:Function = function(moduleEvent:ModuleEvent):void
        {
            var styleModule:IStyleModule =
                IStyleModule(moduleEvent.module.factory.create());
            
            styleModules[moduleEvent.module.url].styleModule = styleModule;
            
            if (update)
                styleDeclarationsChanged();
        };

        module.addEventListener(ModuleEvent.READY, readyHandler,
                                false, 0, true);

        var styleEventDispatcher:StyleEventDispatcher =
                                        new StyleEventDispatcher(module);

        var errorHandler:Function = function(moduleEvent:ModuleEvent):void
        {
			var errorText:String = resourceManager.getString(
				"styles", "unableToLoad", [ moduleEvent.errorText, url ]);
			
            if (styleEventDispatcher.willTrigger(StyleEvent.ERROR))
            {
                var styleEvent:StyleEvent = new StyleEvent(
                    StyleEvent.ERROR, moduleEvent.bubbles, moduleEvent.cancelable);
                styleEvent.bytesLoaded = 0;
                styleEvent.bytesTotal = 0;
                styleEvent.errorText = errorText;
                styleEventDispatcher.dispatchEvent(styleEvent);
            }
            else
            {
                throw new Error(errorText);
            }
        };

        module.addEventListener(ModuleEvent.ERROR, errorHandler,
                                false, 0, true);

        styleModules[url] =
            new StyleModuleInfo(module, readyHandler, errorHandler);

        // This Timer gives the loadStyleDeclarations() caller a chance
        // to add event listeners to the return value, before the module
        // is loaded.
        var timer:Timer = new Timer(0);
        var timerHandler:Function = function(event:TimerEvent):void
        {
            timer.removeEventListener(TimerEvent.TIMER, timerHandler);
            timer.stop();
            module.load(applicationDomain, securityDomain);
        };

        timer.addEventListener(TimerEvent.TIMER, timerHandler, false, 0, true);

        timer.start();

        return styleEventDispatcher;
    }

    /**
     *  Unloads a style SWF.
     *
     *  @param url Location of the style SWF.
     *
     *  @param update If true, all the DisplayList trees will be updated.
     *         The default is true.
     */
    public function unloadStyleDeclarations(
                                url:String, update:Boolean = true):void
    {
        var styleModuleInfo:StyleModuleInfo = styleModules[url];
        if (styleModuleInfo)
        {
            styleModuleInfo.styleModule.unload();

            var module:IModuleInfo = styleModuleInfo.module;
            module.unload();
            
            module.removeEventListener(ModuleEvent.READY,
                                       styleModuleInfo.readyHandler);
            module.removeEventListener(ModuleEvent.ERROR,
                                       styleModuleInfo.errorHandler);

            styleModules[url] = null;
        }

        if (update)
            styleDeclarationsChanged();
    }
}

}

import flash.events.EventDispatcher;
import mx.events.ModuleEvent;
import mx.events.StyleEvent;
import mx.modules.IModuleInfo;
import mx.styles.IStyleModule;

////////////////////////////////////////////////////////////////////////////////
//
//  Helper class: StyleEventDispatcher
//
////////////////////////////////////////////////////////////////////////////////

/**
 *  @private
 */
class StyleEventDispatcher extends EventDispatcher
{
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor
     */
    public function StyleEventDispatcher(moduleInfo:IModuleInfo)
    {
        super();

        moduleInfo.addEventListener(
            ModuleEvent.ERROR, moduleInfo_errorHandler, false, 0, true);

        moduleInfo.addEventListener(
            ModuleEvent.PROGRESS, moduleInfo_progressHandler, false, 0, true);

        moduleInfo.addEventListener(
            ModuleEvent.READY, moduleInfo_readyHandler, false, 0, true);
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function moduleInfo_errorHandler(event:ModuleEvent):void
    {
        var styleEvent:StyleEvent = new StyleEvent(
            StyleEvent.ERROR, event.bubbles, event.cancelable);
        styleEvent.bytesLoaded = event.bytesLoaded;
        styleEvent.bytesTotal = event.bytesTotal;
        styleEvent.errorText = event.errorText;
        dispatchEvent(styleEvent);
    }

    /**
     *  @private
     */
    private function moduleInfo_progressHandler(event:ModuleEvent):void
    {
        var styleEvent:StyleEvent = new StyleEvent(
            StyleEvent.PROGRESS, event.bubbles, event.cancelable);
        styleEvent.bytesLoaded = event.bytesLoaded;
        styleEvent.bytesTotal = event.bytesTotal;
        dispatchEvent(styleEvent);
    }

    /**
     *  @private
     */
    private function moduleInfo_readyHandler(event:ModuleEvent):void
    {
        var styleEvent:StyleEvent = new StyleEvent(StyleEvent.COMPLETE);
        dispatchEvent(styleEvent);
    }
}

////////////////////////////////////////////////////////////////////////////////
//
//  Helper class: StyleModuleInfo
//
////////////////////////////////////////////////////////////////////////////////

/**
 *  @private
 */
class StyleModuleInfo
{
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor
     */
    public function StyleModuleInfo(module:IModuleInfo,
                                    readyHandler:Function,
                                    errorHandler:Function)
    {
        super();

        this.module = module;
        this.readyHandler = readyHandler;
        this.errorHandler = errorHandler;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  errorHandler
    //----------------------------------

    /**
     *  @private
     */
    public var errorHandler:Function;
    
    //----------------------------------
    //  readyHandler
    //----------------------------------

    /**
     *  @private
     */
    public var readyHandler:Function;
    
    //----------------------------------
    //  styleModule
    //----------------------------------

    /**
     *  @private
     */
    public var styleModule:IStyleModule;

    //----------------------------------
    //  module
    //----------------------------------

    /**
     *  @private
     */
    public var module:IModuleInfo
}
