////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls
{

import flash.display.DisplayObject;
import flash.events.MouseEvent;
import mx.core.ClassFactory;
import mx.core.EdgeMetrics;
import mx.core.FlexVersion;
import mx.core.IFlexDisplayObject;
import mx.core.mx_internal;
import mx.events.ChildExistenceChangedEvent;
import mx.events.FlexEvent;
import mx.events.ItemClickEvent;
import mx.styles.ISimpleStyleClient;
import mx.styles.StyleProxy;

use namespace mx_internal;

//--------------------------------------
//  Styles
//--------------------------------------

/**
 *  Number of pixels between the LinkButton controls in the horizontal direction.
 * 
 *  @default 8
 */
[Style(name="horizontalGap", type="Number", format="Length", inherit="no")]

/**
 *  Name of CSS style declaration that specifies the styles to use for the link
 *  button navigation items.
 * 
 *  @default ""
 */
[Style(name="linkButtonStyleName", type="String", inherit="no")]

/**
 *  Number of pixels between the bottom border and the LinkButton controls.
 * 
 *  @default 2
 */
[Style(name="paddingBottom", type="Number", format="Length", inherit="no")]

/**
 *  Number of pixels between the top border and the LinkButton controls.
 * 
 *  @default 2
 */
[Style(name="paddingTop", type="Number", format="Length", inherit="no")]

/**
 *  Color of links as you roll the mouse pointer over them.
 *  The default value is based on the current <code>themeColor</code>.
 * 
 *  @default 0xEEFEE6 (light green)
 */
[Style(name="rollOverColor", type="uint", format="Color", inherit="yes")]

/**
 *  Background color of the LinkButton control as you press it.
 * 
 *  @default 0xCDFFC1
 */
[Style(name="selectionColor", type="uint", format="Color", inherit="yes")]

/**
 *  Separator color used by the default separator skin.
 * 
 *  @default 0xC4CCCC
 */
[Style(name="separatorColor", type="uint", format="Color", inherit="yes")]

/**
 *  Seperator symbol between LinkButton controls in the LinkBar. 
 * 
 *  @default mx.skins.halo.LinkSeparator
 */
[Style(name="separatorSkin", type="Class", inherit="no")]

/**
 *  Separator pixel width, in pixels.
 * 
 *  @default 1
 */
[Style(name="separatorWidth", type="Number", format="Length", inherit="yes")]

/**
 *  Text color of the link as you move the mouse pointer over it.
 * 
 *  @default 0x2B333C
 */
[Style(name="textRollOverColor", type="uint", format="Color", inherit="yes")]

/**
 *  Text color of the link as you press it.
 * 
 *  @default 0x000000
 */
[Style(name="textSelectedColor", type="uint", format="Color", inherit="yes")]

/**
 *  Number of pixels between children in the vertical direction.
 * 
 *  @default 8
 */
[Style(name="verticalGap", type="Number", format="Length", inherit="no")]

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="horizontalLineScrollSize", kind="property")]
[Exclude(name="horizontalPageScrollSize", kind="property")]
[Exclude(name="horizontalScrollBar", kind="property")]
[Exclude(name="horizontalScrollPolicy", kind="property")]
[Exclude(name="horizontalScrollPosition", kind="property")]
[Exclude(name="maxHorizontalScrollPosition", kind="property")]
[Exclude(name="maxVerticalScrollPosition", kind="property")]
[Exclude(name="verticalLineScrollSize", kind="property")]
[Exclude(name="verticalPageScrollSize", kind="property")]
[Exclude(name="verticalScrollBar", kind="property")]
[Exclude(name="verticalScrollPolicy", kind="property")]
[Exclude(name="verticalScrollPosition", kind="property")]

[Exclude(name="scroll", kind="event")]
[Exclude(name="click", kind="event")]

[Exclude(name="horizontalScrollBarStyleName", kind="style")]
[Exclude(name="verticalScrollBarStyleName", kind="style")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[DefaultProperty("dataProvider")]

[IconFile("LinkBar.png")]

[MaxChildren(0)]

/**
 *  A LinkBar control defines a horizontal or vertical row of LinkButton controls
 *  that designate a series of link destinations.
 *  You typically use a LinkBar control to control
 *  the active child container of a ViewStack container,
 *  or to create a stand-alone set of links.
 *
 *  <p>The LinkBar control creates LinkButton controls based on the value of 
 *  its <code>dataProvider</code> property. 
 *  Even though LinkBar is a subclass of Container, do not use methods such as 
 *  <code>Container.addChild()</code> and <code>Container.removeChild()</code> 
 *  to add or remove LinkButton controls. 
 *  Instead, use methods such as <code>addItem()</code> and <code>removeItem()</code> 
 *  to manipulate the <code>dataProvider</code> property. 
 *  The LinkBar control automatically adds or removes the necessary children based on 
 *  changes to the <code>dataProvider</code> property.</p>
 *
 *  <p>A LinkBar control has the following default characteristics:</p>
 *  <table class="innertable">
 *     <tr>
 *        <th>Characteristic</th>
 *        <th>Description</th>
 *     </tr>
 *     <tr>
 *        <td>Preferred size</td>
 *        <td>A width wide enough to contain all label text, plus any padding and separators, and the height of the tallest child.</td>
 *     </tr>
 *     <tr>
 *        <td>Control resizing rules</td>
 *        <td>LinkBar controls do not resize by default. Specify percentage sizes if you want your LinkBar to resize based on the size of its parent container.</td>
 *     </tr>
 *     <tr>
 *        <td>Padding</td>
 *        <td>2 pixels for the top, bottom, left, and right properties.</td>
 *     </tr>
 *  </table>
 *
 *  @mxml
 *  <p>The <code>&lt;mx:LinkBar&gt;</code> tag inherits all of the tag
 *  attributes of its superclass, and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:LinkBar
 *    <b>Properties</b>
 *    selectedIndex="-1"
 *  
 *    <b>Styles</b>
 *    linkButtonStyleName=""
 *    horizontalGap="8"
 *    paddingBottom="2"
 *    paddingTop="2"
 *    rollOverColor="0xEEFEE6"
 *    selectionColor="0xCDFFC1"
 *    separatorColor="<i>No default</i>"
 *    separatorSkin="0x000000"
 *    separatorWidth="1"
 *    textRollOverColor="0x2B333C"
 *    textSelectedColor="0x000000"
 *    verticalGap="8"
 *    &gt;
 *    ...
 *      <i>child tags</i>
 *    ...
 *  &lt;/mx:LinkBar&gt;
 *  </pre>
 *
 *  @includeExample examples/LinkBarExample.mxml
 *
 *  @see mx.controls.NavBar
 *  @see mx.containers.ViewStack
 *  @see mx.controls.LinkButton
 *  @see mx.controls.ToggleButtonBar
 *  @see mx.controls.ButtonBar
 */
public class LinkBar extends NavBar
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private static const SEPARATOR_NAME:String = "_separator";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function LinkBar()
    {
        super();

        navItemFactory = new ClassFactory(LinkButton);

        addEventListener(MouseEvent.CLICK, defaultClickHandler);
        addEventListener(ChildExistenceChangedEvent.CHILD_REMOVE, 
                         childRemoveHandler);
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  selectedIndex
    //----------------------------------

    /**
     *  @private
     *  Storage for the selectedIndex property.
     */
    private var _selectedIndex:int = -1;

    /**
     *  @private
     */
    private var _selectedIndexChanged:Boolean = false;

    [Bindable("valueCommit")]
    [Inspectable(category="General")]

    /**
     *  The index of the last selected LinkButton control if the LinkBar 
     *  control uses a ViewStack container as its data provider.
     * 
     *  @default -1
     */
    override public function get selectedIndex():int
    {
        return super.selectedIndex;
    }

    /**
     *  @private
     */
    override public function set selectedIndex(value:int):void
    {
        if (value == selectedIndex)
            return;

        _selectedIndex = value;
        
        _selectedIndexChanged = true;

        invalidateProperties();

        dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @inheritDoc
     */
    override public function styleChanged(styleProp:String):void
    {
        super.styleChanged(styleProp);
        
        var navItemStyleName:Object;
        
        if (styleProp == "styleName" && FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
        {
            navItemStyleName = this;
        }
        else if (styleProp == "linkButtonStyleName" && FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)
        {
            navItemStyleName = getStyle("linkButtonStyleName");
        }
        
        if (navItemStyleName)
        {
            var n:int = numChildren;
            for (var i:int = 0; i < n; i++)
            {               
                LinkButton(getChildAt(i)).styleName = navItemStyleName;
            }
        }
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties();

        if (_selectedIndexChanged)
        {
            hiliteSelectedNavItem(_selectedIndex);

            // Update parent index.
            super.selectedIndex = _selectedIndex;

            _selectedIndexChanged = false;
        }
    }

    /**
     *  Responds to size changes by setting the positions and sizes
     *  of this LinkBar control's children. 
     *  For more information about the <code>updateDisplayList()</code> method,
     *  see the <code>UIComponent.updateDisplayList()</code> method.
     *
     *  <p>The <code>LinkBar.updateDisplayList()</code> method first calls
     *  the <code>Box.updateDisplayList()</code> method to position the LinkButton controls.
     *  For more details, see the <code>Box.updateDisplayList()</code> method.
     *  After laying out the LinkButton controls, the separators are positioned
     *  between them.</p>
     *
     *  @param unscaledWidth Specifies the width of the component, in pixels,
     *  of the component's coordinates, regardless of the value of the
     *  <code>scaleX</code> property of the component.
     *
     *  @param unscaledHeight Specifies the height of the component, in pixels,
     *  in the component's coordinates, regardless of the value of the
     *  <code>scaleY</code> property of the component.
     * 
     *  @see mx.core.UIComponent#updateDisplayList()
     *  @see mx.containers.Box#updateDisplayList()
     * 
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        // The super method will lay out the Links.
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        var vm:EdgeMetrics = viewMetricsAndPadding;

        var horizontalGap:Number = getStyle("horizontalGap");
        var verticalGap:Number = getStyle("verticalGap");

        var separatorHeight:Number = unscaledHeight - (vm.top + vm.bottom);
        var separatorWidth:Number = unscaledWidth - (vm.left + vm.right);

        // Lay out the separators.
        var n:int = numChildren;
        for (var i:int = 0; i < n; i++)
        {
            var child:IFlexDisplayObject = IFlexDisplayObject(getChildAt(i));

            var separator:IFlexDisplayObject = IFlexDisplayObject(
                rawChildren.getChildByName(SEPARATOR_NAME + i));

            if (separator)
            {
                separator.visible = false;

                // The 0th separator is to the left of the first link.
                // It should always be invisible, and doesn't need
                // to be laid out.
                if (i == 0)
                    continue;

                if (isVertical())
                {
                    separator.move(vm.left, child.y - verticalGap);
                    separator.setActualSize(separatorWidth, verticalGap);

                    // The separators don't get clipped.
                    // (In general, chrome elements
                    // don't get automatically clipped.)
                    // So show a separator only if it is completely visible.
                    if (separator.y + separator.height <
                        unscaledHeight - vm.bottom)
                    {
                        separator.visible = true;
                    }
                }
                else
                {
                    separator.move(child.x - horizontalGap, vm.top);
                    separator.setActualSize(horizontalGap, separatorHeight);

                    if (separator.x + separator.width <
                        unscaledWidth - vm.right)
                    {
                        separator.visible = true;
                    }
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: NavBar
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function createNavItem(
                                        label:String,
                                        icon:Class = null):IFlexDisplayObject
    {
        // Create the new LinkButton.

        var newLink:Button = Button(navItemFactory.newInstance());

        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
        {
            newLink.styleName = this;
        }
        else
        {
            var linkButtonStyleName:String = getStyle("linkButtonStyleName");
            if (linkButtonStyleName)
                newLink.styleName = linkButtonStyleName;
        }
         
        if (label && label.length > 0)
            newLink.label = label;
        else
            newLink.label = " ";

        if (icon)
            newLink.setStyle("icon", icon);

        addChild(newLink);

        newLink.addEventListener(MouseEvent.CLICK, clickHandler);

        // Create the new separator to the left of the LinkButton.

        var separatorClass:Class = Class(getStyle("separatorSkin"));
        var separator:DisplayObject = DisplayObject(new separatorClass());

        separator.name = SEPARATOR_NAME + (numChildren - 1);
        if (separator is ISimpleStyleClient)
            ISimpleStyleClient(separator).styleName = this;

        rawChildren.addChild(separator);
        
        return newLink;
    }

    /**
     *  @private
     */
    override protected function hiliteSelectedNavItem(index:int):void
    {
        var child:Button;

        // Un-hilite the current selection.
        if (selectedIndex != -1 && selectedIndex < numChildren)
        {
            child = Button(getChildAt(selectedIndex));
            child.enabled = true;
        }

        // Set new index.
        super.selectedIndex = index;

        // Hilite the new selection.
        child = Button(getChildAt(selectedIndex));
        child.enabled = false;
    }

    /**
     *  @private
     */
    override protected function resetNavItems():void
    {
        // Reset the index values and selection state.
        var n:int = numChildren;
        for (var i:int = 0; i < n; i++)
        {
            var child:Button = Button(getChildAt(i));
            child.enabled = !(i == selectedIndex);
        }

        invalidateDisplayList();
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function childRemoveHandler(event:ChildExistenceChangedEvent):void
    {
        var child:DisplayObject = event.relatedObject;
        var index:int = getChildIndex(child);
        var separator:DisplayObject =
            rawChildren.getChildByName(SEPARATOR_NAME + index);
        rawChildren.removeChild(separator);

        // Shuffle the separators down.
        var n:int = numChildren - 1;
        for (var i:int = index; i < n; i++)
        {
            rawChildren.getChildByName(SEPARATOR_NAME + (i + 1)).name =
                SEPARATOR_NAME + i;
        }
    
    }

    /**
     *  @private
     */
    private function defaultClickHandler(event:MouseEvent):void
    {
        // We do not want to propagate a MouseEvent.CLICK event up.
        if (!(event is ItemClickEvent))
            event.stopImmediatePropagation();
    }
    
    //--------------------------------------------------------------------------
    //
    //  Overridden event handlers: NavBar
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     */
    override protected function clickHandler(event:MouseEvent):void
    {
        var index:int = getChildIndex(Button(event.currentTarget));

        if (targetStack)
        {
            if (index == selectedIndex)
                hiliteSelectedNavItem(-1);
                
            else
                hiliteSelectedNavItem(index);
        }
        
        super.clickHandler(event);
    }
    
}

}
