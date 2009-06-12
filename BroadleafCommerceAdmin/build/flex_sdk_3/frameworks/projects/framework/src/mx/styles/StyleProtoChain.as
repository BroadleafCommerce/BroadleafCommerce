////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.styles
{

import flash.display.DisplayObject;
import mx.core.UIComponent;
import mx.core.IUITextField;
import mx.core.mx_internal;
import mx.styles.IStyleClient;
import mx.styles.StyleProxy;

use namespace mx_internal;

[ExcludeClass]

/**
 *  @private
 *  This is an all-static class with methods for building the protochains
 *  that Flex uses to look up CSS style properties.
 */
public class StyleProtoChain
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  If the styleName property points to a UIComponent, then we search
	 *  for stylable properties in the following order:
	 *  
	 *  1) Look for inline styles on this object
	 *  2) Look for inline styles on the styleName object
	 *  3) Look for class selectors on the styleName object
	 *  4) Look for type selectors on the styleName object
	 *  5) Look for type selectors on this object
	 *  6) Follow the usual search path for the styleName object
	 *  
	 *  If this object doesn't have any type selectors, then the
	 *  search path can be simplified to two steps:
	 *  
	 *  1) Look for inline styles on this object
	 *  2) Follow the usual search path for the styleName object
	 */
	public static function initProtoChainForUIComponentStyleName(
									obj:IStyleClient):void
	{
		var styleName:IStyleClient = IStyleClient(obj.styleName);
		var target:DisplayObject = obj as DisplayObject;
		
		// Push items onto the proto chain in reverse order, beginning with
		// 6) Follow the usual search path for the styleName object
		var nonInheritChain:Object = styleName.nonInheritingStyles;
		if (!nonInheritChain || nonInheritChain == UIComponent.STYLE_UNINITIALIZED)
        {
			nonInheritChain = StyleManager.stylesRoot;

            if (nonInheritChain.effects)
                obj.registerEffects(nonInheritChain.effects);
        }

		var inheritChain:Object = styleName.inheritingStyles;
		if (!inheritChain || inheritChain == UIComponent.STYLE_UNINITIALIZED)
			inheritChain = StyleManager.stylesRoot;

		// If there's no type selector on this object, then we can collapse
		// 6 steps to 2 (see above)
		var typeSelectors:Array = obj.getClassStyleDeclarations();
		var n:int = typeSelectors.length;
		
		// If we are a StyleProxy and we aren't building the protochain from
		// our type selectors, then we need to build the protochain from
		// the styleName since styleName.nonInheritingStyles is always null.
		if (styleName is StyleProxy)
		{	
			if (n == 0)
			{	
				// 4) Look for type selectors on the styleName object
				// 3) Look for class selectors on the styleName object
				// 2) Look for inline styles on the styleName object
				nonInheritChain = addProperties(nonInheritChain, styleName, false);
			}
			target = StyleProxy(styleName).source as DisplayObject;
		}
		
		for (var i:int = 0; i < n; i++)
		{
			var typeSelector:CSSStyleDeclaration = typeSelectors[i];

			// If there's no *inheriting* type selector on this object, then we
			// can still collapse 6 steps to 2 for the inheriting properties.

			// 5) Look for type selectors on this object
			inheritChain = typeSelector.addStyleToProtoChain(inheritChain, target);	

			// 4) Look for type selectors on the styleName object
			// 3) Look for class selectors on the styleName object
			// 2) Look for inline styles on the styleName object
			inheritChain = addProperties(inheritChain, styleName, true);

			// 5) Look for type selectors on this object
			nonInheritChain = typeSelector.addStyleToProtoChain(nonInheritChain, target);	

			// 4) Look for type selectors on the styleName object
			// 3) Look for class selectors on the styleName object
			// 2) Look for inline styles on the styleName object
			nonInheritChain = addProperties(nonInheritChain, styleName, false);

			if (typeSelector.effects)
				obj.registerEffects(typeSelector.effects);
		}
		
		// 1) Look for inline styles on this object
        
		obj.inheritingStyles =
			obj.styleDeclaration ? 
        	obj.styleDeclaration.addStyleToProtoChain(inheritChain, target) :
			inheritChain;
		
		obj.nonInheritingStyles =
			obj.styleDeclaration ? 
			obj.styleDeclaration.addStyleToProtoChain(nonInheritChain, target) :
			nonInheritChain;
	}
	
	/**
	 *  See the comment for the initProtoChainForUIComponentStyleName
	 *  function. The comment for that function includes a six-step
	 *  sequence. This sub-function implements the following pieces
	 *  of that sequence:
	 *  
	 *  2) Look for inline styles on the styleName object
	 *  3) Look for class selectors on the styleName object
	 *  4) Look for type selectors on the styleName object
	 *  
	 *   This piece is broken out as a separate function so that it
	 *  can be called recursively when the styleName object has a
	 *  styleName property is itself another UIComponent.
	 */
	private	static function addProperties(chain:Object, obj:IStyleClient,
										  bInheriting:Boolean):Object
	{
		// Only use a filter map if styleName is a StyleProxy and we are building the nonInheritingStyles chain
		var filterMap:Object = obj is StyleProxy && !bInheriting ? StyleProxy(obj).filterMap : null;
		
		// StyleProxy's usually have sources that are DisplayObject's, but a StyleProxy can also have 
		// another StyleProxy as it's source (Example: CalendarLayout's source is a StyleProxy for DateChooser, 
		// whose style is a StyleProxy for DateField)
		
		// The way we use target is a bit hacky, but we always assume that styles (if pointed to DisplayObjects)
		// are the parent (or atleast an ancestor), and we rely on this down the line (such as in 
		// DataGridColumn.addStyleToProtoChain)
		var curObj:IStyleClient = obj;
		while (curObj is StyleProxy)
		{
			curObj = StyleProxy(curObj).source;
		}
		var target:DisplayObject = curObj as DisplayObject;
		
		// 4) Add type selectors 
		var typeSelectors:Array = obj.getClassStyleDeclarations();
		var n:int = typeSelectors.length;
		for (var i:int = 0; i < n; i++)
		{
			var typeSelector:CSSStyleDeclaration = typeSelectors[i];
            chain = typeSelector.addStyleToProtoChain(chain, target, filterMap);

            if (typeSelector.effects)
                obj.registerEffects(typeSelector.effects);
		}

		// 3) Add class selectors
		var styleName:Object = obj.styleName;
		if (styleName)
		{
			var classSelector:CSSStyleDeclaration;
			
			if (typeof(styleName) == "object")
			{
				if (styleName is CSSStyleDeclaration)
				{
					// Get the style sheet referenced by the styleName property.
					classSelector = CSSStyleDeclaration(styleName);
				}
				else
				{				
					// If the styleName property is another UIComponent, then
					// recursively add type selectors, class selectors, and
					// inline styles for that UIComponent
					chain = addProperties(chain, IStyleClient(styleName),
										  bInheriting);
				}
			}
			else
			{
				// Get the style sheet referenced by the styleName property.
				classSelector =
					StyleManager.getStyleDeclaration("." + styleName);
			}

			if (classSelector)
			{
                chain = classSelector.addStyleToProtoChain(chain, target, filterMap);	

				if (classSelector.effects)
					obj.registerEffects(classSelector.effects);
			}
		}		

		// 2) Add inline styles 
        if (obj.styleDeclaration)
            chain = obj.styleDeclaration.addStyleToProtoChain(chain, target, filterMap);

		return chain;
	}

	/**
	 *  @private
	 */
	public static function initTextField(obj:IUITextField):void
	{
		// TextFields never have any inline styles or type selector, so
		// this is an optimized version of the initObject function (above)
		var styleName:Object = obj.styleName;
		var classSelector:CSSStyleDeclaration;
		 
		if (styleName)
		{
			if (typeof(styleName) == "object")
			{
				if (styleName is CSSStyleDeclaration)
				{
					// Get the style sheet referenced by the styleName property.
					classSelector = CSSStyleDeclaration(styleName);
				}
				else if (styleName is StyleProxy)
				{
					obj.inheritingStyles =
						IStyleClient(styleName).inheritingStyles;
						
					obj.nonInheritingStyles = addProperties(StyleManager.stylesRoot, IStyleClient(styleName), false);
					
					return;
				}
				else
				{				
					// styleName points to a UIComponent, so just set
					// this TextField's proto chains to be the same
					// as that UIComponent's proto chains.
					
					obj.inheritingStyles =
						IStyleClient(styleName).inheritingStyles;
					obj.nonInheritingStyles =
						IStyleClient(styleName).nonInheritingStyles;
					return;
				}
			}
			else
			{
				// Get the style sheet referenced by the styleName property
				classSelector =
					StyleManager.getStyleDeclaration("." + styleName);
			}
		}
		
		// To build the proto chain, we start at the end and work forward.
		// We'll start by getting the tail of the proto chain, which is:
		//  - for non-inheriting styles, the global style sheet
		//  - for inheriting styles, my parent's style object
		var inheritChain:Object = IStyleClient(obj.parent).inheritingStyles;
		var nonInheritChain:Object = StyleManager.stylesRoot;
		if (!inheritChain)
			inheritChain = StyleManager.stylesRoot;
				
		// Next is the class selector
		if (classSelector)
		{
            inheritChain =
				classSelector.addStyleToProtoChain(inheritChain, DisplayObject(obj));

            nonInheritChain =
				classSelector.addStyleToProtoChain(nonInheritChain, DisplayObject(obj));	
		}

        obj.inheritingStyles = inheritChain;
        obj.nonInheritingStyles = nonInheritChain;
	}
}

}
