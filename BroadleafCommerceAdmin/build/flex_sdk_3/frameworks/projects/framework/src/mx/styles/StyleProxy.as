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

package mx.styles
{

import mx.core.mx_internal;
import mx.core.FlexVersion;

use namespace mx_internal;

/**
 *  Wraps an object that implements the IStyleClient interface. This interface
 *  supports a <code>filterMap</code> property that contains style-source/style-destination pairs.
 * 
 *  @see mx.styles.IStyleClient
 */
public class StyleProxy implements IStyleClient
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *  
     *  @param source The object that implements the IStyleClient interface.
     *  @param filterMap The set of styles to pass from the source to the subcomponent.
     */
    public function StyleProxy(source:IStyleClient, filterMap:Object)
    {
        super();
        
        this.filterMap = filterMap;
        this.source = source;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  className
    //----------------------------------

    /**
     *  @copy mx.styles.IStyleClient#className
     */
    public function get className():String
    {
        return _source.className;
    }
    
    //----------------------------------
    //  filterMap
    //----------------------------------

    /**
     *  @private
     *  Storage for the filterMap property.
     */
    private var _filterMap:Object;
    
    /**
     *  A set of string pairs. The first item of the string pair is the name of the style 
     *  in the source component. The second item of the String pair is the name of the style 
     *  in the subcomponent. With this object, you can map a particular style in the parent component 
     *  to a different style in the subcomponent. This is useful if both the parent 
     *  component and the subcomponent share the same style, but you want to be able to 
     *  control the values seperately.
     */
    public function get filterMap():Object
    {
        return FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0 ?
               null :
               _filterMap;
    }
    
    /**
     *  @private
     */
    public function set filterMap(value:Object):void
    {
        _filterMap = value;
    }
    
    //----------------------------------
    //  inheritingStyles
    //----------------------------------

    /**
     *  @copy mx.styles.IStyleClient#inheritingStyles
     */
    public function get inheritingStyles():Object
    {
        return _source.inheritingStyles;
    }
    
    /**
     *  @private
     */
    public function set inheritingStyles(value:Object):void
    {
        // This should never happen 
    }
    
    //----------------------------------
    //  nonInheritingStyles
    //----------------------------------

    /**
     *  @copy mx.styles.IStyleClient#nonInheritingStyles
     */
    public function get nonInheritingStyles():Object
    {
        return FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0 ?
               _source.nonInheritingStyles :
               null; // This will always need to get reconstructed
    }
    
    /**
     *  @private
     */
    public function set nonInheritingStyles(value:Object):void
    {
        // This should never happen
    }
    
    //----------------------------------
    //  source
    //----------------------------------

    /**
     *  @private
     *  Storage for the filterMap property.
     */
    private var _source:IStyleClient;
    
    /**
     *  The object that implements the IStyleClient interface. This is the object
     *  that is being proxied.
     */
    public function get source():IStyleClient
    {
        return _source;
    }
    
    /**
     *  @private
     */
    public function set source(value:IStyleClient):void
    {
        _source = value;
    }
    
    //----------------------------------
    //  styleDeclaration
    //----------------------------------

    /**
     *  @copy mx.styles.IStyleClient#styleDeclaration
     */
    public function get styleDeclaration():CSSStyleDeclaration
    {
        return _source.styleDeclaration;
    }
    
    /**
     *  @private
     */
    public function set styleDeclaration(value:CSSStyleDeclaration):void
    {
        _source.styleDeclaration = styleDeclaration;
    }
    
    //----------------------------------
    //  styleName
    //----------------------------------

    /**
     *  @copy mx.styles.ISimpleStyleClient#styleName
     */
    public function get styleName():Object
    {
        if (_source.styleName is IStyleClient)
            return new StyleProxy(IStyleClient(_source.styleName), filterMap);
        else
            return _source.styleName;
    }
    
    /**
     *  @private
     */
    public function set styleName(value:Object):void
    {
        _source.styleName = value;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @copy mx.styles.ISimpleStyleClient#styleChanged()
     */
    public function styleChanged(styleProp:String):void
    {
        return _source.styleChanged(styleProp);
    }
    
    /**
     *  @copy mx.styles.IStyleClient#getStyle()
     */
    public function getStyle(styleProp:String):*
    {
        return _source.getStyle(styleProp);
    }
    
    /**
     *  @copy mx.styles.IStyleClient#setStyle()
     */
    public function setStyle(styleProp:String, newValue:*):void
    {
        _source.setStyle(styleProp, newValue);
    }
    
    /**
     *  @copy mx.styles.IStyleClient#clearStyle()
     */
    public function clearStyle(styleProp:String):void
    {
        _source.clearStyle(styleProp);
    }
    
    /**
     *  @copy mx.styles.IStyleClient#getClassStyleDeclarations()
     */
    public function getClassStyleDeclarations():Array
    {
        return _source.getClassStyleDeclarations();
    }
    
    /**
     *  @copy mx.styles.IStyleClient#notifyStyleChangeInChildren()
     */
    public function notifyStyleChangeInChildren(styleProp:String,
                                                recursive:Boolean):void
    {
        return _source.notifyStyleChangeInChildren(styleProp, recursive);
    }
    
    /**
     *  @copy mx.styles.IStyleClient#regenerateStyleCache()
     */
    public function regenerateStyleCache(recursive:Boolean):void
    {
        _source.regenerateStyleCache(recursive);
        return;
    }
    
    /**
     *  @copy mx.styles.IStyleClient#registerEffects()
     */
    public function registerEffects(effects:Array):void
    {
        return _source.registerEffects(effects);
    }
}

}
